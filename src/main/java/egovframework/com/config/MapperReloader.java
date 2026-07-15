package egovframework.com.config;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MyBatis 매퍼 XML 자동 반영기(개발 편의).
 *
 * <p>MyBatis 는 기동 시 매퍼 XML 을 한 번 파싱해 {@link Configuration} 에 캐시하므로,
 * 운영 중 XML 을 수정해도 재기동 전에는 반영되지 않는다. 본 컴포넌트는 소스 매퍼
 * 디렉터리를 주기적으로 감시하여 변경이 감지되면 {@link Configuration} 의 매퍼 관련
 * 캐시를 비우고 소스 XML 을 다시 파싱한다.</p>
 *
 * <p>감시 디렉터리({@code ops.mapper.reload.dir}, 기본 {@code src/main/resources/egovframework/mapper})
 * 가 존재할 때만 동작한다. 실제 배포(WAR) 환경에서는 해당 소스 경로가 없으므로 자동으로
 * 비활성화되어 운영에는 영향이 없다. 강제 비활성화는 {@code ops.mapper.reload.enabled=false}.</p>
 */
@Component
public class MapperReloader implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(MapperReloader.class);

    /** Configuration 에서 매퍼 단위로 비워야 하는 캐시 필드들 */
    private static final String[] CLEAR_MAPS = {
            "mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments"
    };

    private final SqlSessionFactory sqlSessionFactory;

    @Value("${ops.mapper.reload.enabled:true}")
    private boolean enabled;

    @Value("${ops.mapper.reload.dir:src/main/resources/egovframework/mapper}")
    private String watchDir;

    @Value("${ops.mapper.reload.interval-ms:2000}")
    private long intervalMs;

    private volatile boolean running = false;
    private Thread worker;

    public MapperReloader(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        start();
    }

    /** 감시 스레드 시작(컨텍스트 다중 갱신에도 1회만) */
    private synchronized void start() {
        if (running) {
            return;
        }
        if (!enabled) {
            log.info("MyBatis 매퍼 자동반영 비활성화(ops.mapper.reload.enabled=false)");
            return;
        }
        File dir = resolveWatchDir();
        if (dir == null) {
            log.info("MyBatis 매퍼 자동반영 비활성화 - 소스 매퍼 디렉터리 없음(실배포 환경): {}", watchDir);
            return;
        }
        running = true;
        worker = new Thread(() -> watchLoop(dir), "mapper-reloader");
        worker.setDaemon(true);
        worker.start();
        log.info("MyBatis 매퍼 자동반영 활성화 - 감시: {} (간격 {}ms)", dir.getAbsolutePath(), intervalMs);
    }

    /**
     * 감시할 소스 매퍼 디렉터리를 찾는다. 설정값(상대경로)이 현재 작업 디렉터리에서
     * 안 보이면(예: cargo 의 톰캣 작업경로) 상위 디렉터리로 올라가며 탐색한다.
     * 끝까지 못 찾으면 null(=실배포 환경으로 보고 비활성화).
     */
    private File resolveWatchDir() {
        File configured = new File(watchDir);
        if (configured.isDirectory()) {
            return configured.getAbsoluteFile();
        }
        if (!configured.isAbsolute()) {
            File cur = new File(System.getProperty("user.dir", ".")).getAbsoluteFile();
            while (cur != null) {
                File candidate = new File(cur, watchDir);
                if (candidate.isDirectory()) {
                    return candidate;
                }
                cur = cur.getParentFile();
            }
        }
        // 실배포(WAR) 환경 : 소스 경로가 없으면 전개된 클래스패스의 매퍼 디렉터리
        // (WEB-INF/classes/egovframework/mapper) 를 감시 → 배포본 XML 수정도 자동 반영
        File cp = classpathMapperDir();
        if (cp != null && cp.isDirectory()) {
            return cp;
        }
        return null;
    }

    /** 클래스패스에 전개된 매퍼 루트 디렉터리(WEB-INF/classes/egovframework/mapper) 반환(없으면 null) */
    private File classpathMapperDir() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        java.net.URL url = cl.getResource("egovframework/mapper");
        if (url != null && "file".equals(url.getProtocol())) {
            try {
                return new File(url.toURI());
            } catch (Exception ignore) {
                return new File(url.getPath());
            }
        }
        return null;
    }

    private void watchLoop(File dir) {
        Map<String, Long> stamps = snapshot(dir);
        while (running) {
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            Map<String, Long> now = snapshot(dir);
            if (!now.equals(stamps)) {
                List<String> changed = diff(stamps, now);
                stamps = now;
                try {
                    reloadAll(dir);
                    log.info("MyBatis 매퍼 재반영 완료 {} - 변경 {}건", changed, changed.size());
                } catch (Exception e) {
                    log.warn("MyBatis 매퍼 재반영 실패(수정 후 다시 저장하세요): {}", e.getMessage());
                }
            }
        }
    }

    /** 디렉터리 하위 *.xml 의 경로→수정시각 스냅샷 */
    private Map<String, Long> snapshot(File dir) {
        Map<String, Long> map = new HashMap<>();
        collect(dir, map);
        return map;
    }

    private void collect(File dir, Map<String, Long> map) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                collect(f, map);
            } else if (f.getName().endsWith(".xml")) {
                map.put(f.getAbsolutePath(), f.lastModified());
            }
        }
    }

    private List<String> diff(Map<String, Long> before, Map<String, Long> after) {
        List<String> changed = new ArrayList<>();
        for (Map.Entry<String, Long> e : after.entrySet()) {
            if (!e.getValue().equals(before.get(e.getKey()))) {
                changed.add(new File(e.getKey()).getName());
            }
        }
        return changed;
    }

    /**
     * Configuration 의 매퍼 캐시를 비우고 소스 매퍼 XML 전체를 다시 파싱한다.
     * (매퍼가 namespace 단위로 독립적이므로 전체 재로딩으로 단순·안전하게 처리)
     */
    @SuppressWarnings("unchecked")
    private synchronized void reloadAll(File dir) throws Exception {
        Configuration cfg = sqlSessionFactory.getConfiguration();

        for (String fieldName : CLEAR_MAPS) {
            Map<?, ?> map = (Map<?, ?>) readField(cfg, fieldName);
            map.clear();
        }
        Set<String> loaded = (Set<String>) readField(cfg, "loadedResources");
        loaded.clear();

        List<File> xmls = new ArrayList<>();
        listXml(dir, xmls);
        for (File xml : xmls) {
            try (InputStream is = Files.newInputStream(xml.toPath())) {
                // 소스 파일을 직접 파싱(배포본이 아닌 수정된 내용 반영). resource 는 고유 식별자.
                XMLMapperBuilder builder = new XMLMapperBuilder(
                        is, cfg, xml.getAbsolutePath(), cfg.getSqlFragments());
                builder.parse();
            }
        }
    }

    private void listXml(File dir, List<File> out) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                listXml(f, out);
            } else if (f.getName().endsWith(".xml")) {
                out.add(f);
            }
        }
    }

    private Object readField(Configuration cfg, String name) throws Exception {
        Field field = Configuration.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(cfg);
    }

    @Override
    public void destroy() {
        running = false;
        if (worker != null) {
            worker.interrupt();
        }
    }
}

package egovframework.com.config;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

/**
 * Git 자동배포 데모 부트스트랩 (개발 프로파일 한정).
 *
 * <p>외부 인프라 없이 자동배포 기능을 즉시 검증할 수 있도록, 로컬 파일시스템에
 * 데모 git 저장소(태그 v1.0.0 / v1.4.2 포함)를 생성하고 응용시스템(SYS001)에
 * git 설정(URL/브랜치/배포경로/스크립트)을 연결한다. 운영(prod) 프로파일에서는
 * 동작하지 않으며, 관리자가 화면에서 사내 git 저장소를 직접 설정한다.</p>
 */
@Component
@Profile("!prod")
@Order(20)
public class DeployDemoBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DeployDemoBootstrap.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${ops.deploy.workspace:${java.io.tmpdir}/egov-ops/deploy}")
    private String workspace;

    public DeployDemoBootstrap(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            File base = new File(workspace);
            base.mkdirs();
            File demoSrc = new File(base, "_demo-src");

            String branch = "master";
            if (!new File(demoSrc, ".git").exists()) {
                demoSrc.mkdirs();
                try (Git git = Git.init().setDirectory(demoSrc).call()) {
                    // 환경 전역 git 설정(gpg.format=ssh, commit.gpgsign)으로 인한 커밋 실패 방지:
                    // 로컬 저장소 설정으로 서명 비활성화 및 식별정보 지정
                    org.eclipse.jgit.lib.StoredConfig cfg = git.getRepository().getConfig();
                    cfg.setBoolean("commit", null, "gpgsign", false);
                    cfg.setBoolean("tag", null, "gpgsign", false);
                    cfg.setString("gpg", null, "format", "openpgp");
                    cfg.setString("user", null, "name", "egov-ops");
                    cfg.setString("user", null, "email", "ops@egov.go.kr");
                    cfg.save();

                    Files.writeString(new File(demoSrc, "app.txt").toPath(),
                            "egov-ops demo application\nversion=1.0.0\n");
                    git.add().addFilepattern(".").call();
                    git.commit().setMessage("init v1.0.0").setSign(false)
                            .setAuthor("egov-ops", "ops@egov.go.kr")
                            .setCommitter("egov-ops", "ops@egov.go.kr").call();
                    git.tag().setName("v1.0.0").setAnnotated(false).call();

                    Files.writeString(new File(demoSrc, "app.txt").toPath(),
                            "egov-ops demo application\nversion=1.4.2\n- 민원포털 성능개선(인덱스/커넥션풀)\n");
                    git.add().addFilepattern(".").call();
                    git.commit().setMessage("release v1.4.2").setSign(false)
                            .setAuthor("egov-ops", "ops@egov.go.kr")
                            .setCommitter("egov-ops", "ops@egov.go.kr").call();
                    git.tag().setName("v1.4.2").setAnnotated(false).call();

                    branch = git.getRepository().getBranch();
                }
                log.info("[deploy-demo] 데모 git 저장소 생성 : {}", demoSrc.getAbsolutePath());
            } else {
                try (Git git = Git.open(demoSrc)) {
                    branch = git.getRepository().getBranch();
                }
            }

            // SYS001 에 git 설정이 비어있으면 데모 저장소로 연결
            Integer cnt = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM OPS_SYSTEM WHERE SYS_ID = 'SYS001' AND (GIT_URL IS NULL OR GIT_URL = '')",
                    Integer.class);
            if (cnt != null && cnt > 0) {
                String gitUrl = demoSrc.toURI().toString(); // file:/... URL
                String deployPath = new File(base, "SYS001").getAbsolutePath();
                String script = "echo \"[deploy] $SYS_ID ver=$VER ref=$REF type=$DEPLOY_TYPE\"; "
                        + "echo '--- 배포 산출물 ---'; cat app.txt 2>/dev/null; echo '[deploy] 완료'";
                jdbcTemplate.update(
                        "UPDATE OPS_SYSTEM SET GIT_URL = ?, GIT_BRANCH = ?, DEPLOY_PATH = ?, DEPLOY_SCRIPT = ? WHERE SYS_ID = 'SYS001'",
                        gitUrl, branch, deployPath, script);
                log.info("[deploy-demo] SYS001 git 배포 설정 연결 : {} ({})", gitUrl, branch);
            }
        } catch (Exception e) {
            log.warn("[deploy-demo] 데모 배포 저장소 초기화 실패(무시 가능): {}", e.getMessage());
        }
    }
}

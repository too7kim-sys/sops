package egovframework.com.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 초기 데이터 후처리 부트스트랩.
 *
 * <p>data.sql 로 적재된 운영자 계정의 평문 비밀번호를 BCrypt 로 일괄 암호화한다.
 * 이미 암호화된(접두사 {@code $2}) 계정은 건너뛰므로 멱등하게 동작한다.
 * 루트 컨텍스트 기동(ContextRefreshedEvent) 시 1회 수행한다.</p>
 */
@Component
public class DataBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public DataBootstrap(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 루트 컨텍스트에서만 1회 수행
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        List<Map<String, Object>> users =
                jdbcTemplate.queryForList("SELECT USER_ID, PASSWORD FROM OPS_USER");
        for (Map<String, Object> user : users) {
            String userId = (String) user.get("USER_ID");
            String password = (String) user.get("PASSWORD");
            if (password != null && !password.startsWith("$2")) {
                String encoded = passwordEncoder.encode(password);
                jdbcTemplate.update("UPDATE OPS_USER SET PASSWORD = ? WHERE USER_ID = ?", encoded, userId);
            }
        }
    }
}

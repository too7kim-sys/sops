package egovframework.com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 비동기 실행 설정.
 *
 * <p>장시간 소요될 수 있는 Git 자동배포를 논블로킹으로 수행하기 위해 비동기 실행을 활성화한다.</p>
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}

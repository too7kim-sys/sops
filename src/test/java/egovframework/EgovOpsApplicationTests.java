package egovframework;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 애플리케이션 컨텍스트 로드 스모크 테스트.
 *
 * <p>표준프레임워크 계층(Controller/Service/Mapper)·보안설정·MyBatis 매퍼·
 * DB 스키마/데이터 초기화가 정상적으로 구성되는지 검증한다.</p>
 */
@SpringBootTest
class EgovOpsApplicationTests {

    @Test
    void contextLoads() {
        // 컨텍스트 구동 성공 여부만 확인한다.
    }
}

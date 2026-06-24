package egovframework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 범정부 응용프로그램 운영관리시스템 (eGov-Ops).
 *
 * <p>전자정부 표준프레임워크의 계층형 아키텍처(Presentation - Business Logic - Persistence)와
 * 범정부 정보시스템 운영관리 매뉴얼의 "응용프로그램 표준운영절차"를 구현한 운영시스템이다.</p>
 *
 * @author eGov-Ops
 * @since 2026.06.24
 */
@SpringBootApplication
@MapperScan("egovframework.**.service.impl")
public class EgovOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgovOpsApplication.class, args);
    }
}

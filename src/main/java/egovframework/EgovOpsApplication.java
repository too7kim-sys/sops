package egovframework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 범정부 응용프로그램 운영관리시스템 (eGov-Ops).
 *
 * <p>전자정부 표준프레임워크의 계층형 아키텍처(Presentation - Business Logic - Persistence)와
 * 범정부 정보시스템 운영관리 매뉴얼의 "응용프로그램 표준운영절차"를 구현한 운영시스템이다.</p>
 *
 * <p>{@link SpringBootServletInitializer} 를 상속하여 내장 톰캣 실행(java -jar)과
 * 외부 톰캣(Tomcat 10.1+) WAR 배포를 모두 지원한다.</p>
 *
 * @author eGov-Ops
 * @since 2026.06.24
 */
@SpringBootApplication
@MapperScan("egovframework.**.service.impl")
public class EgovOpsApplication extends SpringBootServletInitializer {

    /** 외부 톰캣 배포 시 진입점 */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EgovOpsApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(EgovOpsApplication.class, args);
    }
}

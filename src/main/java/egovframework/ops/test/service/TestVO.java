package egovframework.ops.test.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 테스트관리 VO.
 *
 * <p>응용프로그램 표준운영절차의 테스트관리 단위정보. 테스트헤더와
 * 테스트케이스(1:N)를 함께 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TestVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 테스트 ID */
    private Long testId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 연계 변경 ID */
    private Long chgId;

    /** 테스트 제목 */
    private String title;

    /** 테스트 유형 (UNIT/INTEGRATION/PERFORMANCE/ACCEPTANCE) */
    private String testType;

    /** 테스트 유형명 (조인) */
    private String testTypeNm;

    /** 테스트 환경 (DEV/OPS) */
    private String testEnv;

    /** 테스트 환경명 (조인) */
    private String testEnvNm;

    /** 처리 상태 (PLANNED/TESTING/ANALYZED/CLOSED/FAILED) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 테스트 예정일 (YYYY-MM-DD) */
    private String planDt;

    /** 테스터 ID */
    private String testerId;

    /** 결과 요약 */
    private String resultSummary;

    /** 등록 일시 */
    private String regDt;

    /** 테스트케이스 목록 */
    private List<TestCaseVO> caseList;
}

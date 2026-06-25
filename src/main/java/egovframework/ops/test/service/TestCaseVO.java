package egovframework.ops.test.service;

import lombok.Data;

/**
 * 테스트케이스 VO.
 *
 * <p>테스트헤더(TestVO)에 종속되는 개별 테스트케이스. 케이스별 결과
 * (PASS/FAIL/NA)와 비고를 보관한다.</p>
 */
@Data
public class TestCaseVO {

    /** 케이스 ID */
    private Long caseId;

    /** 테스트 ID */
    private Long testId;

    /** 케이스명 */
    private String caseNm;

    /** 기대 결과 */
    private String expected;

    /** 케이스 결과 (PASS/FAIL/NA) */
    private String caseResult;

    /** 케이스 결과명 (조인) */
    private String caseResultNm;

    /** 비고 */
    private String remark;
}

package egovframework.ops.test.service;

import java.util.List;

/**
 * 테스트관리 서비스 인터페이스 (Business 계층).
 *
 * <p>응용프로그램 표준운영절차 — 테스트 계획/수행/분석/종결을 제공한다.</p>
 */
public interface EgovTestService {

    List<TestVO> selectTestList(TestVO searchVO);

    int selectTestListCnt(TestVO searchVO);

    /** 테스트 상세 (테스트케이스 포함) */
    TestVO selectTest(Long testId);

    /** 테스트 등록 (헤더 + 테스트케이스 N개) */
    void insertTest(TestVO vo);

    /** 테스트 처리 (상태/결과요약/테스터 갱신) */
    void processTest(TestVO vo);

    /** 테스트 삭제 */
    void deleteTest(Long testId);
}

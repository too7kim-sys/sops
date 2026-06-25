package egovframework.ops.test.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.test.service.EgovTestService;
import egovframework.ops.test.service.TestCaseVO;
import egovframework.ops.test.service.TestVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 테스트관리 서비스 구현체.
 *
 * <p>테스트헤더와 테스트케이스(1:N)를 함께 적재한다. 상태 전이 시
 * 결과요약/테스터 정보를 갱신한다(이력테이블 없음).</p>
 */
@Service("egovTestService")
public class EgovTestServiceImpl extends EgovAbstractServiceImpl implements EgovTestService {

    private final TestMapper testMapper;

    public EgovTestServiceImpl(TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    @Override
    public List<TestVO> selectTestList(TestVO searchVO) {
        return testMapper.selectTestList(searchVO);
    }

    @Override
    public int selectTestListCnt(TestVO searchVO) {
        return testMapper.selectTestListCnt(searchVO);
    }

    @Override
    public TestVO selectTest(Long testId) {
        TestVO vo = testMapper.selectTest(testId);
        if (vo != null) {
            vo.setCaseList(testMapper.selectTestCaseList(testId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertTest(TestVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("PLANNED");
        }

        // 빈 케이스(케이스명 공백) 제외
        List<TestCaseVO> cases = new ArrayList<>();
        if (vo.getCaseList() != null) {
            for (TestCaseVO c : vo.getCaseList()) {
                if (c != null && c.getCaseNm() != null && !c.getCaseNm().isBlank()) {
                    cases.add(c);
                }
            }
        }

        // 헤더 적재 (useGeneratedKeys 로 testId 획득)
        testMapper.insertTest(vo);

        // 케이스 적재
        for (TestCaseVO c : cases) {
            c.setTestId(vo.getTestId());
            testMapper.insertTestCase(c);
        }
        log.debug("테스트 등록 : TEST-{} ({}건)", vo.getTestId(), cases.size());
    }

    @Override
    @Transactional
    public void processTest(TestVO vo) {
        testMapper.updateTestProcess(vo);
        log.debug("테스트 처리 : TEST-{} -> {}", vo.getTestId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void deleteTest(Long testId) {
        testMapper.deleteTestCase(testId);
        testMapper.deleteTest(testId);
    }
}

package egovframework.ops.test.service.impl;

import egovframework.ops.test.service.TestCaseVO;
import egovframework.ops.test.service.TestVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 테스트관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface TestMapper {

    List<TestVO> selectTestList(TestVO searchVO);

    int selectTestListCnt(TestVO searchVO);

    TestVO selectTest(Long testId);

    List<TestCaseVO> selectTestCaseList(Long testId);

    void insertTest(TestVO vo);

    void insertTestCase(TestCaseVO caseVO);

    void updateTestProcess(TestVO vo);

    void deleteTest(Long testId);

    void deleteTestCase(Long testId);
}

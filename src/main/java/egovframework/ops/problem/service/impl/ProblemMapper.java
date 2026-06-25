package egovframework.ops.problem.service.impl;

import egovframework.ops.problem.service.KedbVO;
import egovframework.ops.problem.service.ProblemHisVO;
import egovframework.ops.problem.service.ProblemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 문제관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface ProblemMapper {

    List<ProblemVO> selectProblemList(ProblemVO searchVO);

    int selectProblemListCnt(ProblemVO searchVO);

    ProblemVO selectProblem(Long prbId);

    List<ProblemHisVO> selectProblemHisList(Long prbId);

    List<Map<String, Object>> selectProblemIncList(Long prbId);

    List<KedbVO> selectKedbList(Long prbId);

    int selectKedbCnt(Long prbId);

    void insertProblem(ProblemVO vo);

    void updateProblemProcess(ProblemVO vo);

    void insertProblemHis(ProblemHisVO hisVO);

    void insertProblemInc(@Param("prbId") Long prbId, @Param("incId") Long incId);

    void insertKedb(KedbVO vo);

    void deleteProblem(Long prbId);

    void deleteProblemHis(Long prbId);

    void deleteProblemInc(Long prbId);
}

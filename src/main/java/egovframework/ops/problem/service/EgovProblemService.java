package egovframework.ops.problem.service;

import java.util.List;

/**
 * 문제관리 서비스 인터페이스.
 *
 * <p>응용프로그램 표준운영절차 — 문제 등록/조회/처리(상태전이)/종결과
 * 연계장애·KEDB(알려진 오류) 관리를 제공한다.</p>
 */
public interface EgovProblemService {

    List<ProblemVO> selectProblemList(ProblemVO searchVO);

    int selectProblemListCnt(ProblemVO searchVO);

    /** 문제 상세 (처리이력 + 연계장애 + KEDB 포함) */
    ProblemVO selectProblem(Long prbId);

    /** 문제 등록 */
    void insertProblem(ProblemVO vo);

    /**
     * 문제 처리(상태전이).
     * 상태/근본원인/해결책/담당자를 갱신하고 처리이력을 적재한다.
     * RESOLVED 시 해결일시를 기록하고, CLOSED 시 KEDB가 없으면 자동 등록한다.
     */
    void processProblem(ProblemVO vo);

    /** 연계 장애 추가 */
    void linkIncident(Long prbId, Long incId);

    /** KEDB(알려진 오류) 추가 */
    void addKedb(KedbVO vo);

    /** 문제 삭제 (이력/연계 삭제 후 헤더 삭제) */
    void deleteProblem(Long prbId);
}

package egovframework.ops.problem.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.problem.service.EgovProblemService;
import egovframework.ops.problem.service.KedbVO;
import egovframework.ops.problem.service.ProblemHisVO;
import egovframework.ops.problem.service.ProblemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 문제관리 서비스 구현체.
 *
 * <p>문제 처리상태 전이 시 처리이력을 적재하고, 종결(CLOSED) 시 KEDB가 없으면
 * 근본원인/해결책을 알려진 오류로 자동 등록하여 지식 재사용을 지원한다.</p>
 */
@Service("egovProblemService")
public class EgovProblemServiceImpl extends EgovAbstractServiceImpl implements EgovProblemService {

    private final ProblemMapper problemMapper;

    public EgovProblemServiceImpl(ProblemMapper problemMapper) {
        this.problemMapper = problemMapper;
    }

    @Override
    public List<ProblemVO> selectProblemList(ProblemVO searchVO) {
        return problemMapper.selectProblemList(searchVO);
    }

    @Override
    public int selectProblemListCnt(ProblemVO searchVO) {
        return problemMapper.selectProblemListCnt(searchVO);
    }

    @Override
    public ProblemVO selectProblem(Long prbId) {
        ProblemVO vo = problemMapper.selectProblem(prbId);
        if (vo != null) {
            vo.setHistoryList(problemMapper.selectProblemHisList(prbId));
            vo.setIncList(problemMapper.selectProblemIncList(prbId));
            vo.setKedbList(problemMapper.selectKedbList(prbId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertProblem(ProblemVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("REGISTERED");
        }
        problemMapper.insertProblem(vo);
        // 등록 이력 적재
        ProblemHisVO his = new ProblemHisVO();
        his.setPrbId(vo.getPrbId());
        his.setStatus(vo.getStatus());
        his.setContent("문제 등록");
        his.setProcId(vo.getRegId());
        problemMapper.insertProblemHis(his);
        log.debug("문제 등록 : PRB-{}", vo.getPrbId());
    }

    @Override
    @Transactional
    public void processProblem(ProblemVO vo) {
        problemMapper.updateProblemProcess(vo);
        ProblemHisVO his = new ProblemHisVO();
        his.setPrbId(vo.getPrbId());
        his.setStatus(vo.getStatus());
        his.setContent(vo.getRootCause() != null && !vo.getRootCause().isBlank()
                ? vo.getRootCause() : "상태 변경");
        his.setProcId(vo.getChargerId());
        problemMapper.insertProblemHis(his);

        // 종결 시 KEDB(알려진 오류)가 없으면 근본원인/해결책으로 자동 등록
        if ("CLOSED".equals(vo.getStatus()) && problemMapper.selectKedbCnt(vo.getPrbId()) == 0) {
            ProblemVO prb = problemMapper.selectProblem(vo.getPrbId());
            KedbVO kedb = new KedbVO();
            kedb.setPrbId(prb.getPrbId());
            kedb.setTitle(prb.getTitle());
            kedb.setSymptom(prb.getContent());
            kedb.setCause(prb.getRootCause());
            kedb.setSolution(prb.getSolution());
            problemMapper.insertKedb(kedb);
            log.debug("문제 종결 KEDB 자동등록 : PRB-{}", vo.getPrbId());
        }
        log.debug("문제 처리 : PRB-{} -> {}", vo.getPrbId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void linkIncident(Long prbId, Long incId) {
        problemMapper.insertProblemInc(prbId, incId);
    }

    @Override
    @Transactional
    public void addKedb(KedbVO vo) {
        problemMapper.insertKedb(vo);
    }

    @Override
    @Transactional
    public void deleteProblem(Long prbId) {
        problemMapper.deleteProblemHis(prbId);
        problemMapper.deleteProblemInc(prbId);
        problemMapper.deleteProblem(prbId);
    }
}

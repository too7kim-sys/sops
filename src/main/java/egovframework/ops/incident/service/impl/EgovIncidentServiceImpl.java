package egovframework.ops.incident.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.incident.service.EgovIncidentService;
import egovframework.ops.incident.service.IncidentHisVO;
import egovframework.ops.incident.service.IncidentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 장애관리 서비스 구현체.
 *
 * <p>장애 처리상태 전이 시 처리이력을 함께 적재하여 표준운영절차의
 * 처리 추적성을 보장한다.</p>
 */
@Service("egovIncidentService")
public class EgovIncidentServiceImpl extends EgovAbstractServiceImpl implements EgovIncidentService {

    private final IncidentMapper incidentMapper;

    public EgovIncidentServiceImpl(IncidentMapper incidentMapper) {
        this.incidentMapper = incidentMapper;
    }

    @Override
    public List<IncidentVO> selectIncidentList(IncidentVO searchVO) {
        return incidentMapper.selectIncidentList(searchVO);
    }

    @Override
    public int selectIncidentListCnt(IncidentVO searchVO) {
        return incidentMapper.selectIncidentListCnt(searchVO);
    }

    @Override
    public IncidentVO selectIncident(Long incId) {
        IncidentVO vo = incidentMapper.selectIncident(incId);
        if (vo != null) {
            vo.setHistoryList(incidentMapper.selectIncidentHisList(incId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertIncident(IncidentVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("RECEIVED");
        }
        incidentMapper.insertIncident(vo);
        // 접수 이력 적재
        IncidentHisVO his = new IncidentHisVO();
        his.setIncId(vo.getIncId());
        his.setStatus(vo.getStatus());
        his.setContent("장애 접수");
        his.setProcId(vo.getRegId());
        incidentMapper.insertIncidentHis(his);
        log.debug("장애 접수 : INC-{}", vo.getIncId());
    }

    @Override
    @Transactional
    public void updateIncident(IncidentVO vo) {
        incidentMapper.updateIncident(vo);
    }

    @Override
    @Transactional
    public void processIncident(IncidentVO vo) {
        incidentMapper.updateIncidentProcess(vo);
        IncidentHisVO his = new IncidentHisVO();
        his.setIncId(vo.getIncId());
        his.setStatus(vo.getStatus());
        his.setContent(vo.getAction() != null && !vo.getAction().isBlank()
                ? vo.getAction() : "상태 변경");
        his.setProcId(vo.getChargerId());
        incidentMapper.insertIncidentHis(his);
        log.debug("장애 처리 : INC-{} -> {}", vo.getIncId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void deleteIncident(Long incId) {
        incidentMapper.deleteIncidentHis(incId);
        incidentMapper.deleteIncident(incId);
    }
}

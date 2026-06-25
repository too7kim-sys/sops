package egovframework.ops.incident.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.incident.service.EgovIncidentService;
import egovframework.ops.incident.service.IncidentEscalVO;
import egovframework.ops.incident.service.IncidentHisVO;
import egovframework.ops.incident.service.IncidentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 장애관리 서비스 구현체.
 *
 * <p>장애 처리상태 전이 시 처리이력을 함께 적재하여 표준운영절차의
 * 처리 추적성을 보장한다.</p>
 */
@Service("egovIncidentService")
public class EgovIncidentServiceImpl extends EgovAbstractServiceImpl implements EgovIncidentService {

    private static final DateTimeFormatter SLA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
            vo.setEscalList(incidentMapper.selectIncidentEscalList(incId));
            vo.setSlaStatus(calcSlaStatus(vo));
        }
        return vo;
    }

    /**
     * SLA 상태 산정.
     *
     * <p>목표복구일시가 없으면 null. 종결/조치완료이고 조치완료일시가 있으면
     * 목표 대비 준수/위반, 그 외 미해결은 현재시각 대비 진행중/지연으로 판정한다.
     * 동일 포맷("yyyy-MM-dd HH:mm") 문자열의 사전식 비교로 시간순을 판단한다.</p>
     */
    private String calcSlaStatus(IncidentVO vo) {
        String target = vo.getTargetResolveDt();
        if (target == null || target.isEmpty()) {
            return null;
        }
        boolean done = "RESOLVED".equals(vo.getStatus()) || "CLOSED".equals(vo.getStatus());
        if (done && vo.getResolveDt() != null && !vo.getResolveDt().isEmpty()) {
            return vo.getResolveDt().compareTo(target) <= 0 ? "준수" : "위반";
        }
        String now = LocalDateTime.now().format(SLA_FMT);
        return now.compareTo(target) <= 0 ? "진행중" : "지연";
    }

    @Override
    @Transactional
    public void insertIncident(IncidentVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("RECEIVED");
        }
        // SLA 목표복구일시 자동 산정 (등급별 가산)
        if (vo.getTargetResolveDt() == null || vo.getTargetResolveDt().isEmpty()) {
            vo.setTargetResolveDt(calcTargetResolveDt(vo.getSeverity()));
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

    @Override
    @Transactional
    public void escalateIncident(IncidentEscalVO vo) {
        incidentMapper.insertIncidentEscal(vo);
        log.debug("장애 에스컬레이션 : INC-{} -> {}", vo.getIncId(), vo.getEscalLevel());
    }

    /**
     * 장애 등급별 목표복구일시 산정.
     *
     * <p>현재시각 기준 가산: 1등급=+120분, 2등급=+240분, 3등급=+1440분, 4등급=+4320분.</p>
     */
    private String calcTargetResolveDt(String severity) {
        long minutes;
        if ("1".equals(severity)) {
            minutes = 120;
        } else if ("2".equals(severity)) {
            minutes = 240;
        } else if ("3".equals(severity)) {
            minutes = 1440;
        } else if ("4".equals(severity)) {
            minutes = 4320;
        } else {
            return null;
        }
        return LocalDateTime.now().plusMinutes(minutes).format(SLA_FMT);
    }
}

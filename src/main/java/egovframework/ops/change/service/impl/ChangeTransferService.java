package egovframework.ops.change.service.impl;

import egovframework.ops.change.service.ChangeVO;
import egovframework.ops.change.service.EgovChangeService;
import egovframework.ops.incident.service.EgovIncidentService;
import egovframework.ops.incident.service.IncidentVO;
import egovframework.ops.release.service.EgovReleaseService;
import egovframework.ops.release.service.ReleaseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 변경(Change) 이관 오케스트레이터.
 *
 * <p>변경처리 완료 시 후속 업무(배포요청/장애관리)로 신규 레코드를 생성하고
 * 변경에 연계(LINKED) 정보를 기록한다.</p>
 */
@Service
public class ChangeTransferService {

    /** 이관 대상 정보 (JSP 프로퍼티 접근용) */
    public static class TransferTarget {
        private final String code;
        private final String label;
        private final String urlPrefix;
        public TransferTarget(String code, String label, String urlPrefix) {
            this.code = code; this.label = label; this.urlPrefix = urlPrefix;
        }
        public String getCode() { return code; }
        public String getLabel() { return label; }
        public String getUrlPrefix() { return urlPrefix; }
    }

    /** 이관 대상 : 코드 → 대상정보 (입력 순서 유지) */
    public static final Map<String, TransferTarget> TARGETS = new LinkedHashMap<>();
    static {
        TARGETS.put("RELEASE",  new TransferTarget("RELEASE",  "배포요청", "/release/detail/"));
        TARGETS.put("INCIDENT", new TransferTarget("INCIDENT", "장애관리", "/incident/detail/"));
    }

    private final EgovChangeService changeService;
    private final ChangeMapper changeMapper;
    private final EgovReleaseService releaseService;
    private final EgovIncidentService incidentService;

    public ChangeTransferService(EgovChangeService changeService,
                                 ChangeMapper changeMapper,
                                 EgovReleaseService releaseService,
                                 EgovIncidentService incidentService) {
        this.changeService = changeService;
        this.changeMapper = changeMapper;
        this.releaseService = releaseService;
        this.incidentService = incidentService;
    }

    /**
     * 변경을 후속 업무로 이관(신규 생성 + 연계).
     *
     * @return 대상 상세화면 URL (이관 후 이동)
     */
    @Transactional
    public String transfer(Long chgId, String targetType, String actorId) {
        TransferTarget meta = TARGETS.get(targetType);
        if (meta == null) {
            throw new IllegalArgumentException("이관 대상이 아님: " + targetType);
        }
        ChangeVO chg = changeService.selectChange(chgId);
        String sysId = chg.getSysId();
        String title = chg.getTitle();
        String content = chg.getContent();

        Long newId;
        switch (targetType) {
            case "RELEASE": {
                ReleaseVO v = new ReleaseVO();
                v.setSysId(sysId); v.setChgId(chgId); v.setTitle(title); v.setContent(content);
                v.setVer("1.0.0"); v.setStatus("PLANNED"); v.setChargerId(actorId);
                releaseService.insertRelease(v); newId = v.getRelId(); break;
            }
            case "INCIDENT": {
                IncidentVO v = new IncidentVO();
                v.setSysId(sysId); v.setTitle(title); v.setContent(content);
                v.setSeverity("3"); v.setStatus("RECEIVED"); v.setRegId(actorId);
                incidentService.insertIncident(v); newId = v.getIncId(); break;
            }
            default:
                throw new IllegalArgumentException("이관 대상이 아님: " + targetType);
        }

        ChangeVO link = new ChangeVO();
        link.setChgId(chgId);
        link.setLinkedType(targetType);
        link.setLinkedId(newId);
        changeMapper.updateChangeLink(link);

        return meta.getUrlPrefix() + newId;
    }
}

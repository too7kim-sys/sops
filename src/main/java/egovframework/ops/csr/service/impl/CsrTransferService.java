package egovframework.ops.csr.service.impl;

import egovframework.ops.change.service.ChangeVO;
import egovframework.ops.change.service.EgovChangeService;
import egovframework.ops.ci.service.CiVO;
import egovframework.ops.ci.service.EgovCiService;
import egovframework.ops.csr.service.CsrVO;
import egovframework.ops.csr.service.EgovCsrService;
import egovframework.ops.event.service.EgovEventService;
import egovframework.ops.event.service.EventVO;
import egovframework.ops.incident.service.EgovIncidentService;
import egovframework.ops.incident.service.IncidentVO;
import egovframework.ops.itf.service.EgovInterfaceService;
import egovframework.ops.itf.service.InterfaceVO;
import egovframework.ops.problem.service.EgovProblemService;
import egovframework.ops.problem.service.ProblemVO;
import egovframework.ops.release.service.EgovReleaseService;
import egovframework.ops.release.service.ReleaseVO;
import egovframework.ops.test.service.EgovTestService;
import egovframework.ops.test.service.TestVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 요청(CSR) 이관 오케스트레이터.
 *
 * <p>분류 결과에 따라 요청을 대상 모듈(변경/배포/테스트/연계/형상/운영상태/장애/문제)의
 * 신규 레코드로 생성하고, CSR 에 연계(LINKED) 정보를 기록한다.</p>
 */
@Service
public class CsrTransferService {

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
        TARGETS.put("CHANGE",    new TransferTarget("CHANGE",    "변경관리",     "/change/detail/"));
        TARGETS.put("RELEASE",   new TransferTarget("RELEASE",   "배포관리",     "/release/detail/"));
        TARGETS.put("TEST",      new TransferTarget("TEST",      "테스트관리",   "/test/detail/"));
        TARGETS.put("INTERFACE", new TransferTarget("INTERFACE", "연계관리",     "/interface/detail/"));
        TARGETS.put("CI",        new TransferTarget("CI",        "형상관리",     "/ci/detail/"));
        TARGETS.put("EVENT",     new TransferTarget("EVENT",     "운영상태관리", "/event/detail/"));
        TARGETS.put("INCIDENT",  new TransferTarget("INCIDENT",  "장애관리",     "/incident/detail/"));
        TARGETS.put("PROBLEM",   new TransferTarget("PROBLEM",   "문제관리",     "/problem/detail/"));
    }

    private final EgovCsrService csrService;
    private final EgovChangeService changeService;
    private final EgovReleaseService releaseService;
    private final EgovTestService testService;
    private final EgovInterfaceService interfaceService;
    private final EgovCiService ciService;
    private final EgovEventService eventService;
    private final EgovIncidentService incidentService;
    private final EgovProblemService problemService;

    public CsrTransferService(EgovCsrService csrService,
                              EgovChangeService changeService,
                              EgovReleaseService releaseService,
                              EgovTestService testService,
                              EgovInterfaceService interfaceService,
                              EgovCiService ciService,
                              EgovEventService eventService,
                              EgovIncidentService incidentService,
                              EgovProblemService problemService) {
        this.csrService = csrService;
        this.changeService = changeService;
        this.releaseService = releaseService;
        this.testService = testService;
        this.interfaceService = interfaceService;
        this.ciService = ciService;
        this.eventService = eventService;
        this.incidentService = incidentService;
        this.problemService = problemService;
    }

    /**
     * CSR 을 대상 모듈로 이관(신규 생성 + 연계).
     *
     * @return 대상 상세화면 URL (이관 후 이동)
     */
    @Transactional
    public String transfer(Long csrId, String targetType, String actorId) {
        TransferTarget meta = TARGETS.get(targetType);
        if (meta == null) {
            throw new IllegalArgumentException("이관 대상이 아님: " + targetType);
        }
        CsrVO csr = csrService.selectCsr(csrId);
        String sysId = csr.getSysId();
        String title = csr.getTitle();
        String content = csr.getContent();

        Long newId;
        switch (targetType) {
            case "CHANGE": {
                ChangeVO v = new ChangeVO();
                v.setSysId(sysId); v.setTitle(title); v.setContent(content);
                v.setChgType("PROGRAM"); v.setStatus("REQUESTED"); v.setReqId(actorId);
                changeService.insertChange(v); newId = v.getChgId(); break;
            }
            case "RELEASE": {
                ReleaseVO v = new ReleaseVO();
                v.setSysId(sysId); v.setTitle(title); v.setContent(content);
                v.setVer("1.0.0"); v.setStatus("PLANNED"); v.setChargerId(actorId);
                releaseService.insertRelease(v); newId = v.getRelId(); break;
            }
            case "TEST": {
                TestVO v = new TestVO();
                v.setSysId(sysId); v.setTitle(title);
                v.setTestType("UNIT"); v.setTestEnv("DEV"); v.setStatus("PLANNED"); v.setTesterId(actorId);
                testService.insertTest(v); newId = v.getTestId(); break;
            }
            case "INTERFACE": {
                InterfaceVO v = new InterfaceVO();
                v.setSysId(sysId); v.setTitle(title); v.setDataDesc(content);
                v.setIfType("SYNC"); v.setStatus("REQUESTED"); v.setReqId(actorId);
                interfaceService.insertInterface(v); newId = v.getIntfId(); break;
            }
            case "CI": {
                CiVO v = new CiVO();
                v.setSysId(sysId); v.setCiNm(title); v.setCiDesc(content);
                v.setCiType("SOURCE"); v.setCiStatus("IDENTIFIED"); v.setOwnerId(actorId);
                ciService.insertCi(v); newId = v.getCiId(); break;
            }
            case "EVENT": {
                EventVO v = new EventVO();
                v.setSysId(sysId); v.setTitle(title); v.setContent(content);
                v.setEvtType("CPU"); v.setSeverity("INFO"); v.setStatus("DETECTED"); v.setChargerId(actorId);
                eventService.insertEvent(v); newId = v.getEvtId(); break;
            }
            case "INCIDENT": {
                IncidentVO v = new IncidentVO();
                v.setSysId(sysId); v.setTitle(title); v.setContent(content);
                v.setSeverity("3"); v.setStatus("RECEIVED"); v.setRegId(actorId);
                incidentService.insertIncident(v); newId = v.getIncId(); break;
            }
            case "PROBLEM": {
                ProblemVO v = new ProblemVO();
                v.setSysId(sysId); v.setTitle(title); v.setContent(content);
                v.setPriority("MID"); v.setStatus("REGISTERED"); v.setRegId(actorId);
                problemService.insertProblem(v); newId = v.getPrbId(); break;
            }
            default:
                throw new IllegalArgumentException("이관 대상이 아님: " + targetType);
        }

        csrService.transferLink(csrId, targetType, newId, actorId, meta.getLabel());
        return meta.getUrlPrefix() + newId;
    }
}

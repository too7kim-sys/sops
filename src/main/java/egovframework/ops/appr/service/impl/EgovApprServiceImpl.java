package egovframework.ops.appr.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.appr.service.ApprLineVO;
import egovframework.ops.appr.service.ApprTemplateVO;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.appr.service.ShareVO;
import egovframework.ops.sys.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 결재선/공유 서비스 구현체.
 *
 * <p>업무 모듈에 독립적인 공통 결재선·공유 처리를 제공한다. 동일 단계의 라인은
 * 병렬로 처리되며, 전체 진행상태는 화면(Controller)에서 라인 집계로 산정한다.</p>
 */
@Service("egovApprService")
public class EgovApprServiceImpl extends EgovAbstractServiceImpl implements EgovApprService {

    private final ApprMapper apprMapper;

    public EgovApprServiceImpl(ApprMapper apprMapper) {
        this.apprMapper = apprMapper;
    }

    /**
     * 업무 모듈 상태 자동전이 규칙(내부 화이트리스트).
     * {table, idCol, statusCol, approvedStatus, rejectedStatus, apprIdCol, apprDtCol} — 값은 코드 고정(주입 무관).
     * apprIdCol/apprDtCol 이 있으면 승인 확정 시 승인자/승인일시를 기록한다.
     */
    private static final Map<String, String[]> STATUS_RULE = new LinkedHashMap<>();
    static {
        STATUS_RULE.put("CHANGE",  new String[]{"OPS_CHANGE",  "CHG_ID", "STATUS", "APPROVED",    "REJECTED", "APPR_ID", "APPR_DT"});
        STATUS_RULE.put("RELEASE", new String[]{"OPS_RELEASE", "REL_ID", "STATUS", "APPROVED",    null,       null,      null});
        STATUS_RULE.put("CSR",     new String[]{"OPS_CSR",     "CSR_ID", "STATUS", "IN_PROGRESS", "REJECTED", null,      null});
    }

    private ApprLineVO lineKey(String bizType, Long bizId) {
        ApprLineVO p = new ApprLineVO();
        p.setBizType(bizType);
        p.setBizId(bizId);
        return p;
    }

    @Override
    public List<ApprLineVO> selectLineList(String bizType, Long bizId) {
        return apprMapper.selectLineList(lineKey(bizType, bizId));
    }

    @Override
    public ApprLineVO selectLine(Long apprId) {
        return apprMapper.selectLine(apprId);
    }

    @Override
    public void insertLine(ApprLineVO vo) {
        apprMapper.insertLine(vo);
    }

    @Override
    public void deleteLine(Long apprId) {
        apprMapper.deleteLine(apprId);
    }

    @Override
    public void actLine(ApprLineVO vo) {
        apprMapper.actLine(vo);
    }

    @Override
    @Transactional
    public String applyModuleOutcome(String bizType, Long bizId, String actorId, String actorNm, String opinion) {
        String[] rule = STATUS_RULE.get(bizType);
        if (rule == null) {
            return null; // 승인 게이트 미정의 모듈 — 협업 레이어로만 동작
        }
        int approveTotal = 0, approved = 0;
        boolean rejected = false;
        for (ApprLineVO l : selectLineList(bizType, bizId)) {
            if (!"APPROVE".equals(l.getLineType())) {
                continue;
            }
            approveTotal++;
            if ("APPROVED".equals(l.getStatus())) {
                approved++;
            } else if ("REJECTED".equals(l.getStatus())) {
                rejected = true;
            }
        }
        boolean isApproved = false;
        String target = null;
        if (rejected) {
            target = rule[4];
        } else if (approveTotal > 0 && approved == approveTotal) {
            target = rule[3];
            isApproved = true;
        }
        if (target != null) {
            apprMapper.updateBizStatus(rule[0], rule[1], rule[2], bizId, target);
            // 승인 확정 시 승인자/승인일시 기록(컬럼이 정의된 모듈: 변경)
            if (isApproved && rule[5] != null) {
                apprMapper.updateBizApprover(rule[0], rule[1], rule[5], rule[6], bizId, actorId);
            }
            // 변경 처리이력(CAB)에 결재선 승인/반려 단계 기록
            if ("CHANGE".equals(bizType)) {
                String decision = isApproved ? "APPROVED" : "REJECTED";
                String note = (opinion != null && !opinion.isBlank())
                        ? opinion
                        : (isApproved ? "결재선 승인 확정" : "결재선 반려");
                String reviewer = (actorNm != null && !actorNm.isBlank()) ? actorNm : actorId;
                apprMapper.insertChangeCabHistory(bizId, decision, "[결재선] " + note, reviewer);
            }
        }
        return target;
    }

    @Override
    public List<ShareVO> selectShareList(String bizType, Long bizId) {
        ShareVO p = new ShareVO();
        p.setBizType(bizType);
        p.setBizId(bizId);
        return apprMapper.selectShareList(p);
    }

    @Override
    public void insertShare(ShareVO vo) {
        apprMapper.insertShare(vo);
    }

    @Override
    public void deleteShare(Long shareId) {
        apprMapper.deleteShare(shareId);
    }

    @Override
    @Transactional
    public void markShareRead(String bizType, Long bizId, String userId) {
        ShareVO p = new ShareVO();
        p.setBizType(bizType);
        p.setBizId(bizId);
        p.setUserId(userId);
        apprMapper.updateShareRead(p);
    }

    @Override
    public List<ShareVO> selectSharedWithMe(String userId) {
        return apprMapper.selectSharedWithMe(userId);
    }

    @Override
    public int selectSharedUnreadCnt(String userId) {
        return apprMapper.selectSharedUnreadCnt(userId);
    }

    @Override
    public List<UserVO> selectAssigneeCandidates() {
        return apprMapper.selectAssigneeCandidates();
    }

    /**
     * 요청자/담당자 컬럼 규칙(내부 화이트리스트). {table, idCol, reqCol}.
     */
    private static final Map<String, String[]> REQUESTER_RULE = new LinkedHashMap<>();
    static {
        REQUESTER_RULE.put("CHANGE",    new String[]{"OPS_CHANGE",    "CHG_ID",  "REQ_ID"});
        REQUESTER_RULE.put("RELEASE",   new String[]{"OPS_RELEASE",   "REL_ID",  "CHARGER_ID"});
        REQUESTER_RULE.put("CSR",       new String[]{"OPS_CSR",       "CSR_ID",  "REQ_ID"});
        REQUESTER_RULE.put("INCIDENT",  new String[]{"OPS_INCIDENT",  "INC_ID",  "REG_ID"});
        REQUESTER_RULE.put("PROBLEM",   new String[]{"OPS_PROBLEM",   "PRB_ID",  "REG_ID"});
        REQUESTER_RULE.put("TEST",      new String[]{"OPS_TEST",      "TEST_ID", "TESTER_ID"});
        REQUESTER_RULE.put("INTERFACE", new String[]{"OPS_INTERFACE", "INTF_ID", "REQ_ID"});
        REQUESTER_RULE.put("CI",        new String[]{"OPS_CI",        "CI_ID",   "OWNER_ID"});
        REQUESTER_RULE.put("EVENT",     new String[]{"OPS_EVENT",     "EVT_ID",  "CHARGER_ID"});
    }

    /**
     * 접근권한 판정 규칙(내부 화이트리스트). {table, idCol, reqCol, chargerCol(nullable)}.
     */
    private static final Map<String, String[]> ACCESS_RULE = new LinkedHashMap<>();
    static {
        ACCESS_RULE.put("CHANGE",    new String[]{"OPS_CHANGE",    "CHG_ID",  "REQ_ID",     "APPR_ID"});
        ACCESS_RULE.put("RELEASE",   new String[]{"OPS_RELEASE",   "REL_ID",  "CHARGER_ID", null});
        ACCESS_RULE.put("CSR",       new String[]{"OPS_CSR",       "CSR_ID",  "REQ_ID",     "CHARGER_ID"});
        ACCESS_RULE.put("INCIDENT",  new String[]{"OPS_INCIDENT",  "INC_ID",  "REG_ID",     "CHARGER_ID"});
        ACCESS_RULE.put("PROBLEM",   new String[]{"OPS_PROBLEM",   "PRB_ID",  "REG_ID",     "CHARGER_ID"});
        ACCESS_RULE.put("TEST",      new String[]{"OPS_TEST",      "TEST_ID", "TESTER_ID",  null});
        ACCESS_RULE.put("INTERFACE", new String[]{"OPS_INTERFACE", "INTF_ID", "REQ_ID",     "CHARGER_ID"});
        ACCESS_RULE.put("CI",        new String[]{"OPS_CI",        "CI_ID",   "OWNER_ID",   null});
        ACCESS_RULE.put("EVENT",     new String[]{"OPS_EVENT",     "EVT_ID",  "CHARGER_ID", null});
    }

    @Override
    public boolean canAccess(String bizType, Long bizId, String userId) {
        String[] r = ACCESS_RULE.get(bizType);
        if (r == null || bizId == null || userId == null) {
            return true; // 정의되지 않은 대상/식별 불가 → 차단하지 않음
        }
        return apprMapper.countAccess(r[0], r[1], r[2], r[3], bizType, bizId, userId) > 0;
    }

    @Override
    public List<String> selectDeptList() {
        return apprMapper.selectDeptList();
    }

    @Override
    public List<String> resolveTargets(String bizType, Long bizId, String targetType, String targetValue) {
        if (targetType == null) {
            return java.util.Collections.emptyList();
        }
        switch (targetType) {
            case "USER":
                return (targetValue == null || targetValue.isBlank())
                        ? java.util.Collections.emptyList()
                        : java.util.List.of(targetValue);
            case "DEPT":
                return (targetValue == null || targetValue.isBlank())
                        ? java.util.Collections.emptyList()
                        : apprMapper.selectUserIdsByDept(targetValue);
            case "ALL":
                return apprMapper.selectAllActiveUserIds();
            case "REQUESTER": {
                String[] r = REQUESTER_RULE.get(bizType);
                if (r == null) {
                    return java.util.Collections.emptyList();
                }
                String req = apprMapper.selectBizRequester(r[0], r[1], r[2], bizId);
                return (req == null || req.isBlank())
                        ? java.util.Collections.emptyList()
                        : java.util.List.of(req);
            }
            default:
                return java.util.Collections.emptyList();
        }
    }

    @Override
    public List<ApprTemplateVO> selectTemplateList(String bizType) {
        return apprMapper.selectTemplateList(bizType);
    }

    @Override
    public void insertTemplate(ApprTemplateVO vo) {
        apprMapper.insertTemplate(vo);
    }

    @Override
    public void deleteTemplate(Long tplId) {
        apprMapper.deleteTemplate(tplId);
    }

    @Override
    @Transactional
    public int applyTemplate(String bizType, Long bizId, String actorId) {
        int created = 0;
        for (ApprTemplateVO t : apprMapper.selectTemplateList(bizType)) {
            List<String> ids = resolveTargets(bizType, bizId, t.getTargetType(), t.getTargetValue());
            int sort = 1;
            java.util.Set<String> seen = new java.util.LinkedHashSet<>(ids);
            for (String uid : seen) {
                if (uid == null || uid.isBlank()) {
                    continue;
                }
                if ("SHARE".equals(t.getKind())) {
                    ShareVO sv = new ShareVO();
                    sv.setBizType(bizType);
                    sv.setBizId(bizId);
                    sv.setUserId(uid);
                    sv.setShareMemo(t.getMemo());
                    sv.setSharedBy(actorId);
                    apprMapper.insertShare(sv);
                } else {
                    ApprLineVO lv = new ApprLineVO();
                    lv.setBizType(bizType);
                    lv.setBizId(bizId);
                    lv.setLineType(t.getLineType());
                    lv.setStepNo(t.getStepNo());
                    lv.setSortNo(sort++);
                    lv.setAssigneeId(uid);
                    lv.setStatus("PENDING");
                    lv.setRegId(actorId);
                    apprMapper.insertLine(lv);
                }
                created++;
            }
        }
        return created;
    }
}

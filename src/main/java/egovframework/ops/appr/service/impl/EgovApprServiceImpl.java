package egovframework.ops.appr.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.appr.service.ApprLineVO;
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
     * {table, idCol, statusCol, approvedStatus, rejectedStatus} — 값은 코드 고정(주입 무관).
     */
    private static final Map<String, String[]> STATUS_RULE = new LinkedHashMap<>();
    static {
        STATUS_RULE.put("CHANGE",  new String[]{"OPS_CHANGE",  "CHG_ID", "STATUS", "APPROVED",    "REJECTED"});
        STATUS_RULE.put("RELEASE", new String[]{"OPS_RELEASE", "REL_ID", "STATUS", "APPROVED",    null});
        STATUS_RULE.put("CSR",     new String[]{"OPS_CSR",     "CSR_ID", "STATUS", "IN_PROGRESS", "REJECTED"});
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
    public String applyModuleOutcome(String bizType, Long bizId) {
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
        String target = null;
        if (rejected) {
            target = rule[4];
        } else if (approveTotal > 0 && approved == approveTotal) {
            target = rule[3];
        }
        if (target != null) {
            apprMapper.updateBizStatus(rule[0], rule[1], rule[2], bizId, target);
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
}

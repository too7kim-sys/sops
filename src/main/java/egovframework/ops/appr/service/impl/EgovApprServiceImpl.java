package egovframework.ops.appr.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.appr.service.ApprLineVO;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.appr.service.ShareVO;
import egovframework.ops.sys.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

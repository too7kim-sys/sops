package egovframework.ops.change.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.change.service.ChangeVO;
import egovframework.ops.change.service.EgovChangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 변경관리 서비스 구현체.
 *
 * <p>변경요청 → 심의/승인 → 적용/완료의 표준운영절차에 따라 상태를 전이하고
 * 요청/심의/적용 일시를 기록하여 처리 추적성을 보장한다.</p>
 */
@Service("egovChangeService")
public class EgovChangeServiceImpl extends EgovAbstractServiceImpl implements EgovChangeService {

    private final ChangeMapper changeMapper;

    public EgovChangeServiceImpl(ChangeMapper changeMapper) {
        this.changeMapper = changeMapper;
    }

    @Override
    public List<ChangeVO> selectChangeList(ChangeVO searchVO) {
        return changeMapper.selectChangeList(searchVO);
    }

    @Override
    public int selectChangeListCnt(ChangeVO searchVO) {
        return changeMapper.selectChangeListCnt(searchVO);
    }

    @Override
    public ChangeVO selectChange(Long chgId) {
        return changeMapper.selectChange(chgId);
    }

    @Override
    @Transactional
    public void insertChange(ChangeVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("REQUESTED");
        }
        changeMapper.insertChange(vo);
        log.debug("변경요청 등록 : CHG-{}", vo.getChgId());
    }

    @Override
    @Transactional
    public void updateChange(ChangeVO vo) {
        changeMapper.updateChange(vo);
    }

    @Override
    @Transactional
    public void approveChange(ChangeVO vo) {
        changeMapper.updateChangeApprove(vo);
        log.debug("변경 심의/승인 : CHG-{} -> {}", vo.getChgId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void applyChange(ChangeVO vo) {
        changeMapper.updateChangeApply(vo);
        log.debug("변경 적용 : CHG-{} -> {}", vo.getChgId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void deleteChange(Long chgId) {
        changeMapper.deleteChange(chgId);
    }
}

package egovframework.ops.release.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.release.service.EgovReleaseService;
import egovframework.ops.release.service.ReleaseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 배포관리 서비스 구현체.
 *
 * <p>배포 처리상태 전이 시 배포결과를 함께 갱신하고, 배포완료 시점의
 * 배포일시를 기록하여 표준운영절차의 배포 추적성을 보장한다.</p>
 */
@Service("egovReleaseService")
public class EgovReleaseServiceImpl extends EgovAbstractServiceImpl implements EgovReleaseService {

    private final ReleaseMapper releaseMapper;

    public EgovReleaseServiceImpl(ReleaseMapper releaseMapper) {
        this.releaseMapper = releaseMapper;
    }

    @Override
    public List<ReleaseVO> selectReleaseList(ReleaseVO searchVO) {
        return releaseMapper.selectReleaseList(searchVO);
    }

    @Override
    public int selectReleaseListCnt(ReleaseVO searchVO) {
        return releaseMapper.selectReleaseListCnt(searchVO);
    }

    @Override
    public ReleaseVO selectRelease(Long relId) {
        return releaseMapper.selectRelease(relId);
    }

    @Override
    @Transactional
    public void insertRelease(ReleaseVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("PLANNED");
        }
        releaseMapper.insertRelease(vo);
        log.debug("배포계획 등록 : REL-{}", vo.getRelId());
    }

    @Override
    @Transactional
    public void updateRelease(ReleaseVO vo) {
        releaseMapper.updateRelease(vo);
    }

    @Override
    @Transactional
    public void processRelease(ReleaseVO vo) {
        releaseMapper.updateReleaseProcess(vo);
        log.debug("배포 처리 : REL-{} -> {}", vo.getRelId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void deleteRelease(Long relId) {
        releaseMapper.deleteRelease(relId);
    }
}

package egovframework.ops.release.service;

import java.util.List;

/**
 * 배포관리 서비스 인터페이스.
 *
 * <p>응용프로그램 표준운영절차 — 배포계획 등록/조회/처리(상태전이)/완료를 제공한다.</p>
 */
public interface EgovReleaseService {

    List<ReleaseVO> selectReleaseList(ReleaseVO searchVO);

    int selectReleaseListCnt(ReleaseVO searchVO);

    /** 배포 상세 */
    ReleaseVO selectRelease(Long relId);

    /** 배포계획 등록 */
    void insertRelease(ReleaseVO vo);

    /** 배포 기본정보 수정 */
    void updateRelease(ReleaseVO vo);

    /**
     * 배포 처리(상태전이).
     * 상태 변경과 함께 배포결과를 갱신하고, 배포완료 시 배포일시를 기록한다.
     */
    void processRelease(ReleaseVO vo);

    /** 배포 삭제 */
    void deleteRelease(Long relId);
}

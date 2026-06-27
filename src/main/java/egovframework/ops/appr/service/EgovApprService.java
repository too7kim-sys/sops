package egovframework.ops.appr.service;

import egovframework.ops.sys.user.service.UserVO;

import java.util.List;

/**
 * 결재선(검토/승인/처리자 라인) · 공유 서비스 인터페이스.
 *
 * <p>업무 모듈에 종속되지 않는 공통 컴포넌트로, 업무 구분(bizType)과 레코드 ID(bizId)
 * 로 어떤 표준운영절차 레코드에도 결재선·공유를 부여할 수 있다.</p>
 */
public interface EgovApprService {

    /* ---------- 결재선 ---------- */

    /** 결재선 목록 (단계/순서 정렬) */
    List<ApprLineVO> selectLineList(String bizType, Long bizId);

    /** 결재선 단건 */
    ApprLineVO selectLine(Long apprId);

    /** 결재선 추가 */
    void insertLine(ApprLineVO vo);

    /** 결재선 삭제 */
    void deleteLine(Long apprId);

    /** 결재 처리(검토/승인/반려/처리완료) — 의견 포함 */
    void actLine(ApprLineVO vo);

    /* ---------- 공유 ---------- */

    /** 특정 업무의 공유 목록 */
    List<ShareVO> selectShareList(String bizType, Long bizId);

    /** 공유 추가 */
    void insertShare(ShareVO vo);

    /** 공유 삭제 */
    void deleteShare(Long shareId);

    /** 공유 열람 처리(대상자가 화면 조회 시) */
    void markShareRead(String bizType, Long bizId, String userId);

    /** 나에게 공유된 목록(공유함) */
    List<ShareVO> selectSharedWithMe(String userId);

    /** 나에게 공유된 미열람 건수(뱃지) */
    int selectSharedUnreadCnt(String userId);

    /* ---------- 보조 ---------- */

    /** 결재선/공유 지정 후보(활성 사용자) */
    List<UserVO> selectAssigneeCandidates();
}

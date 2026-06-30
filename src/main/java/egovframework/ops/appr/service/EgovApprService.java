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

    /**
     * 결재선 처리 결과를 업무 모듈 상태에 반영(자동 전이).
     *
     * <p>승인 라인 전원 승인 → 모듈 승인상태, 1건이라도 반려 → 모듈 반려상태로 전이한다.
     * 승인 게이트가 정의된 모듈(변경/배포/요청)만 동작하며, 변경된 상태값을 반환(없으면 null).
     * 승인 확정 시 승인자/승인일시 컬럼이 정의된 모듈(변경)은 처리자({@code actorId})와 현재시각을 기록하고,
     * 변경 처리이력(CAB)에 승인/반려 단계를 한 건 남긴다.</p>
     */
    String applyModuleOutcome(String bizType, Long bizId, String actorId, String actorNm, String opinion);

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

    /* ---------- 대상 전개(부서/요청자/전체) ---------- */

    /** 부서 목록 */
    List<String> selectDeptList();

    /**
     * 대상 유형을 실제 사용자 ID 목록으로 전개한다.
     * USER→해당 사용자, DEPT→부서원 전체, REQUESTER→요청자, ALL→전체 활성 사용자.
     */
    List<String> resolveTargets(String bizType, Long bizId, String targetType, String targetValue);

    /* ---------- 관리별 기본 템플릿 ---------- */

    List<ApprTemplateVO> selectTemplateList(String bizType);

    void insertTemplate(ApprTemplateVO vo);

    void updateTemplate(ApprTemplateVO vo);

    void deleteTemplate(Long tplId);

    /** 업무 구분별 기본 템플릿을 해당 레코드에 전개·적용(생성 건수 반환) */
    int applyTemplate(String bizType, Long bizId, String actorId);

    /**
     * 업무 레코드 접근권한 판정(운영관리자 제외).
     * 요청자/처리자/결재선 대상자(검토/승인/처리/심의)/공유 대상자 중 하나면 true.
     */
    boolean canAccess(String bizType, Long bizId, String userId);
}

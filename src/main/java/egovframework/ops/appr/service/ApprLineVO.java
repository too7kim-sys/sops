package egovframework.ops.appr.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 결재선(검토/승인/처리자 라인) VO.
 *
 * <p>업무 레코드(bizType+bizId)에 느슨하게 연결되는 처리 라인 한 건. 동일 단계(stepNo)에
 * 속한 라인은 <b>병렬</b>로 검토·승인할 수 있다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApprLineVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 결재선 ID */
    private Long apprId;

    /** 업무 구분 (CHANGE/RELEASE/CSR/INCIDENT/PROBLEM/TEST/INTERFACE/CI/EVENT) */
    private String bizType;

    /** 업무 레코드 ID */
    private Long bizId;

    /** 라인 유형 (REVIEW/APPROVE/HANDLE) */
    private String lineType;

    /** 라인 유형 명 (코드 조인) */
    private String lineTypeNm;

    /** 단계 (동일 단계 = 병렬) */
    private Integer stepNo;

    /** 단계 내 표시순서 */
    private Integer sortNo;

    /** 지정 대상자 ID */
    private String assigneeId;

    /** 지정 대상자 명 (조인) */
    private String assigneeNm;

    /** 지정 대상자 부서 (조인) */
    private String assigneeDept;

    /** 지정 근거(USER/DEPT/REQUESTER/ALL) — 부서/전체 전개분은 열람권한에서 제외 */
    private String targetType;

    /** 처리 상태 (PENDING/APPROVED/REJECTED/REVIEWED/DONE) */
    private String status;

    /** 처리 상태 명 (코드 조인) */
    private String statusNm;

    /** 의견 */
    private String opinion;

    /** 처리일시 */
    private String actDt;

    /** 등록자 */
    private String regId;

    /** 등록일시 */
    private String regDt;

    /** 처리 액션 (act 요청 파라미터: APPROVE/REJECT/REVIEW/DONE) */
    private String action;
}

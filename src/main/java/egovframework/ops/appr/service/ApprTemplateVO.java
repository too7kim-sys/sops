package egovframework.ops.appr.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 결재선/공유 기본 템플릿 VO (관리별 공통 기본설정).
 *
 * <p>업무 구분(bizType)별로 신규 건에 적용할 기본 결재선/공유를 정의한다.
 * 대상은 사용자/부서/요청자/전체로 지정하며, 적용 시 실제 사용자로 전개된다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApprTemplateVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 템플릿 ID */
    private Long tplId;

    /** 업무 구분 */
    private String bizType;

    /** 업무 구분 명 */
    private String bizTypeNm;

    /** 종류 (LINE/SHARE) */
    private String kind;

    /** 라인 유형 (REVIEW/APPROVE/HANDLE) */
    private String lineType;

    /** 라인 유형 명 */
    private String lineTypeNm;

    /** 단계 */
    private Integer stepNo;

    /** 정렬 순서 */
    private Integer sortNo;

    /** 대상 유형 (USER/DEPT/REQUESTER/ALL) */
    private String targetType;

    /** 대상 유형 명 */
    private String targetTypeNm;

    /** 대상 값 (USER=userId / DEPT=deptCd / null) */
    private String targetValue;

    /** 대상 값 표시명 (DEPT 인 경우 부서명 조인) */
    private String targetValueNm;

    /** 공유 메모 */
    private String memo;
}

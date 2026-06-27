package egovframework.ops.appr.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 공유(공람/참조) VO.
 *
 * <p>업무 레코드를 다른 운영자에게 공유하여 열람할 수 있도록 한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ShareVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 공유 ID */
    private Long shareId;

    /** 업무 구분 */
    private String bizType;

    /** 업무 구분 명 */
    private String bizTypeNm;

    /** 업무 레코드 ID */
    private Long bizId;

    /** 공유 대상자 ID */
    private String userId;

    /** 공유 대상자 명 (조인) */
    private String userNm;

    /** 공유 대상자 부서 (조인) */
    private String userDept;

    /** 공유 메모 */
    private String shareMemo;

    /** 열람여부 (Y/N) */
    private String readAt;

    /** 공유한 사람 */
    private String sharedBy;

    /** 공유한 사람 명 (조인) */
    private String sharedByNm;

    /** 공유일시 */
    private String sharedDt;

    /** 상세화면 경로 (공유함 링크용, 컨텍스트 상대) */
    private String detailUrl;
}

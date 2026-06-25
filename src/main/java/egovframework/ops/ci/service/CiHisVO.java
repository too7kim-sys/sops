package egovframework.ops.ci.service;

import lombok.Data;

/**
 * 형상 변경(통제/감사)이력 VO.
 */
@Data
public class CiHisVO {

    /** 이력 ID */
    private Long hisId;

    /** 형상 ID */
    private Long ciId;

    /** 변경 유형 (IDENTIFY/CHECKOUT/CHECKIN/BASELINE/AUDIT) */
    private String chgType;

    /** 변경 유형명 (조인) */
    private String chgTypeNm;

    /** 버전 */
    private String ver;

    /** 변경 내용 */
    private String content;

    /** 처리자 ID */
    private String procId;

    /** 처리 일시 */
    private String procDt;
}

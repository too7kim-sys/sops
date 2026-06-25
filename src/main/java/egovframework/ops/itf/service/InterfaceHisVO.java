package egovframework.ops.itf.service;

import lombok.Data;

/**
 * 연계 처리이력 VO.
 */
@Data
public class InterfaceHisVO {

    /** 이력 ID */
    private Long hisId;

    /** 연계 ID */
    private Long intfId;

    /** 처리 상태 */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 처리 내용 */
    private String content;

    /** 처리자 ID */
    private String procId;

    /** 처리 일시 */
    private String procDt;
}

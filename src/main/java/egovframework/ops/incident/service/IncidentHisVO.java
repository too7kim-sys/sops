package egovframework.ops.incident.service;

import lombok.Data;

/**
 * 장애 처리이력 VO.
 */
@Data
public class IncidentHisVO {

    /** 이력 ID */
    private Long hisId;

    /** 장애 ID */
    private Long incId;

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

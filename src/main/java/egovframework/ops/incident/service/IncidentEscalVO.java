package egovframework.ops.incident.service;

import lombok.Data;

/**
 * 장애 에스컬레이션 VO.
 *
 * <p>장애 처리 지연/심각도 상향 시 상위 단계(L1/L2/MANAGER/VENDOR)로의
 * 에스컬레이션 이력을 보관한다.</p>
 */
@Data
public class IncidentEscalVO {

    /** 에스컬레이션 ID */
    private Long escalId;

    /** 장애 ID */
    private Long incId;

    /** 에스컬레이션 단계 (L1/L2/MANAGER/VENDOR) */
    private String escalLevel;

    /** 에스컬레이션 단계명 (조인) */
    private String escalLevelNm;

    /** 에스컬레이션 대상 */
    private String escalTo;

    /** 에스컬레이션 사유 */
    private String reason;

    /** 에스컬레이션 수행자 */
    private String escalBy;

    /** 에스컬레이션 일시 */
    private String escalDt;
}

package egovframework.ops.change.service;

import lombok.Data;

import java.io.Serializable;

/**
 * 변경자문위원회(CAB) 심의 이력 VO.
 *
 * <p>변경관리의 CAB 심의 결과(승인/반려/보류)와 의견·심의위원·심의일시를 보관한다.</p>
 */
@Data
public class ChangeCabVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** CAB 심의 ID */
    private Long cabId;

    /** 변경 ID */
    private Long chgId;

    /** 심의 결과 (APPROVED/REJECTED/HOLD) */
    private String decision;

    /** 심의 결과명 (조인) */
    private String decisionNm;

    /** 심의 의견 */
    private String opinion;

    /** 심의위원 */
    private String reviewer;

    /** 심의 일시 */
    private String cabDt;
}

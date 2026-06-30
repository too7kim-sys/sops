package egovframework.ops.sys.menu.service;

import lombok.Data;

import java.io.Serializable;

/**
 * 역할(권한 그룹) VO.
 *
 * <p>역할코드(ROLE_CD)는 사용자 ROLE 및 메뉴권한(OPS_MENU_AUTH.ROLE_ID)과 매핑되며,
 * Spring Security 권한 {@code ROLE_<코드>} 로 부여된다.</p>
 */
@Data
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 역할코드(권한) */
    private String roleCd;

    /** 역할명 */
    private String roleNm;

    /** 정렬순서 */
    private Integer sortNo;

    /** 내장역할 여부(Y=삭제/코드변경 불가) */
    private String builtin;

    /** 사용여부 */
    private String useAt;

    /** 등록일시 */
    private String regDt;

    /** 해당 역할을 사용하는 사용자 수(삭제 가드용, 조회 전용) */
    private Integer userCnt;
}

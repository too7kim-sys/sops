package egovframework.com.cmm;

import lombok.Data;

import java.io.Serializable;

/**
 * 로그인 사용자 세션 정보 VO.
 *
 * <p>표준프레임워크 공통 컴포넌트의 LoginVO 에 대응하며, 인증된 운영자의
 * 식별/권한 정보를 보관한다.</p>
 */
@Data
public class SessionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 사용자 ID */
    private String userId;

    /** 사용자 명 */
    private String userNm;

    /** 권한(롤) : ADMIN(운영관리자), OPERATOR(운영자), USER(일반) */
    private String role;

    /** 소속 부서코드 (OPS_DEPT.DEPT_CD) */
    private String deptCd;

    /** 소속 부서명 (조인 표시용) */
    private String deptNm;

    /** 이메일 */
    private String email;
}

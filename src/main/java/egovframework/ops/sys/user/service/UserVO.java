package egovframework.ops.sys.user.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 운영자(사용자) 정보 VO.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 사용자 ID */
    private String userId;

    /** 사용자 명 */
    private String userNm;

    /** 비밀번호(BCrypt 해시) */
    private String password;

    /** 권한 : ADMIN(운영관리자), OPERATOR(운영자), USER(일반사용자) */
    private String role;

    /** 권한명 (OPS_ROLE.ROLE_NM 조인) */
    private String roleNm;

    /** 직급 (성명(직급) 표기용) */
    private String positn;

    /** 소속 부서코드 (OPS_DEPT.DEPT_CD) — 저장/매핑 기준 */
    private String deptCd;

    /** 소속 부서명 (OPS_DEPT 조인 결과) — 화면 표시용 */
    private String deptNm;

    /** 이메일 */
    private String email;

    /** 연락처 */
    private String telno;

    /** 사용 여부 (Y/N) */
    private String useAt;

    /** 등록 일시 */
    private String regDt;
}

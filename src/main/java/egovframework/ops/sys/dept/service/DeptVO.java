package egovframework.ops.sys.dept.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 부서 마스터 VO (기준정보).
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DeptVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 부서 ID */
    private Long deptId;

    /** 부서 코드 */
    private String deptCd;

    /** 부서명 */
    private String deptNm;

    /** 상위부서 ID */
    private Long upperId;

    /** 상위부서명 (조인) */
    private String upperNm;

    /** 부서장 사용자 ID */
    private String mngrId;

    /** 부서장 명 (조인) */
    private String mngrNm;

    /** 정렬 순서 */
    private Integer sortOrdr;

    /** 부서 설명 */
    private String deptDesc;

    /** 사용 여부 */
    private String useAt;

    /** 등록일시 */
    private String regDt;

    /** 소속 사용자 수 (조인) */
    private Integer userCnt;
}

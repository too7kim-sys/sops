package egovframework.ops.cmm.code.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 공통코드 VO.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CodeVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 코드그룹 */
    private String codeGrp;

    /** 코드그룹명 (OPS_CODE_GRP 조인/입력) */
    private String grpNm;

    /** 코드값 */
    private String codeId;

    /** 코드명 */
    private String codeNm;

    /** 정렬순서 */
    private int sortOrdr;

    /** 사용여부 */
    private String useAt;

    /** 상위코드값 (계층코드 : 소분류→대분류 매핑, 예 CSR_SUBTYPE의 UPPER_CODE=CSR_TYPE) */
    private String upperCode;
}

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

    /** 코드값 */
    private String codeId;

    /** 코드명 */
    private String codeNm;

    /** 정렬순서 */
    private int sortOrdr;

    /** 사용여부 */
    private String useAt;
}

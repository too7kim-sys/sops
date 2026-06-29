package egovframework.ops.csr.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 요청 소분류별 요청내용 템플릿 VO.
 *
 * <p>요청 소분류(CSR_SUBTYPE 코드) 1건당 요청내용 템플릿 1건을 관리한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CsrTplVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 요청 소분류 코드 (CSR_SUBTYPE) */
    private String subType;

    /** 요청 소분류명 (조인) */
    private String subTypeNm;

    /** 요청 대분류 코드 (CSR_TYPE = 소분류의 UPPER_CODE) */
    private String csrType;

    /** 요청 대분류명 (조인) */
    private String csrTypeNm;

    /** 요청내용 템플릿 (리치텍스트 HTML) */
    private String content;

    /** 사용여부 (Y/N) */
    private String useAt;

    /** 등록 일시 */
    private String regDt;
}

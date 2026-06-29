package egovframework.ops.csr.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 요청(CSR) 첨부파일 VO.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CsrFileVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 파일 ID */
    private Long fileId;

    /** 요청 ID */
    private Long csrId;

    /** 원본 파일명 */
    private String originNm;

    /** 저장 파일명(UUID) */
    private String storeNm;

    /** 파일 크기(byte) */
    private Long fileSize;

    /** 콘텐츠 타입 */
    private String contentType;

    /** 등록자 ID */
    private String regId;

    /** 등록 일시 */
    private String regDt;
}

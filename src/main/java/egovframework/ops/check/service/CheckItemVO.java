package egovframework.ops.check.service;

import lombok.Data;

/**
 * 운영점검 항목 VO.
 *
 * <p>점검헤더(CheckVO)에 종속되는 개별 점검항목. 항목별 결과(NORMAL/ABNORMAL)와
 * 비고를 보관한다.</p>
 */
@Data
public class CheckItemVO {

    /** 항목 ID */
    private Long itemId;

    /** 점검 ID */
    private Long chkId;

    /** 항목명 */
    private String itemNm;

    /** 항목 결과 (NORMAL/ABNORMAL) */
    private String itemResult;

    /** 항목 비고 */
    private String itemRemark;
}

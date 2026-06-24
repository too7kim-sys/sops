package egovframework.ops.check.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 운영점검 VO.
 *
 * <p>응용프로그램 표준운영절차의 운영점검(일일/정기 점검) 단위정보.
 * 점검헤더와 점검항목(1:N)을 함께 보관하며, 항목 중 하나라도 이상이면
 * 종합결과를 ABNORMAL 로 산정한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CheckVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 점검 ID */
    private Long chkId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 점검 유형 (DAILY/WEEKLY/MONTHLY) */
    private String chkType;

    /** 점검 유형명 (조인) */
    private String chkTypeNm;

    /** 점검 일자 (YYYY-MM-DD) */
    private String chkDt;

    /** 점검자 ID */
    private String chkrId;

    /** 종합 결과 (NORMAL/ABNORMAL) */
    private String result;

    /** 종합 결과명 (조인) */
    private String resultNm;

    /** 비고 */
    private String remark;

    /** 등록 일시 */
    private String regDt;

    /** 점검항목 수 (목록 조회) */
    private int itemCnt;

    /** 이상 항목 수 (목록 조회) */
    private int abnormalCnt;

    /** 점검항목 목록 */
    private List<CheckItemVO> itemList;
}

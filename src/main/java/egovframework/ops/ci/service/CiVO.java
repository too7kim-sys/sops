package egovframework.ops.ci.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 형상관리 VO.
 *
 * <p>응용프로그램 표준운영절차의 형상항목(CI) 단위정보. 형상식별 → 기준선 →
 * 체크아웃/체크인 → 감사의 형상통제 상태와 이력을 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CiVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 형상 ID */
    private Long ciId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 형상항목명 */
    private String ciNm;

    /** 형상 유형 (SOURCE/LIBRARY/DOCUMENT/CONFIG/DB) */
    private String ciType;

    /** 형상 유형명 (조인) */
    private String ciTypeNm;

    /** 버전 */
    private String ver;

    /** 형상 상태 (IDENTIFIED/BASELINED/CHECKED_OUT/CHECKED_IN) */
    private String ciStatus;

    /** 형상 상태명 (조인) */
    private String ciStatusNm;

    /** 저장 위치 */
    private String location;

    /** 담당자 ID */
    private String ownerId;

    /** 형상 설명 */
    private String ciDesc;

    /** 등록 일시 */
    private String regDt;

    /** 형상이력 목록 */
    private List<CiHisVO> historyList;

    /** [처리입력] 형상통제/감사 변경유형 (IDENTIFY/CHECKOUT/CHECKIN/BASELINE/AUDIT) */
    private String chgType;

    /** [처리입력] 형상통제/감사 내용 */
    private String content;
}

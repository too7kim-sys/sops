package egovframework.ops.csr.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 요청관리(CSR) VO.
 *
 * <p>응용프로그램 표준운영절차의 요청관리 단위정보. 요청 → 접수 → 분류 →
 * 처리 → 종결의 처리상태와 이력을 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CsrVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 요청 ID */
    private Long csrId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 요청 제목 */
    private String title;

    /** 요청 내용 */
    private String content;

    /** 요청 유형 (INQUIRY/CHANGE/IMPROVE/WORK) */
    private String csrType;

    /** 요청 유형명 (조인) */
    private String csrTypeNm;

    /** 우선순위 (HIGH/MID/LOW) */
    private String priority;

    /** 우선순위명 (조인) */
    private String priorityNm;

    /** 처리 상태 (REQUESTED/RECEIVED/CLASSIFIED/IN_PROGRESS/PROCESSED/CLOSED/REJECTED) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 요청자 ID */
    private String reqId;

    /** 요청 일시 */
    private String reqDt;

    /** 처리 담당자 ID */
    private String chargerId;

    /** 처리 내용 */
    private String procContent;

    /** 처리 일시 */
    private String procDt;

    /** 연계 유형 (INCIDENT/CHANGE) */
    private String linkedType;

    /** 연계 ID */
    private Long linkedId;

    /** 만족도 */
    private String satisfaction;

    /** 등록 일시 */
    private String regDt;

    /** 처리이력 목록 */
    private List<CsrHisVO> historyList;
}

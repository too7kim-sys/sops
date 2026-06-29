package egovframework.ops.csr.service;

import egovframework.com.cmm.ComDefaultVO;
import egovframework.ops.system.service.SystemVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
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

    /** 대표 응용시스템 ID (다중 선택 시 첫번째) */
    private String sysId;

    /** 대표 응용시스템 명 (조인) */
    private String sysNm;

    /** 대상 응용시스템 ID 목록 (다중 선택 — 폼 바인딩/수정 선택) */
    private List<String> sysIds = new ArrayList<>();

    /** 대상 응용시스템 목록 (조인 — 상세 표시) */
    private List<SystemVO> sysList = new ArrayList<>();

    /** 대상 응용시스템 수 (목록 표시 '외 N') */
    private int sysCnt;

    /** 요청 제목 */
    private String title;

    /** 요청 내용 */
    private String content;

    /** 요청 대분류 (CSR_TYPE 코드 : GENERAL/INCIDENT/CHANGE/BACKUP/CONFIG) */
    private String csrType;

    /** 요청 대분류명 (조인) */
    private String csrTypeNm;

    /** 요청 소분류 (CSR_SUBTYPE 코드, UPPER_CODE=CSR_TYPE) */
    private String csrSubType;

    /** 요청 소분류명 (조인) */
    private String csrSubTypeNm;

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

    /** 완료요구일 (YYYY-MM-DD) */
    private String dueDt;

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

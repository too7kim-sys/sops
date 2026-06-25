package egovframework.ops.event.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 운영상태관리(이벤트) VO.
 *
 * <p>응용프로그램 표준운영절차의 운영상태(이벤트) 단위정보. 감지 → 분석 → 조치 →
 * 처리완료/에스컬레이션의 처리상태와 이력을 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EventVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 이벤트 ID */
    private Long evtId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 이벤트 제목 */
    private String title;

    /** 이벤트 유형 (CPU/MEMORY/DISK/PROCESS/NETWORK/APP) */
    private String evtType;

    /** 이벤트 유형명 (조인) */
    private String evtTypeNm;

    /** 심각도 (INFO/WARN/CRITICAL) */
    private String severity;

    /** 심각도명 (조인) */
    private String severityNm;

    /** 처리 상태 (DETECTED/ANALYZING/ACTING/HANDLED/ESCALATED/CLOSED) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 발생 일시 */
    private String occrDt;

    /** 이벤트 내용 */
    private String content;

    /** 조치 내용 */
    private String action;

    /** 처리 담당자 ID */
    private String chargerId;

    /** 연계 장애 ID (에스컬레이션 시) */
    private Long linkedIncId;

    /** 등록 일시 */
    private String regDt;

    /** 처리이력 목록 */
    private List<EventHisVO> historyList;
}

package egovframework.ops.incident.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 장애관리 VO.
 *
 * <p>응용프로그램 표준운영절차의 장애관리 단위정보. 접수 → 원인분석 → 조치 →
 * 종결의 처리상태와 이력을 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IncidentVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 장애 ID */
    private Long incId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 장애 제목 */
    private String title;

    /** 장애 내용 */
    private String content;

    /** 장애 등급 (1~4) */
    private String severity;

    /** 처리 상태 (RECEIVED/ANALYZING/ACTING/RESOLVED/CLOSED) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 발생 일시 */
    private String occrDt;

    /** 접수 일시 */
    private String rcptDt;

    /** 조치완료 일시 */
    private String resolveDt;

    /** 장애 원인 */
    private String cause;

    /** 조치 내용 */
    private String action;

    /** 처리 담당자 ID */
    private String chargerId;

    /** 등록자 ID */
    private String regId;

    /** 등록 일시 */
    private String regDt;

    /** 목표복구일시 (SLA) */
    private String targetResolveDt;

    /** 연계 문제 ID */
    private Long refPrbId;

    /** SLA 상태 (transient: 준수/위반/진행중/지연) */
    private String slaStatus;

    /** 처리이력 목록 */
    private List<IncidentHisVO> historyList;

    /** 에스컬레이션 목록 */
    private List<IncidentEscalVO> escalList;
}

package egovframework.ops.problem.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 문제관리 VO.
 *
 * <p>응용프로그램 표준운영절차의 문제관리 단위정보. 등록 → 분석 → 원인규명 →
 * 해결 → 종결의 처리상태/이력과 연계장애·KEDB(알려진 오류)를 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProblemVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 문제 ID */
    private Long prbId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 문제 제목 */
    private String title;

    /** 문제 내용 */
    private String content;

    /** 우선순위 (HIGH/MID/LOW) */
    private String priority;

    /** 우선순위명 (조인) */
    private String priorityNm;

    /** 처리 상태 (REGISTERED/ANALYZING/IDENTIFIED/RESOLVING/RESOLVED/CLOSED) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 근본 원인 */
    private String rootCause;

    /** 해결책 */
    private String solution;

    /** 등록자 ID */
    private String regId;

    /** 등록 일시 */
    private String regDt;

    /** 처리 담당자 ID */
    private String chargerId;

    /** 해결 일시 */
    private String resolveDt;

    /** 처리이력 목록 */
    private List<ProblemHisVO> historyList;

    /** 연계 장애 목록 (incId, title, statusNm) */
    private List<Map<String, Object>> incList;

    /** 알려진 오류(KEDB) 목록 */
    private List<KedbVO> kedbList;
}

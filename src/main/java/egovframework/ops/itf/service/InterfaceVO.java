package egovframework.ops.itf.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 연계관리 VO.
 *
 * <p>응용프로그램 표준운영절차의 연계관리 단위정보. 요청 → 검토 → 계획 →
 * 작업 → 테스트 → 완료의 처리상태와 이력을 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InterfaceVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 연계 ID */
    private Long intfId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 연계 제목 */
    private String title;

    /** 상대 시스템 */
    private String partnerSys;

    /** 연계 유형 (SYNC/ASYNC/BATCH/API) */
    private String ifType;

    /** 연계 유형명 (조인) */
    private String ifTypeNm;

    /** 연계 데이터 설명 */
    private String dataDesc;

    /** 처리 상태 (REQUESTED/REVIEWING/PLANNING/WORKING/TESTING/COMPLETED/REJECTED) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 요청자 ID */
    private String reqId;

    /** 요청 일시 */
    private String reqDt;

    /** 담당자 ID */
    private String chargerId;

    /** 예정일 (YYYY-MM-DD) */
    private String planDt;

    /** 완료 일시 */
    private String completeDt;

    /** 처리 결과 */
    private String result;

    /** 등록 일시 */
    private String regDt;

    /** 처리이력 목록 */
    private List<InterfaceHisVO> historyList;
}

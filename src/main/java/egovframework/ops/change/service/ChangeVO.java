package egovframework.ops.change.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 변경관리 VO.
 *
 * <p>응용프로그램 표준운영절차의 변경관리 단위정보. 변경요청 → 심의/승인 →
 * 적용/완료의 처리상태와 이력을 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChangeVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 변경 ID */
    private Long chgId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 변경 제목 */
    private String title;

    /** 변경 유형 (PROGRAM/DATABASE/CONFIG/EMERGENCY) */
    private String chgType;

    /** 변경 유형명 (조인) */
    private String chgTypeNm;

    /** 변경 사유 */
    private String reason;

    /** 변경 내용 */
    private String content;

    /** 처리 상태 (REQUESTED/REVIEWING/APPROVED/REJECTED/APPLIED/COMPLETED) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 요청자 ID */
    private String reqId;

    /** 요청 일시 */
    private String reqDt;

    /** 승인자(심의자) ID */
    private String apprId;

    /** 승인(심의) 일시 */
    private String apprDt;

    /** 심의 의견 */
    private String apprOpinion;

    /** 적용 예정일 */
    private String planDt;

    /** 적용 일시 */
    private String applyDt;

    /** 등록 일시 */
    private String regDt;

    /** 이행후검토(PIR) 내용 */
    private String pirContent;

    /** 이행후검토(PIR) 일시 */
    private String pirDt;

    /** CAB(변경자문위원회) 심의 이력 목록 */
    private List<ChangeCabVO> cabList;
}

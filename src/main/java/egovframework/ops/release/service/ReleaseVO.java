package egovframework.ops.release.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 배포관리 VO.
 *
 * <p>응용프로그램 표준운영절차의 배포관리 단위정보. 배포계획 → 승인 → 배포 →
 * 완료/롤백의 처리상태와 결과를 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReleaseVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 배포 ID */
    private Long relId;

    /** 응용시스템 ID */
    private String sysId;

    /** 응용시스템 명 (조인) */
    private String sysNm;

    /** 연계 변경요청 ID */
    private Long chgId;

    /** 배포 버전 */
    private String ver;

    /** 배포 제목 */
    private String title;

    /** 배포 내용 */
    private String content;

    /** 처리 상태 (PLANNED/APPROVED/DEPLOYING/DEPLOYED/ROLLBACK) */
    private String status;

    /** 처리 상태명 (조인) */
    private String statusNm;

    /** 배포 예정 일시 */
    private String planDt;

    /** 배포 일시 */
    private String deployDt;

    /** 처리 담당자 ID */
    private String chargerId;

    /** 배포 결과 */
    private String result;

    /** 등록 일시 */
    private String regDt;
}

package egovframework.ops.release.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 배포 항목 VO.
 *
 * <p>배포 단위에 포함되는 모듈/파일 등 세부 항목과 그 처리결과를 보관한다.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReleaseItemVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 배포 항목 ID */
    private Long ritemId;

    /** 배포 ID */
    private Long relId;

    /** 항목 명 */
    private String itemNm;

    /** 항목 설명 */
    private String itemDesc;

    /** 항목 결과 (SUCCESS/FAIL/SKIP) */
    private String itemResult;

    /** 항목 결과명 (조인) */
    private String itemResultNm;
}

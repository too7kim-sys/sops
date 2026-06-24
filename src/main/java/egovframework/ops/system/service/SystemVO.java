package egovframework.ops.system.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 응용시스템(운영대상) 마스터 VO.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 시스템 ID */
    private String sysId;

    /** 시스템 명 */
    private String sysNm;

    /** 시스템 설명 */
    private String sysDesc;

    /** 운영담당자 */
    private String mngrNm;

    /** 운영부서 */
    private String mngrDept;

    /** 중요도 등급 (1~3) */
    private String grad;

    /** 사용여부 */
    private String useAt;

    /** 등록일시 */
    private String regDt;
}

package egovframework.ops.problem.service;

import lombok.Data;

/**
 * 알려진 오류(KEDB, Known Error Database) VO.
 *
 * <p>문제관리에서 식별된 알려진 오류의 증상/원인/임시조치(Workaround)/해결책을 보관한다.</p>
 */
@Data
public class KedbVO {

    /** KEDB ID */
    private Long kedbId;

    /** 연관 문제 ID (nullable) */
    private Long prbId;

    /** 제목 */
    private String title;

    /** 증상 */
    private String symptom;

    /** 원인 */
    private String cause;

    /** 임시조치(Workaround) */
    private String workaround;

    /** 해결책 */
    private String solution;

    /** 등록 일시 */
    private String regDt;
}

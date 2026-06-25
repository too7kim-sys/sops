package egovframework.ops.deploy.service;

import lombok.Data;

/**
 * Git 자동배포 실행 이력 VO.
 */
@Data
public class DeployHisVO {

    /** 배포실행 ID */
    private Long deployId;

    /** 배포(릴리즈) ID */
    private Long relId;

    /** 배포 ref(태그/브랜치/커밋) */
    private String deployRef;

    /** 체크아웃 커밋 해시 */
    private String commitHash;

    /** 결과 (SUCCESS/FAIL) */
    private String result;

    /** 실행 구분 (DEPLOY/ROLLBACK) */
    private String deployType;

    /** 실행 로그 (체크아웃 + 스크립트 출력) */
    private String log;

    /** 실행자 */
    private String execBy;

    /** 실행 일시 */
    private String execDt;
}

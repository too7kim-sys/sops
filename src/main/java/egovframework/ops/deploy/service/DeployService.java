package egovframework.ops.deploy.service;

import java.util.List;

/**
 * Git 자동배포 서비스 인터페이스.
 */
public interface DeployService {

    /**
     * 배포 실행(체크아웃 + 스크립트).
     *
     * @param relId      배포(릴리즈) ID
     * @param ref        배포 ref(태그/브랜치/커밋). 비어있으면 릴리즈 버전 또는 시스템 기본 브랜치 사용
     * @param deployType DEPLOY / ROLLBACK
     * @param execBy     실행자 ID
     * @return 실행 이력(결과 포함)
     */
    DeployHisVO deploy(Long relId, String ref, String deployType, String execBy);

    /** 배포 실행 이력 목록 */
    List<DeployHisVO> selectDeployHisList(Long relId);
}

package egovframework.ops.deploy.service.impl;

import egovframework.ops.deploy.service.DeployHisVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Git 자동배포 MyBatis Mapper.
 */
@Mapper
public interface DeployMapper {

    List<DeployHisVO> selectDeployHisList(Long relId);

    void insertDeployHis(DeployHisVO vo);

    /** 비동기 실행 완료 후 실행 이력 갱신(결과/커밋/로그) */
    void updateDeployHis(DeployHisVO vo);

    /** 배포 실행 결과를 릴리즈에 반영 */
    void updateReleaseDeploy(@Param("relId") Long relId,
                             @Param("deployRef") String deployRef,
                             @Param("commitHash") String commitHash,
                             @Param("log") String log,
                             @Param("status") String status);
}

package egovframework.ops.deploy.service.impl;

import egovframework.ops.deploy.service.DeployHisVO;
import egovframework.ops.deploy.service.GitDeployManager;
import egovframework.ops.deploy.service.SvnDeployManager;
import egovframework.ops.release.service.ReleaseVO;
import egovframework.ops.system.service.SystemVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Git 자동배포 비동기 실행기.
 *
 * <p>RUNNING 상태로 적재된 배포 실행 이력을 받아 별도 스레드에서 체크아웃·스크립트를
 * 수행하고, 완료 시 실행 이력과 릴리즈 상태를 갱신한다. (자기호출 비동기 한계를
 * 피하기 위해 서비스와 분리된 별도 빈으로 둔다.)</p>
 */
@Component
public class DeployExecutor {

    private static final Logger log = LoggerFactory.getLogger(DeployExecutor.class);

    private final DeployMapper deployMapper;
    private final GitDeployManager gitDeployManager;
    private final SvnDeployManager svnDeployManager;

    @Value("${ops.deploy.workspace:${java.io.tmpdir}/egov-sop/deploy}")
    private String workspace;

    @Value("${ops.deploy.script-timeout-sec:60}")
    private int scriptTimeoutSec;

    public DeployExecutor(DeployMapper deployMapper, GitDeployManager gitDeployManager,
                          SvnDeployManager svnDeployManager) {
        this.deployMapper = deployMapper;
        this.gitDeployManager = gitDeployManager;
        this.svnDeployManager = svnDeployManager;
    }

    /**
     * 비동기 배포 실행. {@code deployId} 의 실행 이력을 완료 상태로 갱신하고 릴리즈에 반영한다.
     */
    @Async
    public void execute(Long deployId, ReleaseVO rel, SystemVO sys, String refToUse, String type) {
        File dir = (sys.getDeployPath() != null && !sys.getDeployPath().isBlank())
                ? new File(sys.getDeployPath())
                : new File(workspace, sys.getSysId());
        if (dir.getParentFile() != null) {
            dir.getParentFile().mkdirs();
        }

        Map<String, String> env = new HashMap<>();
        env.put("SYS_ID", sys.getSysId());
        env.put("VER", rel.getVer() == null ? "" : rel.getVer());
        env.put("REF", refToUse == null ? "" : refToUse);
        env.put("DEPLOY_PATH", dir.getAbsolutePath());
        env.put("DEPLOY_TYPE", type);

        // 형상관리 유형(GIT/SVN)에 따라 체크아웃 방식 분기
        boolean isSvn = "SVN".equalsIgnoreCase(sys.getVcsType());
        env.put("VCS_TYPE", isSvn ? "SVN" : "GIT");
        GitDeployManager.Outcome outcome = isSvn
                ? svnDeployManager.run(sys.getGitUrl(), refToUse, dir,
                        sys.getDeployScript(), scriptTimeoutSec, env)
                : gitDeployManager.run(sys.getGitUrl(), sys.getGitBranch(), refToUse, dir,
                        sys.getDeployScript(), scriptTimeoutSec, env);

        String result = outcome.isSuccess() ? "SUCCESS" : "FAIL";
        String logText = truncate(outcome.getLogText(), 3900);

        // 실행 이력 갱신
        DeployHisVO upd = new DeployHisVO();
        upd.setDeployId(deployId);
        upd.setResult(result);
        upd.setCommitHash(outcome.getCommitHash());
        upd.setLog(logText);
        deployMapper.updateDeployHis(upd);

        // 릴리즈 반영 : 성공 시 상태 전이
        String newStatus = null;
        if (outcome.isSuccess()) {
            newStatus = "ROLLBACK".equals(type) ? "ROLLBACK" : "DEPLOYED";
        }
        deployMapper.updateReleaseDeploy(rel.getRelId(), refToUse,
                outcome.getCommitHash(), logText, newStatus);

        log.debug("Git 배포 완료 : REL-{} ref={} result={}", rel.getRelId(), refToUse, result);
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max) + "\n...(생략)";
    }
}

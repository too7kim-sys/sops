package egovframework.ops.deploy.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.deploy.service.DeployHisVO;
import egovframework.ops.deploy.service.DeployService;
import egovframework.ops.deploy.service.GitDeployManager;
import egovframework.ops.release.service.EgovReleaseService;
import egovframework.ops.release.service.ReleaseVO;
import egovframework.ops.system.service.EgovSystemService;
import egovframework.ops.system.service.SystemVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Git 자동배포 서비스 구현체.
 *
 * <p>릴리즈와 응용시스템의 git 설정을 읽어 {@link GitDeployManager} 로 체크아웃·스크립트
 * 배포를 수행하고, 실행 이력 적재 및 릴리즈 상태를 갱신한다.</p>
 */
@Service("egovDeployService")
public class EgovDeployServiceImpl extends EgovAbstractServiceImpl implements DeployService {

    private final DeployMapper deployMapper;
    private final EgovReleaseService releaseService;
    private final EgovSystemService systemService;
    private final GitDeployManager gitDeployManager;

    @Value("${ops.deploy.workspace:${java.io.tmpdir}/egov-ops/deploy}")
    private String workspace;

    @Value("${ops.deploy.script-timeout-sec:60}")
    private int scriptTimeoutSec;

    public EgovDeployServiceImpl(DeployMapper deployMapper,
                                 EgovReleaseService releaseService,
                                 EgovSystemService systemService,
                                 GitDeployManager gitDeployManager) {
        this.deployMapper = deployMapper;
        this.releaseService = releaseService;
        this.systemService = systemService;
        this.gitDeployManager = gitDeployManager;
    }

    @Override
    public List<DeployHisVO> selectDeployHisList(Long relId) {
        return deployMapper.selectDeployHisList(relId);
    }

    @Override
    @Transactional
    public DeployHisVO deploy(Long relId, String ref, String deployType, String execBy) {
        ReleaseVO rel = releaseService.selectRelease(relId);
        SystemVO sys = systemService.selectSystem(rel.getSysId());

        String type = (deployType == null || deployType.isBlank()) ? "DEPLOY" : deployType;
        DeployHisVO his = new DeployHisVO();
        his.setRelId(relId);
        his.setDeployType(type);
        his.setExecBy(execBy);

        // git 미설정 시 즉시 실패 기록
        if (sys == null || sys.getGitUrl() == null || sys.getGitUrl().isBlank()) {
            his.setResult("FAIL");
            his.setDeployRef(ref);
            his.setLog("[error] 응용시스템에 git 저장소(GIT_URL)가 설정되지 않았습니다.");
            deployMapper.insertDeployHis(his);
            return his;
        }

        String refToUse = (ref != null && !ref.isBlank()) ? ref
                : (rel.getVer() != null && !rel.getVer().isBlank() ? rel.getVer() : sys.getGitBranch());

        File dir = (sys.getDeployPath() != null && !sys.getDeployPath().isBlank())
                ? new File(sys.getDeployPath())
                : new File(workspace, sys.getSysId());
        dir.getParentFile().mkdirs();

        Map<String, String> env = new HashMap<>();
        env.put("SYS_ID", sys.getSysId());
        env.put("VER", rel.getVer() == null ? "" : rel.getVer());
        env.put("REF", refToUse == null ? "" : refToUse);
        env.put("DEPLOY_PATH", dir.getAbsolutePath());
        env.put("DEPLOY_TYPE", type);

        GitDeployManager.Outcome outcome = gitDeployManager.run(
                sys.getGitUrl(), sys.getGitBranch(), refToUse, dir,
                sys.getDeployScript(), scriptTimeoutSec, env);

        his.setDeployRef(refToUse);
        his.setCommitHash(outcome.getCommitHash());
        his.setResult(outcome.isSuccess() ? "SUCCESS" : "FAIL");
        his.setLog(truncate(outcome.getLogText(), 3900));
        deployMapper.insertDeployHis(his);

        // 릴리즈 반영 : 성공 시 상태 전이(DEPLOY→DEPLOYED, ROLLBACK→ROLLBACK)
        String newStatus = null;
        if (outcome.isSuccess()) {
            newStatus = "ROLLBACK".equals(type) ? "ROLLBACK" : "DEPLOYED";
        }
        deployMapper.updateReleaseDeploy(relId, refToUse,
                outcome.getCommitHash(), truncate(outcome.getLogText(), 3900), newStatus);

        log.debug("Git 배포 : REL-{} ref={} result={}", relId, refToUse, his.getResult());
        return his;
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max) + "\n...(생략)";
    }
}

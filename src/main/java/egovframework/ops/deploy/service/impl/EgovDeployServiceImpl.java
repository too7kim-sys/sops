package egovframework.ops.deploy.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.deploy.service.DeployHisVO;
import egovframework.ops.deploy.service.DeployService;
import egovframework.ops.release.service.EgovReleaseService;
import egovframework.ops.release.service.ReleaseVO;
import egovframework.ops.system.service.EgovSystemService;
import egovframework.ops.system.service.SystemVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Git 자동배포 서비스 구현체.
 *
 * <p>배포 승인 상태 게이트를 검증한 뒤, 실행 이력을 RUNNING 으로 적재하고 비동기 실행기로
 * 체크아웃·스크립트 배포를 위임한다(논블로킹).</p>
 */
@Service("egovDeployService")
public class EgovDeployServiceImpl extends EgovAbstractServiceImpl implements DeployService {

    /** 배포(DEPLOY) 가능 상태 : 승인 이후 단계 */
    private static final Set<String> DEPLOYABLE =
            Set.of("APPROVED", "DEPLOYING", "VERIFYING", "STABILIZING", "DEPLOYED");
    /** 롤백(ROLLBACK) 가능 상태 : 배포 이력이 있는 단계 */
    private static final Set<String> ROLLBACKABLE =
            Set.of("DEPLOYING", "VERIFYING", "STABILIZING", "DEPLOYED", "ROLLBACK");

    private final DeployMapper deployMapper;
    private final EgovReleaseService releaseService;
    private final EgovSystemService systemService;
    private final DeployExecutor deployExecutor;

    public EgovDeployServiceImpl(DeployMapper deployMapper,
                                 EgovReleaseService releaseService,
                                 EgovSystemService systemService,
                                 DeployExecutor deployExecutor) {
        this.deployMapper = deployMapper;
        this.releaseService = releaseService;
        this.systemService = systemService;
        this.deployExecutor = deployExecutor;
    }

    @Override
    public List<DeployHisVO> selectDeployHisList(Long relId) {
        return deployMapper.selectDeployHisList(relId);
    }

    @Override
    public DeployHisVO deploy(Long relId, String ref, String deployType, String execBy) {
        ReleaseVO rel = releaseService.selectRelease(relId);
        SystemVO sys = (rel == null) ? null : systemService.selectSystem(rel.getSysId());
        String type = (deployType == null || deployType.isBlank()) ? "DEPLOY" : deployType;

        DeployHisVO his = new DeployHisVO();
        his.setRelId(relId);
        his.setDeployType(type);
        his.setExecBy(execBy);

        // 1) git 설정 게이트
        if (sys == null || sys.getGitUrl() == null || sys.getGitUrl().isBlank()) {
            his.setResult("BLOCKED");
            his.setLog("대상 시스템에 Git 저장소(GIT_URL)가 설정되지 않았습니다.");
            return his;
        }
        // 2) 승인 상태 게이트
        String status = rel.getStatus();
        Set<String> allowed = "ROLLBACK".equals(type) ? ROLLBACKABLE : DEPLOYABLE;
        if (status == null || !allowed.contains(status)) {
            his.setResult("BLOCKED");
            his.setLog("ROLLBACK".equals(type)
                    ? "배포 이력이 있는 상태에서만 롤백할 수 있습니다. (현재상태: " + status + ")"
                    : "배포 승인(APPROVED) 이후에만 실행할 수 있습니다. (현재상태: " + status + ")");
            return his;
        }

        String refToUse = (ref != null && !ref.isBlank()) ? ref
                : (rel.getVer() != null && !rel.getVer().isBlank() ? rel.getVer() : sys.getGitBranch());

        // 3) RUNNING 이력 적재 후 비동기 실행 위임
        his.setResult("RUNNING");
        his.setDeployRef(refToUse);
        deployMapper.insertDeployHis(his);
        deployExecutor.execute(his.getDeployId(), rel, sys, refToUse, type);

        log.debug("Git 배포 시작(비동기) : REL-{} ref={} type={}", relId, refToUse, type);
        return his;
    }
}

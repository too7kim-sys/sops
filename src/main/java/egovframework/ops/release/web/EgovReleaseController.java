package egovframework.ops.release.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.deploy.service.DeployService;
import egovframework.ops.release.service.EgovReleaseService;
import egovframework.ops.release.service.ReleaseItemVO;
import egovframework.ops.release.service.ReleaseVO;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 배포관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 배포계획 등록/조회/처리/완료 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/release")
public class EgovReleaseController {

    private final EgovReleaseService releaseService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;
    private final EgovApprService apprService;
    private final DeployService deployService;

    public EgovReleaseController(EgovReleaseService releaseService,
                                 EgovSystemService systemService,
                                 EgovCodeService codeService,
                                 DeployService deployService,
                                EgovApprService apprService) {
        this.releaseService = releaseService;
        this.systemService = systemService;
        this.codeService = codeService;
        this.apprService = apprService;
        this.deployService = deployService;
    }

    /** 배포 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") ReleaseVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = releaseService.selectReleaseListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("releaseList", releaseService.selectReleaseList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("RELEASE_STATUS"));
        model.addAttribute("menu", "release");
        return "release/list";
    }

    /** 배포 상세 */
    @GetMapping("/detail/{relId}")
    public String detail(@PathVariable Long relId, Model model) {
        ReleaseVO release = releaseService.selectRelease(relId);
        model.addAttribute("release", release);
        model.addAttribute("statusList", codeService.selectCodeList("RELEASE_STATUS"));
        model.addAttribute("itemResultList", codeService.selectCodeList("RELEASE_ITEM_RESULT"));
        // 결재선 승인/검토 완료 여부 — 미완료 시 배포 처리 상태를 '배포계획'으로 고정(전이 잠금)
        model.addAttribute("apprPassed", isApprPassed(relId));
        // Git 자동배포 : 대상 시스템 git 설정 및 배포 실행 이력
        if (release != null) {
            model.addAttribute("system", systemService.selectSystem(release.getSysId()));
            model.addAttribute("deployHisList", deployService.selectDeployHisList(relId));
        }
        model.addAttribute("menu", "release");
        return "release/detail";
    }

    /**
     * 결재선 통과 여부 — 결재선의 모든 검토(REVIEW) 라인이 REVIEWED, 모든 승인(APPROVE)
     * 라인이 APPROVED 여야 통과로 본다. 검토·승인 라인이 하나도 없으면(결재선 미구성)
     * 잠글 게이트가 없으므로 통과로 간주한다.
     * 통과 전에는 배포 처리 상태를 '배포계획(PLANNED)'으로만 둘 수 있고, 통과 후에야
     * 배포중·배포완료 등으로 상태를 변경할 수 있다.
     */
    private boolean isApprPassed(Long relId) {
        for (egovframework.ops.appr.service.ApprLineVO ln : apprService.selectLineList("RELEASE", relId)) {
            if ("REVIEW".equals(ln.getLineType()) && !"REVIEWED".equals(ln.getStatus())) {
                return false;
            }
            if ("APPROVE".equals(ln.getLineType()) && !"APPROVED".equals(ln.getStatus())) {
                return false;
            }
        }
        return true;
    }

    /** Git 자동배포 실행 (비동기) */
    @PostMapping("/deploy")
    public String deploy(@RequestParam Long relId,
                         @RequestParam(required = false) String ref,
                         @RequestParam(required = false, defaultValue = "DEPLOY") String deployType,
                         @AuthenticationPrincipal LoginUser loginUser,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        egovframework.ops.deploy.service.DeployHisVO his =
                deployService.deploy(relId, ref, deployType, loginUser.getUsername());
        if (his != null && "BLOCKED".equals(his.getResult())) {
            ra.addFlashAttribute("deployMsg", "[배포 차단] " + his.getLog());
            ra.addFlashAttribute("deployMsgType", "error");
        } else {
            ra.addFlashAttribute("deployMsg", "Git 배포를 시작했습니다. 잠시 후 새로고침하여 결과를 확인하세요.");
            ra.addFlashAttribute("deployMsgType", "info");
        }
        return "redirect:/release/detail/" + relId;
    }

    /** 배포계획 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("release", new ReleaseVO());
        addFormCodes(model);
        model.addAttribute("menu", "release");
        return "release/form";
    }

    /** 배포계획 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute ReleaseVO releaseVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        releaseVO.setChargerId(loginUser.getUsername());
        releaseService.insertRelease(releaseVO);
        // 결재 기본설정(템플릿) 자동 적용 — 결재/검토/공유/처리자 라인을 기본설정에 따라 생성
        apprService.applyTemplate("RELEASE", releaseVO.getRelId(), loginUser.getUsername());
        return "redirect:/release/detail/" + releaseVO.getRelId();
    }

    /** 배포 기본정보 수정 폼 */
    @GetMapping("/edit/{relId}")
    public String editForm(@PathVariable Long relId, Model model) {
        model.addAttribute("release", releaseService.selectRelease(relId));
        addFormCodes(model);
        model.addAttribute("menu", "release");
        return "release/form";
    }

    /** 배포 기본정보 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute ReleaseVO releaseVO) {
        releaseService.updateRelease(releaseVO);
        return "redirect:/release/detail/" + releaseVO.getRelId();
    }

    /** 배포 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute ReleaseVO releaseVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (releaseVO.getChargerId() == null || releaseVO.getChargerId().isBlank()) {
            releaseVO.setChargerId(loginUser.getUsername());
        }
        // 결재선 승인/검토 미완료 시에는 상태 전이를 '배포계획'으로 강제(화면 우회 제출 차단)
        if (!isApprPassed(releaseVO.getRelId())) {
            releaseVO.setStatus("PLANNED");
        }
        releaseService.processRelease(releaseVO);
        return "redirect:/release/detail/" + releaseVO.getRelId();
    }

    /** 배포 항목 등록 */
    @PostMapping("/item")
    public String addItem(@ModelAttribute ReleaseItemVO releaseItemVO) {
        releaseService.addReleaseItem(releaseItemVO);
        return "redirect:/release/detail/" + releaseItemVO.getRelId();
    }

    /** 배포 삭제 */
    @PostMapping("/delete/{relId}")
    public String delete(@PathVariable Long relId) {
        releaseService.deleteRelease(relId);
        return "redirect:/release/list";
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("RELEASE_STATUS"));
    }
}

package egovframework.ops.release.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.release.service.EgovReleaseService;
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

    public EgovReleaseController(EgovReleaseService releaseService,
                                 EgovSystemService systemService,
                                 EgovCodeService codeService) {
        this.releaseService = releaseService;
        this.systemService = systemService;
        this.codeService = codeService;
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
        model.addAttribute("release", releaseService.selectRelease(relId));
        model.addAttribute("statusList", codeService.selectCodeList("RELEASE_STATUS"));
        model.addAttribute("menu", "release");
        return "release/detail";
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
        releaseService.processRelease(releaseVO);
        return "redirect:/release/detail/" + releaseVO.getRelId();
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

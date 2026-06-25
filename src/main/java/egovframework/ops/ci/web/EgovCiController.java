package egovframework.ops.ci.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.ci.service.CiVO;
import egovframework.ops.ci.service.EgovCiService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 형상관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 형상식별/조회/형상통제(체크아웃·체크인·기준선·감사) 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/ci")
public class EgovCiController {

    private final EgovCiService ciService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;

    public EgovCiController(EgovCiService ciService,
                            EgovSystemService systemService,
                            EgovCodeService codeService) {
        this.ciService = ciService;
        this.systemService = systemService;
        this.codeService = codeService;
    }

    /** 형상항목 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") CiVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = ciService.selectCiListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("ciList", ciService.selectCiList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("CI_STATUS"));
        model.addAttribute("menu", "ci");
        return "ci/list";
    }

    /** 형상항목 상세 */
    @GetMapping("/detail/{ciId}")
    public String detail(@PathVariable Long ciId, Model model) {
        model.addAttribute("ci", ciService.selectCi(ciId));
        model.addAttribute("chgTypeList", codeService.selectCodeList("CI_CHG_TYPE"));
        model.addAttribute("menu", "ci");
        return "ci/detail";
    }

    /** 형상항목 식별(등록) 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("ci", new CiVO());
        addFormCodes(model);
        model.addAttribute("menu", "ci");
        return "ci/form";
    }

    /** 형상항목 식별 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute CiVO ciVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        if (ciVO.getOwnerId() == null || ciVO.getOwnerId().isBlank()) {
            ciVO.setOwnerId(loginUser.getUsername());
        }
        ciService.insertCi(ciVO);
        return "redirect:/ci/detail/" + ciVO.getCiId();
    }

    /** 형상항목 기본정보 수정 폼 */
    @GetMapping("/edit/{ciId}")
    public String editForm(@PathVariable Long ciId, Model model) {
        model.addAttribute("ci", ciService.selectCi(ciId));
        addFormCodes(model);
        model.addAttribute("menu", "ci");
        return "ci/form";
    }

    /** 형상항목 기본정보 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute CiVO ciVO) {
        ciService.updateCi(ciVO);
        return "redirect:/ci/detail/" + ciVO.getCiId();
    }

    /** 형상통제/감사 */
    @PostMapping("/process")
    public String process(@ModelAttribute CiVO ciVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (ciVO.getOwnerId() == null || ciVO.getOwnerId().isBlank()) {
            ciVO.setOwnerId(loginUser.getUsername());
        }
        ciService.processCi(ciVO);
        return "redirect:/ci/detail/" + ciVO.getCiId();
    }

    /** 형상항목 삭제 */
    @PostMapping("/delete/{ciId}")
    public String delete(@PathVariable Long ciId) {
        ciService.deleteCi(ciId);
        return "redirect:/ci/list";
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("CI_TYPE"));
        model.addAttribute("statusList", codeService.selectCodeList("CI_STATUS"));
    }
}

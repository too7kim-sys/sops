package egovframework.ops.check.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.check.service.CheckVO;
import egovframework.ops.check.service.EgovCheckService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 운영점검 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 일일/정기 운영점검 등록/조회/삭제 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/check")
public class EgovCheckController {

    private final EgovCheckService checkService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;

    public EgovCheckController(EgovCheckService checkService,
                               EgovSystemService systemService,
                               EgovCodeService codeService) {
        this.checkService = checkService;
        this.systemService = systemService;
        this.codeService = codeService;
    }

    /** 운영점검 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") CheckVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = checkService.selectCheckListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("checkList", checkService.selectCheckList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("resultList", codeService.selectCodeList("CHECK_RESULT"));
        model.addAttribute("menu", "check");
        return "check/list";
    }

    /** 운영점검 상세 */
    @GetMapping("/detail/{chkId}")
    public String detail(@PathVariable Long chkId, Model model) {
        model.addAttribute("check", checkService.selectCheck(chkId));
        model.addAttribute("menu", "check");
        return "check/detail";
    }

    /** 운영점검 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("check", new CheckVO());
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("CHECK_TYPE"));
        model.addAttribute("resultList", codeService.selectCodeList("CHECK_RESULT"));
        model.addAttribute("menu", "check");
        return "check/form";
    }

    /** 운영점검 등록 처리 (헤더 + 점검항목 N개) */
    @PostMapping("/insert")
    public String insert(@ModelAttribute CheckVO checkVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        if (checkVO.getChkrId() == null || checkVO.getChkrId().isBlank()) {
            checkVO.setChkrId(loginUser.getUsername());
        }
        checkService.insertCheck(checkVO);
        return "redirect:/check/detail/" + checkVO.getChkId();
    }

    /** 운영점검 삭제 */
    @PostMapping("/delete/{chkId}")
    public String delete(@PathVariable Long chkId) {
        checkService.deleteCheck(chkId);
        return "redirect:/check/list";
    }
}

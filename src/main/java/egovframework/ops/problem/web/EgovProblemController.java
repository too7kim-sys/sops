package egovframework.ops.problem.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.problem.service.EgovProblemService;
import egovframework.ops.problem.service.KedbVO;
import egovframework.ops.problem.service.ProblemVO;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 문제관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 문제 등록/조회/처리/종결과 연계장애·KEDB 관리 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/problem")
public class EgovProblemController {

    private final EgovProblemService problemService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;
    private final EgovApprService apprService;

    public EgovProblemController(EgovProblemService problemService,
                                 EgovSystemService systemService,
                                 EgovCodeService codeService,
                                EgovApprService apprService) {
        this.problemService = problemService;
        this.systemService = systemService;
        this.codeService = codeService;
        this.apprService = apprService;
    }

    /** 문제 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") ProblemVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = problemService.selectProblemListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("problemList", problemService.selectProblemList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("PROBLEM_STATUS"));
        model.addAttribute("menu", "problem");
        return "problem/list";
    }

    /** 문제 상세 */
    @GetMapping("/detail/{prbId}")
    public String detail(@PathVariable Long prbId, Model model) {
        model.addAttribute("problem", problemService.selectProblem(prbId));
        model.addAttribute("statusList", codeService.selectCodeList("PROBLEM_STATUS"));
        model.addAttribute("menu", "problem");
        return "problem/detail";
    }

    /** 문제 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("problem", new ProblemVO());
        addFormCodes(model);
        model.addAttribute("menu", "problem");
        return "problem/form";
    }

    /** 문제 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute ProblemVO problemVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        problemVO.setRegId(loginUser.getUsername());
        problemService.insertProblem(problemVO);
        // 결재 기본설정(템플릿) 자동 적용 — 결재/검토/공유/처리자 라인을 기본설정에 따라 생성
        apprService.applyTemplate("PROBLEM", problemVO.getPrbId(), loginUser.getUsername());
        return "redirect:/problem/detail/" + problemVO.getPrbId();
    }

    /** 문제 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute ProblemVO problemVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (problemVO.getChargerId() == null || problemVO.getChargerId().isBlank()) {
            problemVO.setChargerId(loginUser.getUsername());
        }
        problemService.processProblem(problemVO);
        return "redirect:/problem/detail/" + problemVO.getPrbId();
    }

    /** 연계 장애 추가 */
    @PostMapping("/linkInc")
    public String linkInc(@RequestParam Long prbId, @RequestParam Long incId) {
        problemService.linkIncident(prbId, incId);
        return "redirect:/problem/detail/" + prbId;
    }

    /** KEDB(알려진 오류) 추가 */
    @PostMapping("/kedb")
    public String kedb(@ModelAttribute KedbVO kedbVO) {
        problemService.addKedb(kedbVO);
        return "redirect:/problem/detail/" + kedbVO.getPrbId();
    }

    /** 문제 삭제 */
    @PostMapping("/delete/{prbId}")
    public String delete(@PathVariable Long prbId) {
        problemService.deleteProblem(prbId);
        return "redirect:/problem/list";
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("priorityList", codeService.selectCodeList("PRIORITY"));
        model.addAttribute("statusList", codeService.selectCodeList("PROBLEM_STATUS"));
    }
}

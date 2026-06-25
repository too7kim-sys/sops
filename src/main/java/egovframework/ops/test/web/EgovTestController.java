package egovframework.ops.test.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.system.service.EgovSystemService;
import egovframework.ops.test.service.EgovTestService;
import egovframework.ops.test.service.TestVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 테스트관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 테스트 계획/수행/분석/종결 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/test")
public class EgovTestController {

    private final EgovTestService testService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;

    public EgovTestController(EgovTestService testService,
                              EgovSystemService systemService,
                              EgovCodeService codeService) {
        this.testService = testService;
        this.systemService = systemService;
        this.codeService = codeService;
    }

    /** 테스트 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") TestVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = testService.selectTestListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("testList", testService.selectTestList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("TEST_STATUS"));
        model.addAttribute("menu", "test");
        return "test/list";
    }

    /** 테스트 상세 */
    @GetMapping("/detail/{testId}")
    public String detail(@PathVariable Long testId, Model model) {
        model.addAttribute("test", testService.selectTest(testId));
        model.addAttribute("statusList", codeService.selectCodeList("TEST_STATUS"));
        model.addAttribute("menu", "test");
        return "test/detail";
    }

    /** 테스트 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("test", new TestVO());
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("TEST_TYPE"));
        model.addAttribute("envList", codeService.selectCodeList("TEST_ENV"));
        model.addAttribute("caseResultList", codeService.selectCodeList("TEST_CASE_RESULT"));
        model.addAttribute("menu", "test");
        return "test/form";
    }

    /** 테스트 등록 처리 (헤더 + 테스트케이스 N개) */
    @PostMapping("/insert")
    public String insert(@ModelAttribute TestVO testVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        if (testVO.getTesterId() == null || testVO.getTesterId().isBlank()) {
            testVO.setTesterId(loginUser.getUsername());
        }
        testService.insertTest(testVO);
        return "redirect:/test/detail/" + testVO.getTestId();
    }

    /** 테스트 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute TestVO testVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (testVO.getTesterId() == null || testVO.getTesterId().isBlank()) {
            testVO.setTesterId(loginUser.getUsername());
        }
        testService.processTest(testVO);
        return "redirect:/test/detail/" + testVO.getTestId();
    }

    /** 테스트 삭제 */
    @PostMapping("/delete/{testId}")
    public String delete(@PathVariable Long testId) {
        testService.deleteTest(testId);
        return "redirect:/test/list";
    }
}

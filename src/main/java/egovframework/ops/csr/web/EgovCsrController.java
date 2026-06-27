package egovframework.ops.csr.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.csr.service.EgovCsrService;
import egovframework.ops.csr.service.CsrVO;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 요청관리(CSR) 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 요청 등록/조회/처리/종결 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/csr")
public class EgovCsrController {

    private final EgovCsrService csrService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;
    private final EgovApprService apprService;

    public EgovCsrController(EgovCsrService csrService,
                             EgovSystemService systemService,
                             EgovCodeService codeService,
                                EgovApprService apprService) {
        this.csrService = csrService;
        this.systemService = systemService;
        this.codeService = codeService;
        this.apprService = apprService;
    }

    /** 요청 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") CsrVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = csrService.selectCsrListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("csrList", csrService.selectCsrList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("CSR_STATUS"));
        model.addAttribute("menu", "csr");
        return "csr/list";
    }

    /** 요청 상세 */
    @GetMapping("/detail/{csrId}")
    public String detail(@PathVariable Long csrId, Model model) {
        model.addAttribute("csr", csrService.selectCsr(csrId));
        model.addAttribute("statusList", codeService.selectCodeList("CSR_STATUS"));
        model.addAttribute("menu", "csr");
        return "csr/detail";
    }

    /** 요청 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("csr", new CsrVO());
        addFormCodes(model);
        model.addAttribute("menu", "csr");
        return "csr/form";
    }

    /** 요청 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute CsrVO csrVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        csrVO.setReqId(loginUser.getUsername());
        csrService.insertCsr(csrVO);
        // 결재 기본설정(템플릿) 자동 적용 — 결재/검토/공유/처리자 라인을 기본설정에 따라 생성
        apprService.applyTemplate("CSR", csrVO.getCsrId(), loginUser.getUsername());
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 기본정보 수정 폼 */
    @GetMapping("/edit/{csrId}")
    public String editForm(@PathVariable Long csrId, Model model) {
        model.addAttribute("csr", csrService.selectCsr(csrId));
        addFormCodes(model);
        model.addAttribute("menu", "csr");
        return "csr/form";
    }

    /** 요청 기본정보 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute CsrVO csrVO) {
        csrService.updateCsr(csrVO);
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute CsrVO csrVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (csrVO.getChargerId() == null || csrVO.getChargerId().isBlank()) {
            csrVO.setChargerId(loginUser.getUsername());
        }
        csrService.processCsr(csrVO);
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 삭제 */
    @PostMapping("/delete/{csrId}")
    public String delete(@PathVariable Long csrId) {
        csrService.deleteCsr(csrId);
        return "redirect:/csr/list";
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("CSR_TYPE"));
        model.addAttribute("priorityList", codeService.selectCodeList("PRIORITY"));
        model.addAttribute("statusList", codeService.selectCodeList("CSR_STATUS"));
    }
}

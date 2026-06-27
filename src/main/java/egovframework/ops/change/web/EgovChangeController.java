package egovframework.ops.change.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.change.service.ChangeCabVO;
import egovframework.ops.change.service.ChangeVO;
import egovframework.ops.change.service.EgovChangeService;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 변경관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 변경요청/조회/심의·승인/적용 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/change")
public class EgovChangeController {

    private final EgovChangeService changeService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;
    private final EgovApprService apprService;

    public EgovChangeController(EgovChangeService changeService,
                                EgovSystemService systemService,
                                EgovCodeService codeService,
                                EgovApprService apprService) {
        this.changeService = changeService;
        this.systemService = systemService;
        this.codeService = codeService;
        this.apprService = apprService;
    }

    /** 변경 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") ChangeVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = changeService.selectChangeListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("changeList", changeService.selectChangeList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("CHANGE_STATUS"));
        model.addAttribute("menu", "change");
        return "change/list";
    }

    /** 변경 상세 */
    @GetMapping("/detail/{chgId}")
    public String detail(@PathVariable Long chgId, Model model) {
        model.addAttribute("change", changeService.selectChange(chgId));
        model.addAttribute("statusList", codeService.selectCodeList("CHANGE_STATUS"));
        model.addAttribute("cabDecisionList", codeService.selectCodeList("CAB_DECISION"));
        model.addAttribute("menu", "change");
        return "change/detail";
    }

    /** 변경요청 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("change", new ChangeVO());
        addFormCodes(model);
        model.addAttribute("menu", "change");
        return "change/form";
    }

    /** 변경요청 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute ChangeVO changeVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        changeVO.setReqId(loginUser.getUsername());
        changeService.insertChange(changeVO);
        // 결재 기본설정(템플릿) 자동 적용 — 결재/검토/공유/처리자 라인을 기본설정에 따라 생성
        apprService.applyTemplate("CHANGE", changeVO.getChgId(), loginUser.getUsername());
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 변경 기본정보 수정 폼 */
    @GetMapping("/edit/{chgId}")
    public String editForm(@PathVariable Long chgId, Model model) {
        model.addAttribute("change", changeService.selectChange(chgId));
        addFormCodes(model);
        model.addAttribute("menu", "change");
        return "change/form";
    }

    /** 변경 기본정보 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute ChangeVO changeVO) {
        changeService.updateChange(changeVO);
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 심의/승인 처리 */
    @PostMapping("/approve")
    public String approve(@ModelAttribute ChangeVO changeVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        changeVO.setApprId(loginUser.getUsername());
        changeService.approveChange(changeVO);
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 적용 처리 */
    @PostMapping("/apply")
    public String apply(@ModelAttribute ChangeVO changeVO) {
        changeService.applyChange(changeVO);
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 변경 삭제 */
    @PostMapping("/delete/{chgId}")
    public String delete(@PathVariable Long chgId) {
        changeService.deleteChange(chgId);
        return "redirect:/change/list";
    }

    /** CAB(변경자문위원회) 심의 등록 */
    @PostMapping("/cab")
    public String cab(@ModelAttribute ChangeCabVO cabVO,
                      @AuthenticationPrincipal LoginUser loginUser) {
        if (cabVO.getReviewer() == null || cabVO.getReviewer().isBlank()) {
            cabVO.setReviewer(loginUser.getUsername());
        }
        changeService.cabReview(cabVO);
        return "redirect:/change/detail/" + cabVO.getChgId();
    }

    /** 이행후검토(PIR) 기록 */
    @PostMapping("/pir")
    public String pir(@ModelAttribute ChangeVO changeVO) {
        changeService.recordPir(changeVO);
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("CHANGE_TYPE"));
        model.addAttribute("statusList", codeService.selectCodeList("CHANGE_STATUS"));
    }
}

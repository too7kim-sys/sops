package egovframework.ops.itf.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.itf.service.EgovInterfaceService;
import egovframework.ops.itf.service.InterfaceVO;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 연계관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 연계 요청/조회/처리/완료 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/interface")
public class EgovInterfaceController {

    private final EgovInterfaceService interfaceService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;

    public EgovInterfaceController(EgovInterfaceService interfaceService,
                                   EgovSystemService systemService,
                                   EgovCodeService codeService) {
        this.interfaceService = interfaceService;
        this.systemService = systemService;
        this.codeService = codeService;
    }

    /** 연계 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") InterfaceVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = interfaceService.selectInterfaceListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("interfaceList", interfaceService.selectInterfaceList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("INTF_STATUS"));
        model.addAttribute("menu", "interface");
        return "interface/list";
    }

    /** 연계 상세 */
    @GetMapping("/detail/{intfId}")
    public String detail(@PathVariable Long intfId, Model model) {
        model.addAttribute("itf", interfaceService.selectInterface(intfId));
        model.addAttribute("statusList", codeService.selectCodeList("INTF_STATUS"));
        model.addAttribute("menu", "interface");
        return "interface/detail";
    }

    /** 연계 요청(등록) 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("itf", new InterfaceVO());
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("INTF_TYPE"));
        model.addAttribute("menu", "interface");
        return "interface/form";
    }

    /** 연계 요청 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute InterfaceVO interfaceVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        interfaceVO.setReqId(loginUser.getUsername());
        interfaceService.insertInterface(interfaceVO);
        return "redirect:/interface/detail/" + interfaceVO.getIntfId();
    }

    /** 연계 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute InterfaceVO interfaceVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (interfaceVO.getChargerId() == null || interfaceVO.getChargerId().isBlank()) {
            interfaceVO.setChargerId(loginUser.getUsername());
        }
        interfaceService.processInterface(interfaceVO);
        return "redirect:/interface/detail/" + interfaceVO.getIntfId();
    }

    /** 연계 삭제 */
    @PostMapping("/delete/{intfId}")
    public String delete(@PathVariable Long intfId) {
        interfaceService.deleteInterface(intfId);
        return "redirect:/interface/list";
    }
}

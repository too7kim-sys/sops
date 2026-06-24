package egovframework.ops.system.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.ops.system.service.EgovSystemService;
import egovframework.ops.system.service.SystemVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 응용시스템 마스터 컨트롤러 (Presentation 계층).
 *
 * <p>기준정보 관리 — 운영대상 응용시스템의 등록/조회/수정/삭제 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/system")
public class EgovSystemController {

    private final EgovSystemService systemService;

    public EgovSystemController(EgovSystemService systemService) {
        this.systemService = systemService;
    }

    /** 시스템 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") SystemVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = systemService.selectSystemListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("systemList", systemService.selectSystemList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("menu", "system");
        return "system/list";
    }

    /** 시스템 상세 */
    @GetMapping("/detail/{sysId}")
    public String detail(@PathVariable String sysId, Model model) {
        model.addAttribute("system", systemService.selectSystem(sysId));
        model.addAttribute("menu", "system");
        return "system/detail";
    }

    /** 시스템 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("system", new SystemVO());
        model.addAttribute("menu", "system");
        return "system/form";
    }

    /** 시스템 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute SystemVO systemVO) {
        systemService.insertSystem(systemVO);
        return "redirect:/system/detail/" + systemVO.getSysId();
    }

    /** 시스템 수정 폼 */
    @GetMapping("/edit/{sysId}")
    public String editForm(@PathVariable String sysId, Model model) {
        model.addAttribute("system", systemService.selectSystem(sysId));
        model.addAttribute("menu", "system");
        return "system/form";
    }

    /** 시스템 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute SystemVO systemVO) {
        systemService.updateSystem(systemVO);
        return "redirect:/system/detail/" + systemVO.getSysId();
    }

    /** 시스템 삭제 */
    @PostMapping("/delete/{sysId}")
    public String delete(@PathVariable String sysId) {
        systemService.deleteSystem(sysId);
        return "redirect:/system/list";
    }
}

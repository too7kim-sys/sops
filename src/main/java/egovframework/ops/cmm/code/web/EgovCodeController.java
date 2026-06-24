package egovframework.ops.cmm.code.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.ops.cmm.code.service.CodeVO;
import egovframework.ops.cmm.code.service.EgovCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 공통코드 관리 컨트롤러 (Presentation 계층).
 *
 * <p>ADMIN 전용 화면 — 공통코드 등록/조회/수정/삭제 기능을 제공한다.
 * URL은 /sys/** 하위로 보안설정상 ADMIN 권한이 필요하다.</p>
 *
 * <p>수정(edit) 흐름은 별도 단건 조회 메서드가 없으므로, 목록 화면에서 행 데이터
 * (codeGrp/codeId/codeNm/sortOrdr/useAt)를 쿼리 파라미터로 폼에 전달하여 채운다.</p>
 */
@Controller
@RequestMapping("/sys/code")
public class EgovCodeController {

    private final EgovCodeService codeService;

    public EgovCodeController(EgovCodeService codeService) {
        this.codeService = codeService;
    }

    /** 코드 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") CodeVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = codeService.selectCodeMngListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("codeList", codeService.selectCodeMngList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("menu", "code");
        return "sys/code/list";
    }

    /** 코드 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("code", new CodeVO());
        model.addAttribute("menu", "code");
        return "sys/code/form";
    }

    /** 코드 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute CodeVO codeVO) {
        codeService.insertCode(codeVO);
        return "redirect:/sys/code/list";
    }

    /**
     * 코드 수정 폼.
     *
     * <p>단건 조회 메서드가 없으므로 목록에서 전달받은 행 값을 그대로 폼에 채운다.</p>
     */
    @GetMapping("/edit")
    public String editForm(@ModelAttribute("code") CodeVO codeVO, Model model) {
        model.addAttribute("menu", "code");
        return "sys/code/form";
    }

    /** 코드 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute CodeVO codeVO) {
        codeService.updateCode(codeVO);
        return "redirect:/sys/code/list";
    }

    /** 코드 삭제 */
    @PostMapping("/delete")
    public String delete(@RequestParam String codeGrp, @RequestParam String codeId) {
        CodeVO codeVO = new CodeVO();
        codeVO.setCodeGrp(codeGrp);
        codeVO.setCodeId(codeId);
        codeService.deleteCode(codeVO);
        return "redirect:/sys/code/list";
    }
}

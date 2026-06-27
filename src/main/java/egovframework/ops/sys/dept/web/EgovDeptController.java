package egovframework.ops.sys.dept.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.ops.sys.dept.service.DeptVO;
import egovframework.ops.sys.dept.service.EgovDeptService;
import egovframework.ops.sys.user.service.EgovUserService;
import egovframework.ops.sys.user.service.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 부서 관리 컨트롤러 (Presentation 계층, 기준정보).
 *
 * <p>ADMIN 전용 화면 — 부서 등록/조회/수정/삭제. URL은 /sys/** 하위로 보안상 ADMIN 권한 필요.</p>
 */
@Controller
@RequestMapping("/sys/dept")
public class EgovDeptController {

    private final EgovDeptService deptService;
    private final EgovUserService userService;

    public EgovDeptController(EgovDeptService deptService, EgovUserService userService) {
        this.deptService = deptService;
        this.userService = userService;
    }

    /** 부서장 후보(활성 사용자 전체) */
    private List<UserVO> userCandidates() {
        UserVO uv = new UserVO();
        uv.setPageUnit(1000);
        uv.initPaging();
        return userService.selectUserList(uv);
    }

    private void addFormRefs(Model model) {
        model.addAttribute("deptComboList", deptService.selectDeptComboList());
        model.addAttribute("userList", userCandidates());
    }

    /** 부서 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") DeptVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = deptService.selectDeptListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("deptList", deptService.selectDeptList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("menu", "dept");
        return "sys/dept/list";
    }

    /** 부서 검색 팝업 (결재선/공유·사용자·부서 폼 공통, ADMIN/OPERATOR) */
    @GetMapping("/popup")
    public String popup(@ModelAttribute("searchVO") DeptVO searchVO,
                        @RequestParam(defaultValue = "dept") String prefix,
                        Model model) {
        searchVO.setSearchStatus("Y"); // 활성 부서만
        searchVO.setPageUnit(200);
        searchVO.initPaging();
        model.addAttribute("deptList", deptService.selectDeptList(searchVO));
        model.addAttribute("prefix", prefix);
        return "sys/dept/popup";
    }

    /** 부서 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("dept", new DeptVO());
        addFormRefs(model);
        model.addAttribute("menu", "dept");
        return "sys/dept/form";
    }

    /** 부서 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute DeptVO deptVO) {
        deptService.insertDept(deptVO);
        return "redirect:/sys/dept/list";
    }

    /** 부서 수정 폼 */
    @GetMapping("/edit/{deptId}")
    public String editForm(@PathVariable Long deptId, Model model) {
        model.addAttribute("dept", deptService.selectDept(deptId));
        addFormRefs(model);
        model.addAttribute("menu", "dept");
        return "sys/dept/form";
    }

    /** 부서 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute DeptVO deptVO) {
        deptService.updateDept(deptVO);
        return "redirect:/sys/dept/list";
    }

    /** 부서 삭제 */
    @PostMapping("/delete/{deptId}")
    public String delete(@PathVariable Long deptId) {
        deptService.deleteDept(deptId);
        return "redirect:/sys/dept/list";
    }
}

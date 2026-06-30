package egovframework.ops.sys.user.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.ops.sys.dept.service.EgovDeptService;
import egovframework.ops.sys.menu.service.EgovMenuService;
import egovframework.ops.sys.user.service.EgovUserService;
import egovframework.ops.sys.user.service.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자(운영자) 관리 컨트롤러 (Presentation 계층).
 *
 * <p>ADMIN 전용 화면 — 운영자 등록/조회/수정/삭제 기능을 제공한다.
 * URL은 /sys/** 하위로 보안설정상 ADMIN 권한이 필요하다.</p>
 */
@Controller
@RequestMapping("/sys/user")
public class EgovUserController {

    private final EgovUserService userService;
    private final EgovDeptService deptService;
    private final EgovMenuService menuService;

    public EgovUserController(EgovUserService userService, EgovDeptService deptService,
                             EgovMenuService menuService) {
        this.userService = userService;
        this.deptService = deptService;
        this.menuService = menuService;
    }

    /** 사용자 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") UserVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = userService.selectUserListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("userList", userService.selectUserList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("menu", "user");
        return "sys/user/list";
    }

    /** 사용자 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("user", new UserVO());
        model.addAttribute("deptComboList", deptService.selectDeptComboList());
        model.addAttribute("roleList", menuService.selectRoleList());
        model.addAttribute("menu", "user");
        return "sys/user/form";
    }

    /** 사용자 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute UserVO userVO) {
        userService.insertUser(userVO);
        return "redirect:/sys/user/list";
    }

    /** 사용자 수정 폼 */
    @GetMapping("/edit/{userId}")
    public String editForm(@PathVariable String userId, Model model) {
        model.addAttribute("user", userService.selectUser(userId));
        model.addAttribute("deptComboList", deptService.selectDeptComboList());
        model.addAttribute("roleList", menuService.selectRoleList());
        model.addAttribute("menu", "user");
        return "sys/user/form";
    }

    /** 사용자 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute UserVO userVO) {
        userService.updateUser(userVO);
        return "redirect:/sys/user/list";
    }

    /** 사용자 삭제 */
    @PostMapping("/delete/{userId}")
    public String delete(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "redirect:/sys/user/list";
    }
}

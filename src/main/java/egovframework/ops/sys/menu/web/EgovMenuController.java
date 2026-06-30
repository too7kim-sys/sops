package egovframework.ops.sys.menu.web;

import egovframework.ops.sys.menu.service.EgovMenuService;
import egovframework.ops.sys.menu.service.MenuVO;
import egovframework.ops.sys.menu.service.RoleVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 메뉴관리 / 권한관리 컨트롤러 (운영관리자 전용 — /sys/** 보안).
 */
@Controller
@RequestMapping("/sys")
public class EgovMenuController {

    private final EgovMenuService menuService;

    public EgovMenuController(EgovMenuService menuService) {
        this.menuService = menuService;
    }

    /* ============================ 메뉴관리 ============================ */

    @GetMapping("/menu/list")
    public String list(Model model) {
        model.addAttribute("menuList", menuService.selectMenuList());
        model.addAttribute("menu", "menu");
        return "sys/menu/list";
    }

    @GetMapping("/menu/write")
    public String writeForm(Model model) {
        model.addAttribute("menuVO", new MenuVO());
        model.addAttribute("groupList", menuService.selectGroupList());
        model.addAttribute("menu", "menu");
        return "sys/menu/form";
    }

    @GetMapping("/menu/edit/{menuId}")
    public String editForm(@PathVariable Long menuId, Model model) {
        model.addAttribute("menuVO", menuService.selectMenu(menuId));
        model.addAttribute("groupList", menuService.selectGroupList());
        model.addAttribute("menu", "menu");
        return "sys/menu/form";
    }

    @PostMapping("/menu/insert")
    public String insert(@ModelAttribute MenuVO menuVO) {
        menuService.insertMenu(menuVO);
        return "redirect:/sys/menu/list";
    }

    @PostMapping("/menu/update")
    public String update(@ModelAttribute MenuVO menuVO) {
        menuService.updateMenu(menuVO);
        return "redirect:/sys/menu/list";
    }

    @PostMapping("/menu/delete/{menuId}")
    public String delete(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return "redirect:/sys/menu/list";
    }

    /* ============================ 권한관리 ============================ */

    @GetMapping("/auth")
    public String auth(@RequestParam(required = false) String role, Model model) {
        java.util.Map<String, String> roles = menuService.selectRoleMap();
        // 선택 역할 미지정/유효하지 않으면 첫 역할로
        if (role == null || !roles.containsKey(role)) {
            role = roles.isEmpty() ? null : roles.keySet().iterator().next();
        }
        model.addAttribute("roles", roles);
        model.addAttribute("roleList", menuService.selectRoleList());
        model.addAttribute("selectedRole", role);
        if (role != null) {
            model.addAttribute("menuList", menuService.selectAuthMenuList(role));
        }
        model.addAttribute("menu", "auth");
        return "sys/auth";
    }

    @PostMapping("/auth/save")
    public String authSave(@RequestParam String role,
                           @RequestParam(name = "menuIds", required = false) List<Long> menuIds) {
        menuService.saveMenuAuth(role, menuIds);
        return "redirect:/sys/auth?role=" + role;
    }

    /* ===== 역할 등록/수정/삭제 (팝업) ===== */

    /** 역할 관리 팝업 화면 */
    @GetMapping("/role/popup")
    public String rolePopup(Model model) {
        model.addAttribute("roleList", menuService.selectRoleList());
        return "sys/role/popup";
    }

    @PostMapping("/role/save")
    public String roleSave(@ModelAttribute RoleVO roleVO,
                           @RequestParam(required = false) String from,
                           RedirectAttributes ra) {
        boolean popup = "popup".equals(from);
        if (roleVO.getRoleCd() == null || roleVO.getRoleCd().isBlank()
                || roleVO.getRoleNm() == null || roleVO.getRoleNm().isBlank()) {
            ra.addFlashAttribute("roleMsg", "역할코드와 역할명을 입력하세요.");
            return popup ? "redirect:/sys/role/popup" : "redirect:/sys/auth";
        }
        menuService.saveRole(roleVO);
        if (popup) {
            return "redirect:/sys/role/popup?changed=1";
        }
        return "redirect:/sys/auth?role=" + roleVO.getRoleCd().trim().toUpperCase();
    }

    @PostMapping("/role/delete")
    public String roleDelete(@RequestParam String roleCd,
                             @RequestParam(required = false) String from,
                             RedirectAttributes ra) {
        boolean popup = "popup".equals(from);
        try {
            menuService.deleteRole(roleCd);
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("roleMsg", e.getMessage());
        }
        if (popup) {
            return "redirect:/sys/role/popup?changed=1";
        }
        return "redirect:/sys/auth";
    }
}

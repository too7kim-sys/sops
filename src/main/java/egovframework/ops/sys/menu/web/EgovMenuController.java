package egovframework.ops.sys.menu.web;

import egovframework.ops.sys.menu.service.EgovMenuService;
import egovframework.ops.sys.menu.service.MenuVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 메뉴관리 / 권한관리 컨트롤러 (운영관리자 전용 — /sys/** 보안).
 */
@Controller
@RequestMapping("/sys")
public class EgovMenuController {

    /** 권한관리 대상 역할 */
    private static final Map<String, String> ROLES = new LinkedHashMap<>();
    static {
        ROLES.put("ADMIN", "운영관리자");
        ROLES.put("OPERATOR", "운영자");
        ROLES.put("USER", "일반사용자");
    }

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
    public String auth(@RequestParam(defaultValue = "ADMIN") String role, Model model) {
        model.addAttribute("roles", ROLES);
        model.addAttribute("selectedRole", role);
        model.addAttribute("menuList", menuService.selectAuthMenuList(role));
        model.addAttribute("menu", "auth");
        return "sys/auth";
    }

    @PostMapping("/auth/save")
    public String authSave(@RequestParam String role,
                           @RequestParam(name = "menuIds", required = false) List<Long> menuIds) {
        menuService.saveMenuAuth(role, menuIds);
        return "redirect:/sys/auth?role=" + role;
    }
}

package egovframework.ops.sys.menu.web;

import egovframework.com.config.LoginUser;
import egovframework.ops.sys.menu.service.EgovMenuService;
import egovframework.ops.sys.menu.service.MenuVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

/**
 * 전역 내비게이션 — 로그인 사용자의 역할에 따른 메뉴 트리(navMenus)를 모든 화면에 주입한다.
 */
@ControllerAdvice
public class NavGlobalAdvice {

    private final EgovMenuService menuService;

    public NavGlobalAdvice(EgovMenuService menuService) {
        this.menuService = menuService;
    }

    @ModelAttribute("navMenus")
    public List<MenuVO> navMenus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser) {
            String role = ((LoginUser) auth.getPrincipal()).getUser().getRole();
            return menuService.selectMenuTree(role);
        }
        return Collections.emptyList();
    }
}

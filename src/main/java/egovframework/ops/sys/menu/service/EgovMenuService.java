package egovframework.ops.sys.menu.service;

import java.util.List;

/**
 * 메뉴/권한 관리 서비스.
 */
public interface EgovMenuService {

    /** 전체 메뉴(관리화면) */
    List<MenuVO> selectMenuList();

    /** 그룹 메뉴(상위 선택용) */
    List<MenuVO> selectGroupList();

    MenuVO selectMenu(Long menuId);

    void insertMenu(MenuVO vo);

    void updateMenu(MenuVO vo);

    void deleteMenu(Long menuId);

    /** 역할별 동적 메뉴 트리(헤더 렌더링) — 빈 그룹은 제외 */
    List<MenuVO> selectMenuTree(String role);

    /** 권한관리 — 역할 기준 전체 메뉴 + 허용여부(granted) */
    List<MenuVO> selectAuthMenuList(String role);

    /** 권한 저장 — 역할의 허용 메뉴를 일괄 교체 */
    void saveMenuAuth(String role, List<Long> menuIds);
}

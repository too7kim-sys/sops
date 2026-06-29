package egovframework.ops.sys.menu.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.sys.menu.service.EgovMenuService;
import egovframework.ops.sys.menu.service.MenuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 메뉴/권한 관리 서비스 구현체.
 */
@Service("egovMenuService")
public class EgovMenuServiceImpl extends EgovAbstractServiceImpl implements EgovMenuService {

    private final MenuMapper menuMapper;

    public EgovMenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public List<MenuVO> selectMenuList() {
        return menuMapper.selectMenuList();
    }

    @Override
    public List<MenuVO> selectGroupList() {
        return menuMapper.selectGroupList();
    }

    @Override
    public MenuVO selectMenu(Long menuId) {
        return menuMapper.selectMenu(menuId);
    }

    @Override
    @Transactional
    public void insertMenu(MenuVO vo) {
        if (vo.getUseAt() == null) {
            vo.setUseAt("Y");
        }
        if (vo.getMenuType() == null) {
            vo.setMenuType("ITEM");
        }
        menuMapper.insertMenu(vo);
    }

    @Override
    @Transactional
    public void updateMenu(MenuVO vo) {
        menuMapper.updateMenu(vo);
    }

    @Override
    @Transactional
    public void deleteMenu(Long menuId) {
        menuMapper.deleteMenuAuthByMenu(menuId);
        menuMapper.deleteMenu(menuId);
    }

    @Override
    public List<MenuVO> selectMenuTree(String role) {
        List<MenuVO> all = menuMapper.selectMenusByRole(role);
        Map<Long, MenuVO> byId = new LinkedHashMap<>();
        for (MenuVO m : all) {
            byId.put(m.getMenuId(), m);
        }
        List<MenuVO> roots = new ArrayList<>();
        for (MenuVO m : all) {
            if (m.getUpperId() != null && byId.containsKey(m.getUpperId())) {
                byId.get(m.getUpperId()).getChildren().add(m);
            } else if (m.getUpperId() == null) {
                roots.add(m);
            }
        }
        // 자식 없는 그룹은 제외
        roots.removeIf(m -> "GROUP".equals(m.getMenuType()) && m.getChildren().isEmpty());
        return roots;
    }

    @Override
    public List<MenuVO> selectAuthMenuList(String role) {
        List<MenuVO> all = menuMapper.selectMenuList();
        List<Long> granted = menuMapper.selectAuthMenuIds(role);
        for (MenuVO m : all) {
            m.setGranted(granted.contains(m.getMenuId()));
        }
        return all;
    }

    @Override
    @Transactional
    public void saveMenuAuth(String role, List<Long> menuIds) {
        menuMapper.deleteMenuAuthByRole(role);
        if (menuIds != null) {
            for (Long id : menuIds) {
                if (id != null) {
                    menuMapper.insertMenuAuth(role, id);
                }
            }
        }
    }
}

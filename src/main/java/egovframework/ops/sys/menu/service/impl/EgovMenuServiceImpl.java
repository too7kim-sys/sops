package egovframework.ops.sys.menu.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.sys.menu.service.EgovMenuService;
import egovframework.ops.sys.menu.service.MenuVO;
import egovframework.ops.sys.menu.service.RoleVO;
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
    private final RoleMapper roleMapper;

    public EgovMenuServiceImpl(MenuMapper menuMapper, RoleMapper roleMapper) {
        this.menuMapper = menuMapper;
        this.roleMapper = roleMapper;
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

    /* ===== 역할(권한 그룹) 관리 ===== */

    @Override
    public List<RoleVO> selectRoleList() {
        return roleMapper.selectRoleList();
    }

    @Override
    public Map<String, String> selectRoleMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (RoleVO r : roleMapper.selectActiveRoleList()) {
            map.put(r.getRoleCd(), r.getRoleNm());
        }
        return map;
    }

    @Override
    public RoleVO selectRole(String roleCd) {
        return roleMapper.selectRole(roleCd);
    }

    @Override
    @Transactional
    public void saveRole(RoleVO vo) {
        if (vo.getRoleCd() != null) {
            vo.setRoleCd(vo.getRoleCd().trim().toUpperCase());
        }
        if (vo.getUseAt() == null || vo.getUseAt().isBlank()) {
            vo.setUseAt("Y");
        }
        if (vo.getSortNo() == null) {
            vo.setSortNo(99);
        }
        // 존재하면 수정, 없으면 신규 등록
        if (roleMapper.countRole(vo.getRoleCd()) > 0) {
            roleMapper.updateRole(vo);
        } else {
            roleMapper.insertRole(vo);
        }
    }

    @Override
    @Transactional
    public void deleteRole(String roleCd) {
        RoleVO role = roleMapper.selectRole(roleCd);
        if (role == null) {
            return;
        }
        if ("Y".equals(role.getBuiltin())) {
            throw new IllegalStateException("내장역할은 삭제할 수 없습니다: " + roleCd);
        }
        // 사용중(사용자 보유) 역할은 삭제 거부 — 사용자 수는 목록 조회로 확인
        for (RoleVO r : roleMapper.selectRoleList()) {
            if (roleCd.equals(r.getRoleCd()) && r.getUserCnt() != null && r.getUserCnt() > 0) {
                throw new IllegalStateException("사용중인 역할은 삭제할 수 없습니다: " + roleCd);
            }
        }
        // 메뉴권한도 함께 정리 후 역할 삭제
        menuMapper.deleteMenuAuthByRole(roleCd);
        roleMapper.deleteRole(roleCd);
    }
}

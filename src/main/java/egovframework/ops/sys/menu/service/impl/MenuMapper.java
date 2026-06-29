package egovframework.ops.sys.menu.service.impl;

import egovframework.ops.sys.menu.service.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 메뉴/권한 MyBatis Mapper.
 */
@Mapper
public interface MenuMapper {

    /** 전체 메뉴(관리화면) */
    List<MenuVO> selectMenuList();

    /** 그룹 메뉴(상위 선택용) */
    List<MenuVO> selectGroupList();

    MenuVO selectMenu(Long menuId);

    void insertMenu(MenuVO vo);

    void updateMenu(MenuVO vo);

    void deleteMenu(Long menuId);

    /** 역할이 접근 가능한 메뉴(그룹 + 허용 ITEM), 사용중만 */
    List<MenuVO> selectMenusByRole(String role);

    /* ===== 권한 ===== */

    /** 역할의 허용 메뉴ID 목록 */
    List<Long> selectAuthMenuIds(String role);

    void deleteMenuAuthByRole(String roleId);

    void insertMenuAuth(@Param("roleId") String roleId, @Param("menuId") Long menuId);

    void deleteMenuAuthByMenu(Long menuId);
}

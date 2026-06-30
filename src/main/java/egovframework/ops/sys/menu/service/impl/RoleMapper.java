package egovframework.ops.sys.menu.service.impl;

import egovframework.ops.sys.menu.service.RoleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 역할(권한 그룹) MyBatis Mapper.
 */
@Mapper
public interface RoleMapper {

    /** 전체 역할(관리화면) — 사용자 수 포함 */
    List<RoleVO> selectRoleList();

    /** 사용중(USE_AT='Y') 역할 — 권한관리 탭/사용자 역할선택용 */
    List<RoleVO> selectActiveRoleList();

    RoleVO selectRole(String roleCd);

    int countRole(String roleCd);

    void insertRole(RoleVO vo);

    void updateRole(RoleVO vo);

    void deleteRole(String roleCd);
}

package egovframework.ops.sys.dept.service.impl;

import egovframework.ops.sys.dept.service.DeptVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 부서 관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface DeptMapper {

    List<DeptVO> selectDeptList(DeptVO searchVO);

    int selectDeptListCnt(DeptVO searchVO);

    DeptVO selectDept(Long deptId);

    List<DeptVO> selectDeptComboList();

    /** 전체 부서코드(자동 채번용, 비활성 포함) */
    List<String> selectAllDeptCds();

    void insertDept(DeptVO vo);

    void updateDept(DeptVO vo);

    void deleteDept(Long deptId);
}

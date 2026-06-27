package egovframework.ops.sys.dept.service;

import java.util.List;

/**
 * 부서 관리 서비스 인터페이스 (기준정보).
 */
public interface EgovDeptService {

    /** 부서 목록(검색/페이징) */
    List<DeptVO> selectDeptList(DeptVO searchVO);

    /** 부서 목록 건수 */
    int selectDeptListCnt(DeptVO searchVO);

    /** 부서 단건 */
    DeptVO selectDept(Long deptId);

    /** 활성 부서 목록(드롭다운용) */
    List<DeptVO> selectDeptComboList();

    /** 부서 등록 */
    void insertDept(DeptVO vo);

    /** 부서 수정 */
    void updateDept(DeptVO vo);

    /** 부서 삭제 */
    void deleteDept(Long deptId);
}

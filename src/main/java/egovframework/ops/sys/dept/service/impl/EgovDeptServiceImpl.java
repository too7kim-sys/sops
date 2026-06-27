package egovframework.ops.sys.dept.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.sys.dept.service.DeptVO;
import egovframework.ops.sys.dept.service.EgovDeptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 부서 관리 서비스 구현체 (기준정보).
 */
@Service("egovDeptService")
public class EgovDeptServiceImpl extends EgovAbstractServiceImpl implements EgovDeptService {

    private final DeptMapper deptMapper;

    public EgovDeptServiceImpl(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override
    public List<DeptVO> selectDeptList(DeptVO searchVO) {
        return deptMapper.selectDeptList(searchVO);
    }

    @Override
    public int selectDeptListCnt(DeptVO searchVO) {
        return deptMapper.selectDeptListCnt(searchVO);
    }

    @Override
    public DeptVO selectDept(Long deptId) {
        return deptMapper.selectDept(deptId);
    }

    @Override
    public List<DeptVO> selectDeptComboList() {
        return deptMapper.selectDeptComboList();
    }

    @Override
    public void insertDept(DeptVO vo) {
        deptMapper.insertDept(vo);
    }

    @Override
    public void updateDept(DeptVO vo) {
        deptMapper.updateDept(vo);
    }

    @Override
    public void deleteDept(Long deptId) {
        deptMapper.deleteDept(deptId);
    }
}

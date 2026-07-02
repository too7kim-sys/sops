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
        // 부서코드 미입력 시 자동 채번 (D101, D102 …)
        if (vo.getDeptCd() == null || vo.getDeptCd().isBlank()) {
            vo.setDeptCd(generateDeptCd());
        }
        deptMapper.insertDept(vo);
    }

    /** 다음 부서코드 생성 — 기존 'D###' 중 최대 일련번호 +1 (비활성 포함, DB 무관) */
    private String generateDeptCd() {
        int max = 0;
        for (String cd : deptMapper.selectAllDeptCds()) {
            if (cd != null && cd.matches("D\\d+")) {
                int n = Integer.parseInt(cd.substring(1));
                if (n > max) {
                    max = n;
                }
            }
        }
        return String.format("D%03d", max + 1);
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

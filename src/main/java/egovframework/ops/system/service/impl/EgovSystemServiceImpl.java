package egovframework.ops.system.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.system.service.EgovSystemService;
import egovframework.ops.system.service.SystemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 응용시스템 마스터 서비스 구현체.
 */
@Service("egovSystemService")
public class EgovSystemServiceImpl extends EgovAbstractServiceImpl implements EgovSystemService {

    private final SystemMapper systemMapper;

    public EgovSystemServiceImpl(SystemMapper systemMapper) {
        this.systemMapper = systemMapper;
    }

    @Override
    public List<SystemVO> selectSystemList(SystemVO searchVO) {
        return systemMapper.selectSystemList(searchVO);
    }

    @Override
    public int selectSystemListCnt(SystemVO searchVO) {
        return systemMapper.selectSystemListCnt(searchVO);
    }

    @Override
    public List<SystemVO> selectSystemAll() {
        return systemMapper.selectSystemAll();
    }

    @Override
    public SystemVO selectSystem(String sysId) {
        return systemMapper.selectSystem(sysId);
    }

    @Override
    @Transactional
    public void insertSystem(SystemVO vo) {
        if (vo.getUseAt() == null) {
            vo.setUseAt("Y");
        }
        systemMapper.insertSystem(vo);
    }

    @Override
    @Transactional
    public void updateSystem(SystemVO vo) {
        systemMapper.updateSystem(vo);
    }

    @Override
    @Transactional
    public void deleteSystem(String sysId) {
        systemMapper.deleteSystem(sysId);
    }
}

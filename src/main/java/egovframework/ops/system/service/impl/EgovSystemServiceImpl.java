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
        if (vo.getVcsType() == null || vo.getVcsType().isBlank()) {
            vo.setVcsType("GIT");
        }
        // 시스템ID 미입력 시 자동 채번 (SYS001, SYS002 …)
        if (vo.getSysId() == null || vo.getSysId().isBlank()) {
            vo.setSysId(generateSysId());
        }
        systemMapper.insertSystem(vo);
    }

    /** 다음 시스템ID 생성 — 기존 'SYS###' 중 최대 일련번호 +1 (비활성 포함, DB 무관) */
    private String generateSysId() {
        int max = 0;
        for (String id : systemMapper.selectAllSysIds()) {
            if (id != null && id.matches("SYS\\d+")) {
                int n = Integer.parseInt(id.substring(3));
                if (n > max) {
                    max = n;
                }
            }
        }
        return String.format("SYS%03d", max + 1);
    }

    @Override
    @Transactional
    public void updateSystem(SystemVO vo) {
        if (vo.getVcsType() == null || vo.getVcsType().isBlank()) {
            vo.setVcsType("GIT");
        }
        systemMapper.updateSystem(vo);
    }

    @Override
    @Transactional
    public void deleteSystem(String sysId) {
        systemMapper.deleteSystem(sysId);
    }
}

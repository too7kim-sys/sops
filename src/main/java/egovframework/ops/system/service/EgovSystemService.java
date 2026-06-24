package egovframework.ops.system.service;

import java.util.List;

/**
 * 응용시스템 마스터 서비스 인터페이스.
 */
public interface EgovSystemService {

    List<SystemVO> selectSystemList(SystemVO searchVO);

    int selectSystemListCnt(SystemVO searchVO);

    /** 사용중 시스템 전체 (드롭다운용) */
    List<SystemVO> selectSystemAll();

    SystemVO selectSystem(String sysId);

    void insertSystem(SystemVO vo);

    void updateSystem(SystemVO vo);

    void deleteSystem(String sysId);
}

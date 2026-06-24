package egovframework.ops.system.service.impl;

import egovframework.ops.system.service.SystemVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 응용시스템 마스터 MyBatis Mapper.
 */
@Mapper
public interface SystemMapper {

    List<SystemVO> selectSystemList(SystemVO searchVO);

    int selectSystemListCnt(SystemVO searchVO);

    List<SystemVO> selectSystemAll();

    SystemVO selectSystem(String sysId);

    void insertSystem(SystemVO vo);

    void updateSystem(SystemVO vo);

    void deleteSystem(String sysId);
}

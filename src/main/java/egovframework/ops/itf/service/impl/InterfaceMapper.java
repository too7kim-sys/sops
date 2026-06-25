package egovframework.ops.itf.service.impl;

import egovframework.ops.itf.service.InterfaceHisVO;
import egovframework.ops.itf.service.InterfaceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 연계관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface InterfaceMapper {

    List<InterfaceVO> selectInterfaceList(InterfaceVO searchVO);

    int selectInterfaceListCnt(InterfaceVO searchVO);

    InterfaceVO selectInterface(Long intfId);

    List<InterfaceHisVO> selectInterfaceHisList(Long intfId);

    void insertInterface(InterfaceVO vo);

    void updateInterfaceProcess(InterfaceVO vo);

    void insertInterfaceHis(InterfaceHisVO hisVO);

    void deleteInterface(Long intfId);

    void deleteInterfaceHis(Long intfId);
}

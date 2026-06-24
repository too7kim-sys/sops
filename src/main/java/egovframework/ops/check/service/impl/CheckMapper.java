package egovframework.ops.check.service.impl;

import egovframework.ops.check.service.CheckItemVO;
import egovframework.ops.check.service.CheckVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 운영점검 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface CheckMapper {

    List<CheckVO> selectCheckList(CheckVO searchVO);

    int selectCheckListCnt(CheckVO searchVO);

    CheckVO selectCheck(Long chkId);

    List<CheckItemVO> selectCheckItemList(Long chkId);

    void insertCheck(CheckVO vo);

    void insertCheckItem(CheckItemVO itemVO);

    void deleteCheck(Long chkId);

    void deleteCheckItem(Long chkId);
}

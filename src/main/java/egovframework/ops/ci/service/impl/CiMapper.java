package egovframework.ops.ci.service.impl;

import egovframework.ops.ci.service.CiHisVO;
import egovframework.ops.ci.service.CiVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 형상관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface CiMapper {

    List<CiVO> selectCiList(CiVO searchVO);

    int selectCiListCnt(CiVO searchVO);

    CiVO selectCi(Long ciId);

    List<CiHisVO> selectCiHisList(Long ciId);

    void insertCi(CiVO vo);

    void updateCi(CiVO vo);

    void updateCiStatus(CiVO vo);

    void insertCiHis(CiHisVO hisVO);

    void deleteCi(Long ciId);

    void deleteCiHis(Long ciId);
}

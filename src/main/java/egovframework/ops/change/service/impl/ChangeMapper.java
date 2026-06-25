package egovframework.ops.change.service.impl;

import egovframework.ops.change.service.ChangeCabVO;
import egovframework.ops.change.service.ChangeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 변경관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface ChangeMapper {

    List<ChangeVO> selectChangeList(ChangeVO searchVO);

    int selectChangeListCnt(ChangeVO searchVO);

    ChangeVO selectChange(Long chgId);

    void insertChange(ChangeVO vo);

    void updateChange(ChangeVO vo);

    void updateChangeApprove(ChangeVO vo);

    void updateChangeApply(ChangeVO vo);

    void deleteChange(Long chgId);

    List<ChangeCabVO> selectChangeCabList(Long chgId);

    void insertChangeCab(ChangeCabVO vo);

    void updateCabStatus(ChangeVO vo);

    void updatePir(ChangeVO vo);
}

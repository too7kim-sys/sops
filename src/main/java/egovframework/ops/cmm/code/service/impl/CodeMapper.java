package egovframework.ops.cmm.code.service.impl;

import egovframework.ops.cmm.code.service.CodeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 공통코드 MyBatis Mapper.
 */
@Mapper
public interface CodeMapper {

    List<CodeVO> selectCodeList(String codeGrp);

    List<CodeVO> selectCodeMngList(CodeVO searchVO);

    int selectCodeMngListCnt(CodeVO searchVO);

    void insertCode(CodeVO codeVO);

    void updateCode(CodeVO codeVO);

    void deleteCode(CodeVO codeVO);

    /* 코드그룹 마스터(그룹명) */
    String selectCodeGrpNm(String codeGrp);

    int countCodeGrp(String codeGrp);

    void insertCodeGrp(CodeVO codeVO);

    void updateCodeGrp(CodeVO codeVO);
}

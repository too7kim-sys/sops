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
}

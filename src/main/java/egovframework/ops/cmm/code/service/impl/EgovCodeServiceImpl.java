package egovframework.ops.cmm.code.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.cmm.code.service.CodeVO;
import egovframework.ops.cmm.code.service.EgovCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 공통코드 서비스 구현체.
 */
@Service("egovCodeService")
public class EgovCodeServiceImpl extends EgovAbstractServiceImpl implements EgovCodeService {

    private final CodeMapper codeMapper;

    public EgovCodeServiceImpl(CodeMapper codeMapper) {
        this.codeMapper = codeMapper;
    }

    @Override
    public List<CodeVO> selectCodeList(String codeGrp) {
        return codeMapper.selectCodeList(codeGrp);
    }

    @Override
    public List<CodeVO> selectCodeMngList(CodeVO searchVO) {
        return codeMapper.selectCodeMngList(searchVO);
    }

    @Override
    public int selectCodeMngListCnt(CodeVO searchVO) {
        return codeMapper.selectCodeMngListCnt(searchVO);
    }

    @Override
    @Transactional
    public void insertCode(CodeVO codeVO) {
        if (codeVO.getUseAt() == null) {
            codeVO.setUseAt("Y");
        }
        codeMapper.insertCode(codeVO);
    }

    @Override
    @Transactional
    public void updateCode(CodeVO codeVO) {
        codeMapper.updateCode(codeVO);
    }

    @Override
    @Transactional
    public void deleteCode(CodeVO codeVO) {
        codeMapper.deleteCode(codeVO);
    }
}

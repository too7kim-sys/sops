package egovframework.ops.csr.service.impl;

import egovframework.ops.csr.service.CsrHisVO;
import egovframework.ops.csr.service.CsrVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 요청관리(CSR) MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface CsrMapper {

    List<CsrVO> selectCsrList(CsrVO searchVO);

    int selectCsrListCnt(CsrVO searchVO);

    CsrVO selectCsr(Long csrId);

    List<CsrHisVO> selectCsrHisList(Long csrId);

    void insertCsr(CsrVO vo);

    void updateCsr(CsrVO vo);

    void updateCsrProcess(CsrVO vo);

    void insertCsrHis(CsrHisVO hisVO);

    void deleteCsr(Long csrId);

    void deleteCsrHis(Long csrId);
}

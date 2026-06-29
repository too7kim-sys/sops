package egovframework.ops.csr.service.impl;

import egovframework.ops.csr.service.CsrHisVO;
import egovframework.ops.csr.service.CsrTplVO;
import egovframework.ops.csr.service.CsrVO;
import egovframework.ops.system.service.SystemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /* ===== 요청 ↔ 대상 시스템(다중) ===== */

    /** 요청의 대상 시스템 목록(시스템명 조인) */
    List<SystemVO> selectCsrSysList(Long csrId);

    void insertCsrSys(@Param("csrId") Long csrId, @Param("sysId") String sysId);

    void deleteCsrSys(Long csrId);

    /* ===== 요청 소분류별 요청내용 템플릿 ===== */

    /** 전체 소분류 + 템플릿(LEFT JOIN) — 관리목록 */
    List<CsrTplVO> selectCsrTplList();

    /** 템플릿이 등록된 소분류만 — 등록폼 자동주입용 */
    List<CsrTplVO> selectCsrTplActive();

    /** 소분류 1건의 템플릿(소분류/대분류명 포함) */
    CsrTplVO selectCsrTpl(String subType);

    int updateCsrTpl(CsrTplVO vo);

    void insertCsrTpl(CsrTplVO vo);
}

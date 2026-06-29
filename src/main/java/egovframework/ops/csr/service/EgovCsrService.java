package egovframework.ops.csr.service;

import java.util.List;

/**
 * 요청관리(CSR) 서비스 인터페이스.
 *
 * <p>응용프로그램 표준운영절차 — 요청 등록/조회/처리(상태전이)/종결을 제공한다.</p>
 */
public interface EgovCsrService {

    List<CsrVO> selectCsrList(CsrVO searchVO);

    int selectCsrListCnt(CsrVO searchVO);

    /** 요청 상세 (처리이력 포함) */
    CsrVO selectCsr(Long csrId);

    /** 요청 등록 */
    void insertCsr(CsrVO vo);

    /** 요청 기본정보 수정 */
    void updateCsr(CsrVO vo);

    /**
     * 요청 처리(상태전이).
     * 상태 변경과 함께 처리내용을 갱신하고 처리이력을 적재한다.
     */
    void processCsr(CsrVO vo);

    /** 요청 삭제 */
    void deleteCsr(Long csrId);

    /* ===== 요청 소분류별 요청내용 템플릿 ===== */

    /** 소분류 전체 + 템플릿 목록 (관리화면) */
    List<CsrTplVO> selectCsrTplList();

    /** 등록폼 자동주입용 — 템플릿이 있는 소분류만 */
    List<CsrTplVO> selectCsrTplActive();

    /** 소분류 1건 템플릿 조회 */
    CsrTplVO selectCsrTpl(String subType);

    /** 템플릿 저장(있으면 수정, 없으면 등록) */
    void saveCsrTpl(CsrTplVO vo);

    /* ===== 요청 첨부파일 (메타데이터) ===== */

    List<CsrFileVO> selectCsrFileList(Long csrId);

    CsrFileVO selectCsrFile(Long fileId);

    void insertCsrFile(CsrFileVO vo);

    void deleteCsrFile(Long fileId);
}

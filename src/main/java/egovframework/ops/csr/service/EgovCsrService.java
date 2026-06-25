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
}

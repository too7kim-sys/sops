package egovframework.ops.ci.service;

import java.util.List;

/**
 * 형상관리 서비스 인터페이스 (Business 계층).
 *
 * <p>응용프로그램 표준운영절차 — 형상식별/조회/형상통제(체크아웃·체크인·기준선·감사)를 제공한다.</p>
 */
public interface EgovCiService {

    List<CiVO> selectCiList(CiVO searchVO);

    int selectCiListCnt(CiVO searchVO);

    /** 형상항목 상세 (형상이력 포함) */
    CiVO selectCi(Long ciId);

    /** 형상항목 식별(등록) */
    void insertCi(CiVO vo);

    /** 형상항목 기본정보 수정 */
    void updateCi(CiVO vo);

    /** 형상통제/감사 (변경유형에 따른 상태전이 + 이력 적재) */
    void processCi(CiVO vo);

    /** 형상항목 삭제 */
    void deleteCi(Long ciId);
}

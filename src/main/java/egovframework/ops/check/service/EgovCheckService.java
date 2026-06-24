package egovframework.ops.check.service;

import java.util.List;

/**
 * 운영점검 서비스 인터페이스 (Business 계층).
 *
 * <p>응용프로그램 표준운영절차 — 일일/정기 운영점검의 등록/조회/삭제를 제공한다.</p>
 */
public interface EgovCheckService {

    List<CheckVO> selectCheckList(CheckVO searchVO);

    int selectCheckListCnt(CheckVO searchVO);

    CheckVO selectCheck(Long chkId);

    void insertCheck(CheckVO vo);

    void deleteCheck(Long chkId);
}

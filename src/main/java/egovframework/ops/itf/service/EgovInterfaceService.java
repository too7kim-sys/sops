package egovframework.ops.itf.service;

import java.util.List;

/**
 * 연계관리 서비스 인터페이스 (Business 계층).
 *
 * <p>응용프로그램 표준운영절차 — 연계 요청/조회/처리(상태전이)/완료를 제공한다.</p>
 */
public interface EgovInterfaceService {

    List<InterfaceVO> selectInterfaceList(InterfaceVO searchVO);

    int selectInterfaceListCnt(InterfaceVO searchVO);

    /** 연계 상세 (처리이력 포함) */
    InterfaceVO selectInterface(Long intfId);

    /** 연계 요청(등록) */
    void insertInterface(InterfaceVO vo);

    /** 연계 처리(상태전이) */
    void processInterface(InterfaceVO vo);

    /** 연계 삭제 */
    void deleteInterface(Long intfId);
}

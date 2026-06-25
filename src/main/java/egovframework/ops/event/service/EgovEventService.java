package egovframework.ops.event.service;

import java.util.List;

/**
 * 운영상태관리(이벤트) 서비스 인터페이스.
 *
 * <p>응용프로그램 표준운영절차 — 이벤트 감지/조회/처리(상태전이)/종결을 제공한다.</p>
 */
public interface EgovEventService {

    List<EventVO> selectEventList(EventVO searchVO);

    int selectEventListCnt(EventVO searchVO);

    /** 이벤트 상세 (처리이력 포함) */
    EventVO selectEvent(Long evtId);

    /** 이벤트 감지(등록) */
    void insertEvent(EventVO vo);

    /**
     * 이벤트 처리(상태전이).
     * 상태 변경과 함께 조치 내용/담당자/장애연계를 갱신하고 처리이력을 적재한다.
     */
    void processEvent(EventVO vo);

    /** 이벤트 삭제 */
    void deleteEvent(Long evtId);
}

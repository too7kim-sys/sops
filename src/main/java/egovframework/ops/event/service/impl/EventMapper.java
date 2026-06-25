package egovframework.ops.event.service.impl;

import egovframework.ops.event.service.EventHisVO;
import egovframework.ops.event.service.EventVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 운영상태관리(이벤트) MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface EventMapper {

    List<EventVO> selectEventList(EventVO searchVO);

    int selectEventListCnt(EventVO searchVO);

    EventVO selectEvent(Long evtId);

    List<EventHisVO> selectEventHisList(Long evtId);

    void insertEvent(EventVO vo);

    void updateEventProcess(EventVO vo);

    void insertEventHis(EventHisVO hisVO);

    void deleteEvent(Long evtId);

    void deleteEventHis(Long evtId);
}

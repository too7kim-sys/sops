package egovframework.ops.event.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.event.service.EgovEventService;
import egovframework.ops.event.service.EventHisVO;
import egovframework.ops.event.service.EventVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 운영상태관리(이벤트) 서비스 구현체.
 *
 * <p>이벤트 처리상태 전이 시 처리이력을 함께 적재하여 표준운영절차의
 * 처리 추적성을 보장한다. 에스컬레이션 시 장애(Incident)와 연계한다.</p>
 */
@Service("egovEventService")
public class EgovEventServiceImpl extends EgovAbstractServiceImpl implements EgovEventService {

    private final EventMapper eventMapper;

    public EgovEventServiceImpl(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventVO> selectEventList(EventVO searchVO) {
        return eventMapper.selectEventList(searchVO);
    }

    @Override
    public int selectEventListCnt(EventVO searchVO) {
        return eventMapper.selectEventListCnt(searchVO);
    }

    @Override
    public EventVO selectEvent(Long evtId) {
        EventVO vo = eventMapper.selectEvent(evtId);
        if (vo != null) {
            vo.setHistoryList(eventMapper.selectEventHisList(evtId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertEvent(EventVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("DETECTED");
        }
        eventMapper.insertEvent(vo);
        // 감지 이력 적재
        EventHisVO his = new EventHisVO();
        his.setEvtId(vo.getEvtId());
        his.setStatus(vo.getStatus());
        his.setContent("이벤트 감지");
        his.setProcId(vo.getChargerId());
        eventMapper.insertEventHis(his);
        log.debug("이벤트 감지 : EVT-{}", vo.getEvtId());
    }

    @Override
    @Transactional
    public void processEvent(EventVO vo) {
        eventMapper.updateEventProcess(vo);
        EventHisVO his = new EventHisVO();
        his.setEvtId(vo.getEvtId());
        his.setStatus(vo.getStatus());
        his.setContent(vo.getAction() != null && !vo.getAction().isBlank()
                ? vo.getAction() : "상태 변경");
        his.setProcId(vo.getChargerId());
        eventMapper.insertEventHis(his);
        log.debug("이벤트 처리 : EVT-{} -> {}", vo.getEvtId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void deleteEvent(Long evtId) {
        eventMapper.deleteEventHis(evtId);
        eventMapper.deleteEvent(evtId);
    }
}

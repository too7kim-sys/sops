package egovframework.ops.itf.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.itf.service.EgovInterfaceService;
import egovframework.ops.itf.service.InterfaceHisVO;
import egovframework.ops.itf.service.InterfaceVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 연계관리 서비스 구현체.
 *
 * <p>연계 처리상태 전이 시 처리이력을 함께 적재하여 표준운영절차의
 * 처리 추적성을 보장한다.</p>
 */
@Service("egovInterfaceService")
public class EgovInterfaceServiceImpl extends EgovAbstractServiceImpl implements EgovInterfaceService {

    private final InterfaceMapper interfaceMapper;

    public EgovInterfaceServiceImpl(InterfaceMapper interfaceMapper) {
        this.interfaceMapper = interfaceMapper;
    }

    @Override
    public List<InterfaceVO> selectInterfaceList(InterfaceVO searchVO) {
        return interfaceMapper.selectInterfaceList(searchVO);
    }

    @Override
    public int selectInterfaceListCnt(InterfaceVO searchVO) {
        return interfaceMapper.selectInterfaceListCnt(searchVO);
    }

    @Override
    public InterfaceVO selectInterface(Long intfId) {
        InterfaceVO vo = interfaceMapper.selectInterface(intfId);
        if (vo != null) {
            vo.setHistoryList(interfaceMapper.selectInterfaceHisList(intfId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertInterface(InterfaceVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("REQUESTED");
        }
        interfaceMapper.insertInterface(vo);
        // 요청 이력 적재
        InterfaceHisVO his = new InterfaceHisVO();
        his.setIntfId(vo.getIntfId());
        his.setStatus(vo.getStatus());
        his.setContent("연계 요청 등록");
        his.setProcId(vo.getReqId());
        interfaceMapper.insertInterfaceHis(his);
        log.debug("연계 요청 : INTF-{}", vo.getIntfId());
    }

    @Override
    @Transactional
    public void processInterface(InterfaceVO vo) {
        interfaceMapper.updateInterfaceProcess(vo);
        InterfaceHisVO his = new InterfaceHisVO();
        his.setIntfId(vo.getIntfId());
        his.setStatus(vo.getStatus());
        his.setContent(vo.getResult() != null && !vo.getResult().isBlank()
                ? vo.getResult() : "상태 변경");
        his.setProcId(vo.getChargerId());
        interfaceMapper.insertInterfaceHis(his);
        log.debug("연계 처리 : INTF-{} -> {}", vo.getIntfId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void deleteInterface(Long intfId) {
        interfaceMapper.deleteInterfaceHis(intfId);
        interfaceMapper.deleteInterface(intfId);
    }
}

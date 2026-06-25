package egovframework.ops.csr.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.csr.service.EgovCsrService;
import egovframework.ops.csr.service.CsrHisVO;
import egovframework.ops.csr.service.CsrVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 요청관리(CSR) 서비스 구현체.
 *
 * <p>요청 처리상태 전이 시 처리이력을 함께 적재하여 표준운영절차의
 * 처리 추적성을 보장한다.</p>
 */
@Service("egovCsrService")
public class EgovCsrServiceImpl extends EgovAbstractServiceImpl implements EgovCsrService {

    private final CsrMapper csrMapper;

    public EgovCsrServiceImpl(CsrMapper csrMapper) {
        this.csrMapper = csrMapper;
    }

    @Override
    public List<CsrVO> selectCsrList(CsrVO searchVO) {
        return csrMapper.selectCsrList(searchVO);
    }

    @Override
    public int selectCsrListCnt(CsrVO searchVO) {
        return csrMapper.selectCsrListCnt(searchVO);
    }

    @Override
    public CsrVO selectCsr(Long csrId) {
        CsrVO vo = csrMapper.selectCsr(csrId);
        if (vo != null) {
            vo.setHistoryList(csrMapper.selectCsrHisList(csrId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertCsr(CsrVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("REQUESTED");
        }
        csrMapper.insertCsr(vo);
        // 접수 이력 적재
        CsrHisVO his = new CsrHisVO();
        his.setCsrId(vo.getCsrId());
        his.setStatus(vo.getStatus());
        his.setContent("요청 등록");
        his.setProcId(vo.getReqId());
        csrMapper.insertCsrHis(his);
        log.debug("요청 등록 : CSR-{}", vo.getCsrId());
    }

    @Override
    @Transactional
    public void updateCsr(CsrVO vo) {
        csrMapper.updateCsr(vo);
    }

    @Override
    @Transactional
    public void processCsr(CsrVO vo) {
        csrMapper.updateCsrProcess(vo);
        CsrHisVO his = new CsrHisVO();
        his.setCsrId(vo.getCsrId());
        his.setStatus(vo.getStatus());
        his.setContent(vo.getProcContent() != null && !vo.getProcContent().isBlank()
                ? vo.getProcContent() : "상태 변경");
        his.setProcId(vo.getChargerId());
        csrMapper.insertCsrHis(his);
        log.debug("요청 처리 : CSR-{} -> {}", vo.getCsrId(), vo.getStatus());
    }

    @Override
    @Transactional
    public void deleteCsr(Long csrId) {
        csrMapper.deleteCsrHis(csrId);
        csrMapper.deleteCsr(csrId);
    }
}

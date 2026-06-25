package egovframework.ops.ci.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.ci.service.CiHisVO;
import egovframework.ops.ci.service.CiVO;
import egovframework.ops.ci.service.EgovCiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 형상관리 서비스 구현체.
 *
 * <p>형상통제/감사 시 변경유형(CHECKOUT/CHECKIN/BASELINE/AUDIT)에 따라 형상상태를
 * 전이하고 형상이력을 적재하여 표준운영절차의 형상 추적성을 보장한다.</p>
 */
@Service("egovCiService")
public class EgovCiServiceImpl extends EgovAbstractServiceImpl implements EgovCiService {

    private final CiMapper ciMapper;

    public EgovCiServiceImpl(CiMapper ciMapper) {
        this.ciMapper = ciMapper;
    }

    @Override
    public List<CiVO> selectCiList(CiVO searchVO) {
        return ciMapper.selectCiList(searchVO);
    }

    @Override
    public int selectCiListCnt(CiVO searchVO) {
        return ciMapper.selectCiListCnt(searchVO);
    }

    @Override
    public CiVO selectCi(Long ciId) {
        CiVO vo = ciMapper.selectCi(ciId);
        if (vo != null) {
            vo.setHistoryList(ciMapper.selectCiHisList(ciId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertCi(CiVO vo) {
        if (vo.getCiStatus() == null) {
            vo.setCiStatus("IDENTIFIED");
        }
        ciMapper.insertCi(vo);
        // 형상식별 이력 적재
        CiHisVO his = new CiHisVO();
        his.setCiId(vo.getCiId());
        his.setChgType("IDENTIFY");
        his.setVer(vo.getVer());
        his.setContent("형상항목 식별");
        his.setProcId(vo.getOwnerId());
        ciMapper.insertCiHis(his);
        log.debug("형상항목 식별 : CI-{}", vo.getCiId());
    }

    @Override
    @Transactional
    public void updateCi(CiVO vo) {
        ciMapper.updateCi(vo);
    }

    @Override
    @Transactional
    public void processCi(CiVO vo) {
        // 변경유형에 따른 형상상태 산정
        String chgType = vo.getChgType();
        if ("CHECKOUT".equals(chgType)) {
            vo.setCiStatus("CHECKED_OUT");
        } else if ("CHECKIN".equals(chgType)) {
            vo.setCiStatus("CHECKED_IN");
        } else if ("BASELINE".equals(chgType)) {
            vo.setCiStatus("BASELINED");
        } else {
            // AUDIT 등 — 상태 유지
            vo.setCiStatus(null);
        }
        ciMapper.updateCiStatus(vo);

        CiHisVO his = new CiHisVO();
        his.setCiId(vo.getCiId());
        his.setChgType(chgType);
        his.setVer(vo.getVer());
        his.setContent(vo.getContent());
        his.setProcId(vo.getOwnerId());
        ciMapper.insertCiHis(his);
        log.debug("형상통제/감사 : CI-{} ({})", vo.getCiId(), chgType);
    }

    @Override
    @Transactional
    public void deleteCi(Long ciId) {
        ciMapper.deleteCiHis(ciId);
        ciMapper.deleteCi(ciId);
    }
}

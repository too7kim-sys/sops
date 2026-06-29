package egovframework.ops.csr.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.csr.service.EgovCsrService;
import egovframework.ops.csr.service.CsrHisVO;
import egovframework.ops.csr.service.CsrTplVO;
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
            // 대상 시스템(다중) — 상세 표시 + 수정 폼 선택값
            java.util.List<egovframework.ops.system.service.SystemVO> sysList =
                    csrMapper.selectCsrSysList(csrId);
            vo.setSysList(sysList);
            java.util.List<String> ids = new java.util.ArrayList<>();
            for (egovframework.ops.system.service.SystemVO s : sysList) {
                ids.add(s.getSysId());
            }
            vo.setSysIds(ids);
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertCsr(CsrVO vo) {
        if (vo.getStatus() == null) {
            vo.setStatus("REQUESTED");
        }
        normalizeDueDt(vo);
        applyRepresentativeSys(vo);
        csrMapper.insertCsr(vo);
        saveCsrSys(vo);
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
        normalizeDueDt(vo);
        applyRepresentativeSys(vo);
        csrMapper.updateCsr(vo);
        csrMapper.deleteCsrSys(vo.getCsrId());
        saveCsrSys(vo);
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
        csrMapper.deleteCsrSys(csrId);
        csrMapper.deleteCsrHis(csrId);
        csrMapper.deleteCsr(csrId);
    }

    /** 완료요구일 빈값을 null 로 정규화 (DATE 캐스팅 안전) */
    private void normalizeDueDt(CsrVO vo) {
        if (vo.getDueDt() != null && vo.getDueDt().isBlank()) {
            vo.setDueDt(null);
        }
    }

    /** 다중 선택의 첫번째를 대표 SYS_ID 로 설정 (단일 select 호환·NOT NULL 보장) */
    private void applyRepresentativeSys(CsrVO vo) {
        if (vo.getSysIds() != null && !vo.getSysIds().isEmpty()) {
            vo.setSysId(vo.getSysIds().get(0));
        } else if (vo.getSysId() != null && !vo.getSysId().isBlank()) {
            // 폼에서 다중목록 없이 대표값만 온 경우 보정
            vo.setSysIds(new java.util.ArrayList<>(java.util.List.of(vo.getSysId())));
        }
    }

    /** OPS_CSR_SYS 에 선택 시스템들을 중복 없이 적재 */
    private void saveCsrSys(CsrVO vo) {
        if (vo.getSysIds() == null) {
            return;
        }
        java.util.LinkedHashSet<String> uniq = new java.util.LinkedHashSet<>(vo.getSysIds());
        for (String sysId : uniq) {
            if (sysId != null && !sysId.isBlank()) {
                csrMapper.insertCsrSys(vo.getCsrId(), sysId);
            }
        }
    }

    /* ===== 요청 소분류별 요청내용 템플릿 ===== */

    @Override
    public List<CsrTplVO> selectCsrTplList() {
        return csrMapper.selectCsrTplList();
    }

    @Override
    public List<CsrTplVO> selectCsrTplActive() {
        return csrMapper.selectCsrTplActive();
    }

    @Override
    public CsrTplVO selectCsrTpl(String subType) {
        return csrMapper.selectCsrTpl(subType);
    }

    @Override
    @Transactional
    public void saveCsrTpl(CsrTplVO vo) {
        if (vo.getUseAt() == null || vo.getUseAt().isBlank()) {
            vo.setUseAt("Y");
        }
        // 있으면 수정, 없으면 등록 (소분류 1:1)
        if (csrMapper.updateCsrTpl(vo) == 0) {
            csrMapper.insertCsrTpl(vo);
        }
    }
}

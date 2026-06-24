package egovframework.ops.check.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.check.service.CheckItemVO;
import egovframework.ops.check.service.CheckVO;
import egovframework.ops.check.service.EgovCheckService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 운영점검 서비스 구현체.
 *
 * <p>점검헤더와 점검항목(1:N)을 함께 적재한다. 항목 중 하나라도 ABNORMAL 이면
 * 종합결과를 ABNORMAL 로, 모두 정상이면 NORMAL 로 자동 산정한다.</p>
 */
@Service("egovCheckService")
public class EgovCheckServiceImpl extends EgovAbstractServiceImpl implements EgovCheckService {

    private final CheckMapper checkMapper;

    public EgovCheckServiceImpl(CheckMapper checkMapper) {
        this.checkMapper = checkMapper;
    }

    @Override
    public List<CheckVO> selectCheckList(CheckVO searchVO) {
        return checkMapper.selectCheckList(searchVO);
    }

    @Override
    public int selectCheckListCnt(CheckVO searchVO) {
        return checkMapper.selectCheckListCnt(searchVO);
    }

    @Override
    public CheckVO selectCheck(Long chkId) {
        CheckVO vo = checkMapper.selectCheck(chkId);
        if (vo != null) {
            vo.setItemList(checkMapper.selectCheckItemList(chkId));
        }
        return vo;
    }

    @Override
    @Transactional
    public void insertCheck(CheckVO vo) {
        // 빈 항목(항목명 공백) 제외
        List<CheckItemVO> items = new ArrayList<>();
        if (vo.getItemList() != null) {
            for (CheckItemVO item : vo.getItemList()) {
                if (item != null && item.getItemNm() != null && !item.getItemNm().isBlank()) {
                    items.add(item);
                }
            }
        }

        // 종합결과 산정 : 항목 중 하나라도 ABNORMAL 이면 ABNORMAL
        boolean abnormal = false;
        for (CheckItemVO item : items) {
            if ("ABNORMAL".equals(item.getItemResult())) {
                abnormal = true;
                break;
            }
        }
        vo.setResult(abnormal ? "ABNORMAL" : "NORMAL");

        // 헤더 적재 (useGeneratedKeys 로 chkId 획득)
        checkMapper.insertCheck(vo);

        // 항목 적재
        for (CheckItemVO item : items) {
            item.setChkId(vo.getChkId());
            checkMapper.insertCheckItem(item);
        }
        log.debug("운영점검 등록 : CHK-{} ({}건, 종합결과={})", vo.getChkId(), items.size(), vo.getResult());
    }

    @Override
    @Transactional
    public void deleteCheck(Long chkId) {
        checkMapper.deleteCheckItem(chkId);
        checkMapper.deleteCheck(chkId);
    }
}

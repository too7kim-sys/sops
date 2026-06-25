package egovframework.ops.change.service;

import java.util.List;

/**
 * 변경관리 서비스 인터페이스.
 *
 * <p>응용프로그램 표준운영절차 — 변경요청 등록/조회/심의·승인/적용을 제공한다.</p>
 */
public interface EgovChangeService {

    List<ChangeVO> selectChangeList(ChangeVO searchVO);

    int selectChangeListCnt(ChangeVO searchVO);

    /** 변경 상세 */
    ChangeVO selectChange(Long chgId);

    /** 변경요청 등록 */
    void insertChange(ChangeVO vo);

    /** 변경 기본정보 수정 */
    void updateChange(ChangeVO vo);

    /**
     * 심의/승인 처리.
     * 상태(REVIEWING/APPROVED/REJECTED) 변경과 함께 심의자/심의일시/심의의견을 갱신한다.
     */
    void approveChange(ChangeVO vo);

    /**
     * 적용 처리.
     * 상태(APPLIED/COMPLETED) 변경과 함께 적용일시를 최초값 유지로 기록한다.
     */
    void applyChange(ChangeVO vo);

    /** 변경 삭제 */
    void deleteChange(Long chgId);

    /**
     * CAB(변경자문위원회) 심의 등록.
     * OPS_CHANGE_CAB 에 심의 이력을 적재하고 심의결과에 따라 변경 STATUS 를 자동 갱신한다.
     * (APPROVED→'APPROVED', REJECTED→'REJECTED', HOLD→'REVIEWING')
     */
    void cabReview(ChangeCabVO vo);

    /**
     * 이행후검토(PIR) 기록.
     * 변경이 적용/완료 단계일 때 PIR_CONTENT/PIR_DT 를 갱신한다.
     */
    void recordPir(ChangeVO vo);
}

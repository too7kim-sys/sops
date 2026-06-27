package egovframework.ops.appr.service.impl;

import egovframework.ops.appr.service.ApprLineVO;
import egovframework.ops.appr.service.ShareVO;
import egovframework.ops.sys.user.service.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 결재선/공유 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface ApprMapper {

    List<ApprLineVO> selectLineList(ApprLineVO param);

    ApprLineVO selectLine(Long apprId);

    void insertLine(ApprLineVO vo);

    void deleteLine(Long apprId);

    void actLine(ApprLineVO vo);

    /** 업무 모듈 상태 자동전이 (table/idCol/statusCol 은 내부 화이트리스트 값) */
    void updateBizStatus(@Param("table") String table,
                         @Param("idCol") String idCol,
                         @Param("statusCol") String statusCol,
                         @Param("bizId") Long bizId,
                         @Param("status") String status);

    /** 승인 확정 시 승인자/승인일시 기록 (컬럼명은 내부 화이트리스트 값) */
    void updateBizApprover(@Param("table") String table,
                           @Param("idCol") String idCol,
                           @Param("apprIdCol") String apprIdCol,
                           @Param("apprDtCol") String apprDtCol,
                           @Param("bizId") Long bizId,
                           @Param("apprId") String apprId);

    /** 변경 처리이력(CAB 심의이력)에 결재선 승인/반려 단계 기록 */
    void insertChangeCabHistory(@Param("chgId") Long chgId,
                                @Param("decision") String decision,
                                @Param("opinion") String opinion,
                                @Param("reviewer") String reviewer);

    List<ShareVO> selectShareList(ShareVO param);

    void insertShare(ShareVO vo);

    void deleteShare(Long shareId);

    void updateShareRead(ShareVO param);

    List<ShareVO> selectSharedWithMe(String userId);

    int selectSharedUnreadCnt(String userId);

    List<UserVO> selectAssigneeCandidates();
}

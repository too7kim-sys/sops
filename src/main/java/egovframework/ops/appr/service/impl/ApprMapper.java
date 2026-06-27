package egovframework.ops.appr.service.impl;

import egovframework.ops.appr.service.ApprLineVO;
import egovframework.ops.appr.service.ShareVO;
import egovframework.ops.sys.user.service.UserVO;
import org.apache.ibatis.annotations.Mapper;

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

    List<ShareVO> selectShareList(ShareVO param);

    void insertShare(ShareVO vo);

    void deleteShare(Long shareId);

    void updateShareRead(ShareVO param);

    List<ShareVO> selectSharedWithMe(String userId);

    int selectSharedUnreadCnt(String userId);

    List<UserVO> selectAssigneeCandidates();
}

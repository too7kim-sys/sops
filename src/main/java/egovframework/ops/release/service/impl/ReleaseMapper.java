package egovframework.ops.release.service.impl;

import egovframework.ops.release.service.ReleaseVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 배포관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface ReleaseMapper {

    List<ReleaseVO> selectReleaseList(ReleaseVO searchVO);

    int selectReleaseListCnt(ReleaseVO searchVO);

    ReleaseVO selectRelease(Long relId);

    void insertRelease(ReleaseVO vo);

    void updateRelease(ReleaseVO vo);

    void updateReleaseProcess(ReleaseVO vo);

    void deleteRelease(Long relId);
}

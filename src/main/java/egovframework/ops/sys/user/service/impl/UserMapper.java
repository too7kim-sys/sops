package egovframework.ops.sys.user.service.impl;

import egovframework.ops.sys.user.service.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 사용자 관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface UserMapper {

    List<UserVO> selectUserList(UserVO searchVO);

    int selectUserListCnt(UserVO searchVO);

    UserVO selectUser(String userId);

    /** 활성 사용자 표시정보(아이디/성명/직급) — 성명(직급) 매핑용 */
    List<UserVO> selectUserDisplayList();

    void insertUser(UserVO userVO);

    void updateUser(UserVO userVO);

    void deleteUser(String userId);
}

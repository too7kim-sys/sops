package egovframework.ops.sys.user.service;

import java.util.List;

/**
 * 사용자(운영자) 관리 서비스 인터페이스.
 *
 * <p>표준프레임워크 Business Logic 계층 — 운영자 등록/조회/수정/삭제 및
 * 인증을 위한 단건 조회를 제공한다.</p>
 */
public interface EgovUserService {

    /** 사용자 목록 조회 */
    List<UserVO> selectUserList(UserVO searchVO);

    /** 사용자 목록 전체 건수 */
    int selectUserListCnt(UserVO searchVO);

    /** 사용자 단건 조회 (인증/상세) */
    UserVO selectUser(String userId);

    /** 사용자 등록 */
    void insertUser(UserVO userVO);

    /** 사용자 수정 */
    void updateUser(UserVO userVO);

    /** 사용자 삭제 */
    void deleteUser(String userId);
}

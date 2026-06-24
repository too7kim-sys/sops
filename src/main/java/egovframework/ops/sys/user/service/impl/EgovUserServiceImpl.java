package egovframework.ops.sys.user.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.sys.user.service.EgovUserService;
import egovframework.ops.sys.user.service.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자(운영자) 관리 서비스 구현체.
 */
@Service("egovUserService")
public class EgovUserServiceImpl extends EgovAbstractServiceImpl implements EgovUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public EgovUserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserVO> selectUserList(UserVO searchVO) {
        return userMapper.selectUserList(searchVO);
    }

    @Override
    public int selectUserListCnt(UserVO searchVO) {
        return userMapper.selectUserListCnt(searchVO);
    }

    @Override
    public UserVO selectUser(String userId) {
        return userMapper.selectUser(userId);
    }

    @Override
    @Transactional
    public void insertUser(UserVO userVO) {
        userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));
        if (userVO.getUseAt() == null) {
            userVO.setUseAt("Y");
        }
        userMapper.insertUser(userVO);
        log.debug("운영자 등록 : {}", userVO.getUserId());
    }

    @Override
    @Transactional
    public void updateUser(UserVO userVO) {
        // 비밀번호가 입력된 경우에만 재암호화하여 변경한다.
        if (userVO.getPassword() != null && !userVO.getPassword().isBlank()) {
            userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));
        } else {
            userVO.setPassword(null);
        }
        userMapper.updateUser(userVO);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        userMapper.deleteUser(userId);
    }
}

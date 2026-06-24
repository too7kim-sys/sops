package egovframework.com.config;

import egovframework.ops.sys.user.service.EgovUserService;
import egovframework.ops.sys.user.service.UserVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 표준프레임워크 사용자 인증 처리 서비스.
 *
 * <p>운영자 ID 로 사용자 정보를 조회하여 Spring Security 인증 주체를 생성한다.</p>
 */
@Service
public class EgovUserDetailsService implements UserDetailsService {

    private final EgovUserService egovUserService;

    public EgovUserDetailsService(EgovUserService egovUserService) {
        this.egovUserService = egovUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVO user = egovUserService.selectUser(username);
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다 : " + username);
        }
        return new LoginUser(user);
    }
}

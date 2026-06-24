package egovframework.com.config;

import egovframework.ops.sys.user.service.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security 인증 주체(Principal).
 *
 * <p>인증된 운영자의 {@link UserVO} 를 보관하여 화면/비즈니스 로직에서
 * 사용자 명·부서·권한 정보를 활용할 수 있도록 한다.</p>
 */
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final UserVO user;

    public LoginUser(UserVO user) {
        this.user = user;
    }

    public UserVO getUser() {
        return user;
    }

    public String getUserNm() {
        return user.getUserNm();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "Y".equalsIgnoreCase(user.getUseAt());
    }
}

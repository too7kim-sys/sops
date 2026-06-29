package egovframework.ops.appr.web;

import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.sys.user.service.EgovUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

/**
 * 전역 모델 보강 — 공유함 미열람 건수(헤더 배지)와
 * 사용자ID→성명(직급) 표시 매핑(userNameMap)을 모든 화면에 주입한다.
 */
@ControllerAdvice
public class ApprGlobalAdvice {

    private final EgovApprService apprService;
    private final EgovUserService userService;

    public ApprGlobalAdvice(EgovApprService apprService, EgovUserService userService) {
        this.apprService = apprService;
        this.userService = userService;
    }

    /** 사용자ID → "성명(직급)" 매핑 — JSP 에서 ${uf:nm(userNameMap, id)} 로 사용 */
    @ModelAttribute("userNameMap")
    public Map<String, String> userNameMap() {
        return userService.selectUserNameMap();
    }

    @ModelAttribute("sharedUnreadCnt")
    public int sharedUnreadCnt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser) {
            LoginUser user = (LoginUser) auth.getPrincipal();
            return apprService.selectSharedUnreadCnt(user.getUsername());
        }
        return 0;
    }
}

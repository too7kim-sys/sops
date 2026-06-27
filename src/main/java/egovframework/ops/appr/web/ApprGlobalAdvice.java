package egovframework.ops.appr.web;

import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 전역 모델 보강 — 공유함 미열람 건수를 모든 화면(헤더 배지)에 주입한다.
 */
@ControllerAdvice
public class ApprGlobalAdvice {

    private final EgovApprService apprService;

    public ApprGlobalAdvice(EgovApprService apprService) {
        this.apprService = apprService;
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

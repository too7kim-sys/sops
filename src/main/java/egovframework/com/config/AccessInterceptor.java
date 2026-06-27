package egovframework.com.config;

import egovframework.ops.appr.service.EgovApprService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 업무 레코드 접근통제 인터셉터.
 *
 * <p>운영관리자(ADMIN)를 제외한 사용자는 해당 업무의 <b>요청자/처리자/검토자/승인자/심의자(결재선)</b>
 * 또는 <b>공유 대상자</b>인 경우에만 상세·수정·삭제·처리 화면에 접근할 수 있다.
 * 권한이 없으면 {@code /denied} 로 리다이렉트한다.</p>
 */
public class AccessInterceptor implements HandlerInterceptor {

    private final EgovApprService apprService;

    public AccessInterceptor(EgovApprService apprService) {
        this.apprService = apprService;
    }

    /** URL 첫 세그먼트 → 업무 구분 */
    private static final Map<String, String> SEG_BIZ = new LinkedHashMap<>();
    /** URL 첫 세그먼트 → 레코드 ID 파라미터명 */
    private static final Map<String, String> SEG_IDPARAM = new LinkedHashMap<>();
    static {
        SEG_BIZ.put("csr", "CSR");           SEG_IDPARAM.put("csr", "csrId");
        SEG_BIZ.put("change", "CHANGE");     SEG_IDPARAM.put("change", "chgId");
        SEG_BIZ.put("release", "RELEASE");   SEG_IDPARAM.put("release", "relId");
        SEG_BIZ.put("incident", "INCIDENT"); SEG_IDPARAM.put("incident", "incId");
        SEG_BIZ.put("problem", "PROBLEM");   SEG_IDPARAM.put("problem", "prbId");
        SEG_BIZ.put("interface", "INTERFACE"); SEG_IDPARAM.put("interface", "intfId");
        SEG_BIZ.put("test", "TEST");         SEG_IDPARAM.put("test", "testId");
        SEG_BIZ.put("ci", "CI");             SEG_IDPARAM.put("ci", "ciId");
        SEG_BIZ.put("event", "EVENT");       SEG_IDPARAM.put("event", "evtId");
    }

    private Long parseLong(String s) {
        if (s == null || !s.matches("\\d+")) {
            return null;
        }
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof LoginUser)) {
            return true; // 미인증 → 시큐리티에 위임
        }
        LoginUser user = (LoginUser) auth.getPrincipal();
        if ("ADMIN".equals(user.getUser().getRole())) {
            return true; // 운영관리자는 전체 접근
        }

        String ctx = request.getContextPath();
        String path = request.getRequestURI().substring(ctx.length());
        String[] parts = path.split("/");
        if (parts.length < 2) {
            return true;
        }
        String seg = parts[1];

        String bizType;
        Long bizId;
        if ("appr".equals(seg)) {
            // 결재선/공유 패널 등 — 파라미터로 대상 식별
            bizType = request.getParameter("bizType");
            bizId = parseLong(request.getParameter("bizId"));
            if (bizType == null || bizId == null) {
                return true; // 식별 불가(act/shared 등) → 컨트롤러 자체 가드에 위임
            }
        } else {
            bizType = SEG_BIZ.get(seg);
            if (bizType == null) {
                return true; // 업무 모듈 아님
            }
            // 경로형 ID (/seg/detail|edit|delete/{id})
            bizId = (parts.length >= 4) ? parseLong(parts[3]) : null;
            if (bizId == null) {
                bizId = parseLong(request.getParameter(SEG_IDPARAM.get(seg)));
            }
            if (bizId == null) {
                return true; // 목록/등록 등 특정 레코드 없음 → 허용
            }
        }

        if (apprService.canAccess(bizType, bizId, user.getUsername())) {
            return true;
        }
        response.sendRedirect(ctx + "/denied");
        return false;
    }
}

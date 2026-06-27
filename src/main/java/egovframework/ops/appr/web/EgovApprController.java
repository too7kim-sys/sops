package egovframework.ops.appr.web;

import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.ApprLineVO;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.appr.service.ShareVO;
import egovframework.ops.cmm.code.service.EgovCodeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 결재선(검토/승인/처리자 라인) · 공유 컨트롤러 (Presentation 계층, 공통 컴포넌트).
 *
 * <p>업무 상세화면에 {@code <c:import url="/appr/panel"/>} 로 포함되어 결재선 관리,
 * 병렬 검토/승인, 공유 기능을 제공한다. 처리 후에는 호출 화면(returnUrl)으로 복귀한다.</p>
 */
@Controller
@RequestMapping("/appr")
public class EgovApprController {

    private final EgovApprService apprService;
    private final EgovCodeService codeService;

    public EgovApprController(EgovApprService apprService, EgovCodeService codeService) {
        this.apprService = apprService;
        this.codeService = codeService;
    }

    /** 업무 구분 → 한글명 / 상세 URL prefix */
    private static final Map<String, String[]> BIZ = new LinkedHashMap<>();
    static {
        BIZ.put("CHANGE",    new String[]{"변경관리", "/change/detail/"});
        BIZ.put("RELEASE",   new String[]{"배포관리", "/release/detail/"});
        BIZ.put("CSR",       new String[]{"요청관리", "/csr/detail/"});
        BIZ.put("INCIDENT",  new String[]{"장애관리", "/incident/detail/"});
        BIZ.put("PROBLEM",   new String[]{"문제관리", "/problem/detail/"});
        BIZ.put("TEST",      new String[]{"테스트관리", "/test/detail/"});
        BIZ.put("INTERFACE", new String[]{"연계관리", "/interface/detail/"});
        BIZ.put("CI",        new String[]{"형상관리", "/ci/detail/"});
        BIZ.put("EVENT",     new String[]{"운영상태", "/event/detail/"});
    }

    private String bizTypeNm(String bizType) {
        String[] m = BIZ.get(bizType);
        return m != null ? m[0] : bizType;
    }

    private String defaultDetailUrl(String ctx, String bizType, Long bizId) {
        String[] m = BIZ.get(bizType);
        return m != null ? ctx + m[1] + bizId : ctx + "/main";
    }

    /** 오픈 리다이렉트 방지: 앱 내부 상대경로만 허용 */
    private String safeReturn(String returnUrl, String ctx, String bizType, Long bizId) {
        if (returnUrl != null && returnUrl.startsWith("/") && !returnUrl.startsWith("//")) {
            return returnUrl;
        }
        return defaultDetailUrl(ctx, bizType, bizId);
    }

    private boolean isManager(LoginUser user) {
        String role = user.getUser().getRole();
        return "ADMIN".equals(role) || "OPERATOR".equals(role);
    }

    /* ============================ 패널 ============================ */

    /** 결재선/공유 패널 (상세화면에 c:import 로 포함) */
    @GetMapping("/panel")
    public String panel(@RequestParam String bizType,
                        @RequestParam Long bizId,
                        @RequestParam(required = false) String returnUrl,
                        @AuthenticationPrincipal LoginUser loginUser,
                        Model model) {

        String loginId = loginUser.getUsername();
        // 공유 대상자가 조회하면 열람 처리
        apprService.markShareRead(bizType, bizId, loginId);

        List<ApprLineVO> lines = apprService.selectLineList(bizType, bizId);
        List<ShareVO> shares = apprService.selectShareList(bizType, bizId);

        // 진행 집계 (병렬 라인 기준)
        int approveTotal = 0, approveDone = 0, reviewTotal = 0, reviewDone = 0, handleTotal = 0, handleDone = 0;
        boolean rejected = false;
        int myPending = 0;
        for (ApprLineVO l : lines) {
            boolean done = !"PENDING".equals(l.getStatus());
            if ("APPROVE".equals(l.getLineType())) {
                approveTotal++;
                if ("APPROVED".equals(l.getStatus())) approveDone++;
                if ("REJECTED".equals(l.getStatus())) rejected = true;
            } else if ("REVIEW".equals(l.getLineType())) {
                reviewTotal++;
                if (done) reviewDone++;
            } else if ("HANDLE".equals(l.getLineType())) {
                handleTotal++;
                if (done) handleDone++;
            }
            if (loginId.equals(l.getAssigneeId()) && "PENDING".equals(l.getStatus())) myPending++;
        }
        String overallKey, overallNm;
        if (rejected) {
            overallKey = "REJECTED"; overallNm = "반려";
        } else if (approveTotal > 0 && approveDone == approveTotal) {
            overallKey = "APPROVED"; overallNm = "승인완료";
        } else if (lines.isEmpty()) {
            overallKey = "NONE"; overallNm = "결재선 없음";
        } else {
            overallKey = "PENDING"; overallNm = "진행중";
        }

        model.addAttribute("bizType", bizType);
        model.addAttribute("bizId", bizId);
        model.addAttribute("bizTypeNm", bizTypeNm(bizType));
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("lineList", lines);
        model.addAttribute("shares", shares);
        model.addAttribute("candidates", apprService.selectAssigneeCandidates());
        model.addAttribute("lineTypeList", codeService.selectCodeList("LINE_TYPE"));
        model.addAttribute("loginId", loginId);
        model.addAttribute("isManager", isManager(loginUser));
        model.addAttribute("approveTotal", approveTotal);
        model.addAttribute("approveDone", approveDone);
        model.addAttribute("reviewTotal", reviewTotal);
        model.addAttribute("reviewDone", reviewDone);
        model.addAttribute("handleTotal", handleTotal);
        model.addAttribute("handleDone", handleDone);
        model.addAttribute("myPending", myPending);
        model.addAttribute("overallKey", overallKey);
        model.addAttribute("overallNm", overallNm);
        return "appr/panel";
    }

    /* ============================ 결재선 ============================ */

    /** 결재선 추가 (선택 대상자마다 1건, 동일 단계 = 병렬) */
    @PostMapping("/line/add")
    public String addLine(@RequestParam String bizType,
                          @RequestParam Long bizId,
                          @RequestParam String lineType,
                          @RequestParam(defaultValue = "1") Integer stepNo,
                          @RequestParam(name = "assigneeId", required = false) List<String> assigneeIds,
                          @RequestParam(required = false) String returnUrl,
                          @AuthenticationPrincipal LoginUser loginUser) {
        String ctx = ctx();
        if (isManager(loginUser) && assigneeIds != null) {
            int sort = 1;
            for (String aid : assigneeIds) {
                if (aid == null || aid.isBlank()) continue;
                ApprLineVO vo = new ApprLineVO();
                vo.setBizType(bizType);
                vo.setBizId(bizId);
                vo.setLineType(lineType);
                vo.setStepNo(stepNo);
                vo.setSortNo(sort++);
                vo.setAssigneeId(aid);
                vo.setStatus("PENDING");
                vo.setRegId(loginUser.getUsername());
                apprService.insertLine(vo);
            }
        }
        return "redirect:" + safeReturn(returnUrl, ctx, bizType, bizId);
    }

    /** 결재선 삭제 */
    @PostMapping("/line/delete")
    public String deleteLine(@RequestParam Long apprId,
                             @RequestParam String bizType,
                             @RequestParam Long bizId,
                             @RequestParam(required = false) String returnUrl,
                             @AuthenticationPrincipal LoginUser loginUser) {
        if (isManager(loginUser)) {
            apprService.deleteLine(apprId);
        }
        return "redirect:" + safeReturn(returnUrl, ctx(), bizType, bizId);
    }

    /** 결재 처리(검토/승인/반려/처리완료) — 본인 라인 또는 관리자만 */
    @PostMapping("/act")
    public String act(@RequestParam Long apprId,
                      @RequestParam String action,
                      @RequestParam(required = false) String opinion,
                      @RequestParam(required = false) String returnUrl,
                      @AuthenticationPrincipal LoginUser loginUser) {
        ApprLineVO line = apprService.selectLine(apprId);
        if (line == null) {
            return "redirect:" + ctx() + "/main";
        }
        boolean allowed = loginUser.getUsername().equals(line.getAssigneeId())
                || "ADMIN".equals(loginUser.getUser().getRole());
        if (allowed) {
            String status;
            switch (action) {
                case "APPROVE": status = "APPROVED"; break;
                case "REJECT":  status = "REJECTED"; break;
                case "REVIEW":  status = "REVIEWED"; break;
                case "DONE":    status = "DONE";     break;
                default:        status = null;
            }
            if (status != null) {
                line.setStatus(status);
                line.setOpinion(opinion);
                apprService.actLine(line);
                // 결재 결과를 업무 모듈 상태에 자동 반영(승인 게이트 정의 모듈) + 승인자/승인일시 기록
                apprService.applyModuleOutcome(line.getBizType(), line.getBizId(), loginUser.getUsername());
            }
        }
        return "redirect:" + safeReturn(returnUrl, ctx(), line.getBizType(), line.getBizId());
    }

    /* ============================ 공유 ============================ */

    /** 공유 추가 (선택 대상자마다 1건) */
    @PostMapping("/share/add")
    public String addShare(@RequestParam String bizType,
                           @RequestParam Long bizId,
                           @RequestParam(name = "userId", required = false) List<String> userIds,
                           @RequestParam(required = false) String shareMemo,
                           @RequestParam(required = false) String returnUrl,
                           @AuthenticationPrincipal LoginUser loginUser) {
        if (userIds != null) {
            for (String uid : userIds) {
                if (uid == null || uid.isBlank()) continue;
                ShareVO vo = new ShareVO();
                vo.setBizType(bizType);
                vo.setBizId(bizId);
                vo.setUserId(uid);
                vo.setShareMemo(shareMemo);
                vo.setSharedBy(loginUser.getUsername());
                apprService.insertShare(vo);
            }
        }
        return "redirect:" + safeReturn(returnUrl, ctx(), bizType, bizId);
    }

    /** 공유 삭제 */
    @PostMapping("/share/delete")
    public String deleteShare(@RequestParam Long shareId,
                              @RequestParam String bizType,
                              @RequestParam Long bizId,
                              @RequestParam(required = false) String returnUrl,
                              @AuthenticationPrincipal LoginUser loginUser) {
        apprService.deleteShare(shareId);
        return "redirect:" + safeReturn(returnUrl, ctx(), bizType, bizId);
    }

    /** 공유함 — 나에게 공유된 업무 목록 */
    @GetMapping("/shared")
    public String sharedWithMe(@AuthenticationPrincipal LoginUser loginUser, Model model) {
        List<ShareVO> list = apprService.selectSharedWithMe(loginUser.getUsername());
        for (ShareVO s : list) {
            s.setBizTypeNm(bizTypeNm(s.getBizType()));
            String[] m = BIZ.get(s.getBizType());
            s.setDetailUrl(m != null ? m[1] + s.getBizId() : "/main");
        }
        model.addAttribute("sharedList", list);
        model.addAttribute("menu", "shared");
        return "appr/shared";
    }

    /** 컨텍스트 경로 (리다이렉트는 컨텍스트 상대이므로 빈 문자열) */
    private String ctx() {
        return "";
    }
}

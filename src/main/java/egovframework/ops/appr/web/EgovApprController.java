package egovframework.ops.appr.web;

import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.ApprLineVO;
import egovframework.ops.appr.service.ApprTemplateVO;
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

    /** 업무 구분 → 한글명 / 상세 URL prefix (메뉴 노출 순서대로 정의) */
    private static final Map<String, String[]> BIZ = new LinkedHashMap<>();
    static {
        BIZ.put("CSR",       new String[]{"요청관리", "/csr/detail/"});
        BIZ.put("CHANGE",    new String[]{"변경관리", "/change/detail/"});
        BIZ.put("RELEASE",   new String[]{"배포관리", "/release/detail/"});
        BIZ.put("TEST",      new String[]{"테스트관리", "/test/detail/"});
        BIZ.put("INTERFACE", new String[]{"연계관리", "/interface/detail/"});
        BIZ.put("CI",        new String[]{"형상관리", "/ci/detail/"});
        BIZ.put("EVENT",     new String[]{"운영상태관리", "/event/detail/"});
        BIZ.put("INCIDENT",  new String[]{"장애관리", "/incident/detail/"});
        BIZ.put("PROBLEM",   new String[]{"문제관리", "/problem/detail/"});
    }

    private String bizTypeNm(String bizType) {
        String[] m = BIZ.get(bizType);
        return m != null ? m[0] : bizType;
    }

    /** 업무구분 콤보용 코드→한글명 (메뉴순서 유지) */
    private Map<String, String> bizTypeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> e : BIZ.entrySet()) {
            map.put(e.getKey(), e.getValue()[0]);
        }
        return map;
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

        // 기본 결재선/공유 자동 구성 : 결재선·공유가 모두 비어 있고 기본설정(템플릿)이 있으면 지연 적용
        // (신규 등록 시점뿐 아니라 기존/시드 데이터도 패널 조회 시 기본 결재선이 보이도록)
        if (lines.isEmpty() && shares.isEmpty()) {
            int created = apprService.applyTemplate(bizType, bizId, loginId);
            if (created > 0) {
                lines = apprService.selectLineList(bizType, bizId);
                shares = apprService.selectShareList(bizType, bizId);
            }
        }

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
        model.addAttribute("targetTypeList", codeService.selectCodeList("TARGET_TYPE"));
        model.addAttribute("deptList", apprService.selectDeptList());
        model.addAttribute("hasTemplate", !apprService.selectTemplateList(bizType).isEmpty());
        model.addAttribute("loginId", loginId);
        model.addAttribute("isManager", isManager(loginUser));
        model.addAttribute("isAdmin", "ADMIN".equals(loginUser.getUser().getRole()));
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

    /** 결재선 추가 — 대상 유형(사용자/부서/요청자/전체)을 실제 사용자로 전개, 동일 단계 = 병렬 */
    @PostMapping("/line/add")
    public String addLine(@RequestParam String bizType,
                          @RequestParam Long bizId,
                          @RequestParam String lineType,
                          @RequestParam(defaultValue = "1") Integer stepNo,
                          @RequestParam(defaultValue = "USER") String targetType,
                          @RequestParam(name = "assigneeId", required = false) List<String> assigneeIds,
                          @RequestParam(required = false) String targetValue,
                          @RequestParam(required = false) String returnUrl,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (isManager(loginUser)) {
            int sort = 1;
            for (String aid : resolveAssignees(bizType, bizId, targetType, assigneeIds, targetValue)) {
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
        return "redirect:" + safeReturn(returnUrl, ctx(), bizType, bizId);
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
                // 결재 결과를 업무 모듈 상태에 자동 반영 + 승인자/승인일시 + 변경 처리이력(CAB) 기록
                apprService.applyModuleOutcome(line.getBizType(), line.getBizId(),
                        loginUser.getUsername(), loginUser.getUserNm(), opinion);
            }
        }
        return "redirect:" + safeReturn(returnUrl, ctx(), line.getBizType(), line.getBizId());
    }

    /* ============================ 공유 ============================ */

    /** 공유 추가 — 대상 유형(사용자/부서/요청자/전체)을 실제 사용자로 전개 */
    @PostMapping("/share/add")
    public String addShare(@RequestParam String bizType,
                           @RequestParam Long bizId,
                           @RequestParam(defaultValue = "USER") String targetType,
                           @RequestParam(name = "userId", required = false) List<String> userIds,
                           @RequestParam(required = false) String targetValue,
                           @RequestParam(required = false) String shareMemo,
                           @RequestParam(required = false) String returnUrl,
                           @AuthenticationPrincipal LoginUser loginUser) {
        for (String uid : resolveAssignees(bizType, bizId, targetType, userIds, targetValue)) {
            ShareVO vo = new ShareVO();
            vo.setBizType(bizType);
            vo.setBizId(bizId);
            vo.setUserId(uid);
            vo.setShareMemo(shareMemo);
            vo.setSharedBy(loginUser.getUsername());
            apprService.insertShare(vo);
        }
        return "redirect:" + safeReturn(returnUrl, ctx(), bizType, bizId);
    }

    /**
     * 대상 유형을 실제 사용자 ID 목록(중복 제거)으로 전개.
     * USER 는 다중 선택값(userIds), 그 외는 서비스 전개 규칙을 사용한다.
     */
    private List<String> resolveAssignees(String bizType, Long bizId, String targetType,
                                          List<String> userIds, String targetValue) {
        List<String> ids;
        if (targetType == null || "USER".equals(targetType)) {
            ids = (userIds == null) ? List.of() : userIds;
        } else {
            ids = apprService.resolveTargets(bizType, bizId, targetType, targetValue);
        }
        java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>();
        for (String id : ids) {
            if (id != null && !id.isBlank()) {
                set.add(id);
            }
        }
        return new java.util.ArrayList<>(set);
    }

    /* ============================ 기본 템플릿 ============================ */

    /** 업무 구분별 기본 템플릿을 현재 레코드에 적용(전개) */
    @PostMapping("/applyTemplate")
    public String applyTemplate(@RequestParam String bizType,
                                @RequestParam Long bizId,
                                @RequestParam(required = false) String returnUrl,
                                @AuthenticationPrincipal LoginUser loginUser) {
        if (isManager(loginUser)) {
            apprService.applyTemplate(bizType, bizId, loginUser.getUsername());
        }
        return "redirect:" + safeReturn(returnUrl, ctx(), bizType, bizId);
    }

    /** 기본 설정(템플릿) 관리 화면 — 운영관리자 */
    @GetMapping("/template")
    public String templateAdmin(@RequestParam(required = false) String bizType, Model model) {
        String sel = (bizType == null || bizType.isBlank()) ? "CSR" : bizType;
        model.addAttribute("bizType", sel);
        model.addAttribute("bizTypeNm", bizTypeNm(sel));
        model.addAttribute("bizTypes", bizTypeMap());
        model.addAttribute("templateList", apprService.selectTemplateList(sel));
        model.addAttribute("lineTypeList", codeService.selectCodeList("LINE_TYPE"));
        model.addAttribute("targetTypeList", codeService.selectCodeList("TARGET_TYPE"));
        model.addAttribute("deptList", apprService.selectDeptList());
        model.addAttribute("candidates", apprService.selectAssigneeCandidates());
        model.addAttribute("menu", "apprTpl");
        return "appr/template";
    }

    /** 템플릿 행 추가 */
    @PostMapping("/template/add")
    public String addTemplate(@ModelAttribute ApprTemplateVO vo,
                              @AuthenticationPrincipal LoginUser loginUser) {
        if ("ADMIN".equals(loginUser.getUser().getRole())) {
            if ("SHARE".equals(vo.getKind())) {
                vo.setLineType(null);
            }
            if (vo.getStepNo() == null) vo.setStepNo(1);
            if (vo.getSortNo() == null) vo.setSortNo(1);
            apprService.insertTemplate(vo);
        }
        return "redirect:" + ctx() + "/appr/template?bizType=" + vo.getBizType();
    }

    /** 템플릿 행 삭제 */
    @PostMapping("/template/delete")
    public String deleteTemplate(@RequestParam Long tplId,
                                 @RequestParam String bizType,
                                 @AuthenticationPrincipal LoginUser loginUser) {
        if ("ADMIN".equals(loginUser.getUser().getRole())) {
            apprService.deleteTemplate(tplId);
        }
        return "redirect:" + ctx() + "/appr/template?bizType=" + bizType;
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

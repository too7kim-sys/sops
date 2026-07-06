package egovframework.ops.change.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.change.service.ChangeCabVO;
import egovframework.ops.change.service.ChangeVO;
import egovframework.ops.change.service.EgovChangeService;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 변경관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 변경요청/조회/심의·승인/적용 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/change")
public class EgovChangeController {

    private final EgovChangeService changeService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;
    private final EgovApprService apprService;
    private final egovframework.ops.change.service.impl.ChangeTransferService transferService;

    public EgovChangeController(EgovChangeService changeService,
                                EgovSystemService systemService,
                                EgovCodeService codeService,
                                EgovApprService apprService,
                                egovframework.ops.change.service.impl.ChangeTransferService transferService) {
        this.changeService = changeService;
        this.systemService = systemService;
        this.codeService = codeService;
        this.apprService = apprService;
        this.transferService = transferService;
    }

    /** 변경 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") ChangeVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = changeService.selectChangeListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("changeList", changeService.selectChangeList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("CHANGE_STATUS"));
        model.addAttribute("menu", "change");
        return "change/list";
    }

    /** 변경 상세 */
    @GetMapping("/detail/{chgId}")
    public String detail(@PathVariable Long chgId, Model model,
                         @AuthenticationPrincipal LoginUser loginUser) {
        ChangeVO change = changeService.selectChange(chgId);
        // 중요도 2등급 이하(CAB 미대상)는 검토·승인을 자동 완료 — 이관 등으로 등록 시 자동승인이
        // 누락된 건도 상세 접근 시 보정(그렇지 않으면 검토 단계에 막혀 '이전 단계 진행 중'으로 결재 불가).
        // 승인 이후 상태(APPROVED/APPLIED/COMPLETED/REJECTED)에서는 재실행하지 않는다(적용/완료 상태 되돌림 방지).
        if (change != null
                && ("REQUESTED".equals(change.getStatus()) || "REVIEWING".equals(change.getStatus()))
                && !isGrade1(change.getSysId())) {
            apprService.autoReviewApprove("CHANGE", chgId, loginUser.getUsername(),
                    "자동 검토·승인(중요도 2등급 이하 · CAB 심의 생략)");
            change = changeService.selectChange(chgId); // 자동 완료 반영된 상태로 재조회
        }
        model.addAttribute("change", change);
        // CAB 심의는 검토자(결재선 REVIEW 담당자)·운영관리자만 수행 — 폼 노출 제어
        model.addAttribute("canReview", canReview(chgId, loginUser));
        // 변경 처리는 처리자(결재선 HANDLE 담당자)·운영관리자만 수행 — 폼 노출 제어
        model.addAttribute("canProcess", canProcess(chgId, loginUser));
        // 변경 처리는 검토·승인이 모두 완료되어야 가능 — 폼 활성 제어
        model.addAttribute("apprComplete", isApprComplete(chgId));
        // CAB 심의는 중요도 1등급 시스템만 수행 — 그 외 등급은 CAB 폼 미노출(자동 검토·승인)
        model.addAttribute("cabRequired", change != null && isGrade1(change.getSysId()));
        model.addAttribute("transferTargets", egovframework.ops.change.service.impl.ChangeTransferService.TARGETS);
        model.addAttribute("statusList", codeService.selectCodeList("CHANGE_STATUS"));
        model.addAttribute("procTypeList", codeService.selectCodeList("CHANGE_PROC_TYPE"));
        model.addAttribute("cabDecisionList", codeService.selectCodeList("CAB_DECISION"));
        // CAB 심의위원 기본값 : 요청자 + 심의자 + 결재선(검토/승인/처리) 대상자 (중복 제거, 순서 유지)
        java.util.LinkedHashSet<String> members = new java.util.LinkedHashSet<>();
        if (change != null) {
            addMember(members, change.getReqId());
            addMember(members, change.getApprId());
        }
        for (egovframework.ops.appr.service.ApprLineVO ln : apprService.selectLineList("CHANGE", chgId)) {
            addMember(members, ln.getAssigneeId());
        }
        model.addAttribute("cabMembers", new java.util.ArrayList<>(members));
        model.addAttribute("userCandidates", apprService.selectAssigneeCandidates());
        model.addAttribute("menu", "change");
        return "change/detail";
    }

    /** 공백·중복 없이 심의위원 후보 추가 */
    private void addMember(java.util.Set<String> set, String id) {
        if (id != null && !id.isBlank()) {
            set.add(id);
        }
    }

    /** 검토자 여부 — 결재선의 검토(REVIEW) 담당자 본인 */
    private boolean isReviewer(Long chgId, LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        String uid = loginUser.getUsername();
        for (egovframework.ops.appr.service.ApprLineVO ln : apprService.selectLineList("CHANGE", chgId)) {
            if ("REVIEW".equals(ln.getLineType()) && uid.equals(ln.getAssigneeId())) {
                return true;
            }
        }
        return false;
    }

    /** CAB 심의 가능 여부 — 검토자 본인이거나 운영관리자 */
    private boolean canReview(Long chgId, LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        return "ADMIN".equals(loginUser.getUser().getRole()) || isReviewer(chgId, loginUser);
    }

    /** 처리자 여부 — 결재선의 처리(HANDLE) 담당자 본인 */
    private boolean isChanger(Long chgId, LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        String uid = loginUser.getUsername();
        for (egovframework.ops.appr.service.ApprLineVO ln : apprService.selectLineList("CHANGE", chgId)) {
            if ("HANDLE".equals(ln.getLineType()) && uid.equals(ln.getAssigneeId())) {
                return true;
            }
        }
        return false;
    }

    /** 대상 시스템의 중요도등급이 1등급인지 — 1등급만 CAB 심의 수행 */
    private boolean isGrade1(String sysId) {
        if (sysId == null || sysId.isBlank()) {
            return false;
        }
        egovframework.ops.system.service.SystemVO sys = systemService.selectSystem(sysId);
        return sys != null && "1".equals(sys.getGrad());
    }

    /** 변경 처리 가능 여부 — 처리자 본인이거나 운영관리자 */
    private boolean canProcess(Long chgId, LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        return "ADMIN".equals(loginUser.getUser().getRole()) || isChanger(chgId, loginUser);
    }

    /**
     * 검토·승인 완료 여부 — 결재선의 모든 검토(REVIEW) 라인이 REVIEWED,
     * 모든 승인(APPROVE) 라인이 APPROVED 여야 변경 처리 가능.
     * 검토/승인 라인이 하나도 없으면 아직 완료로 보지 않는다.
     */
    private boolean isApprComplete(Long chgId) {
        boolean hasGate = false;
        for (egovframework.ops.appr.service.ApprLineVO ln : apprService.selectLineList("CHANGE", chgId)) {
            if ("REVIEW".equals(ln.getLineType())) {
                hasGate = true;
                if (!"REVIEWED".equals(ln.getStatus())) {
                    return false;
                }
            } else if ("APPROVE".equals(ln.getLineType())) {
                hasGate = true;
                if (!"APPROVED".equals(ln.getStatus())) {
                    return false;
                }
            }
        }
        return hasGate;
    }

    /** 변경요청 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("change", new ChangeVO());
        addFormCodes(model);
        model.addAttribute("menu", "change");
        return "change/form";
    }

    /** 변경요청 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute ChangeVO changeVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        changeVO.setReqId(loginUser.getUsername());
        changeService.insertChange(changeVO);
        // 결재 기본설정(템플릿) 자동 적용 — 결재/검토/공유/처리자 라인을 기본설정에 따라 생성
        apprService.applyTemplate("CHANGE", changeVO.getChgId(), loginUser.getUsername());
        // CAB 심의는 중요도 1등급 시스템만 수행, 그 외 등급은 CAB 없이 자동 검토·승인
        if (!isGrade1(changeVO.getSysId())) {
            apprService.autoReviewApprove("CHANGE", changeVO.getChgId(), loginUser.getUsername(),
                    "자동 검토·승인(중요도 2등급 이하 · CAB 심의 생략)");
        }
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 변경 기본정보 수정 폼 */
    @GetMapping("/edit/{chgId}")
    public String editForm(@PathVariable Long chgId, Model model) {
        model.addAttribute("change", changeService.selectChange(chgId));
        addFormCodes(model);
        model.addAttribute("menu", "change");
        return "change/form";
    }

    /** 변경 기본정보 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute ChangeVO changeVO) {
        changeService.updateChange(changeVO);
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 심의/승인 처리 */
    @PostMapping("/approve")
    public String approve(@ModelAttribute ChangeVO changeVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        changeVO.setApprId(loginUser.getUsername());
        changeService.approveChange(changeVO);
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 변경 처리(적용/완료) — 처리자만, 적용 시 배포요청/장애관리로 이관 가능 */
    @PostMapping("/apply")
    public String apply(@ModelAttribute ChangeVO changeVO,
                        @RequestParam(required = false) String transferTo,
                        @AuthenticationPrincipal LoginUser loginUser) {
        // 변경 처리는 처리자(결재선 HANDLE)·운영관리자만, 검토·승인 완료 후에만
        if (!canProcess(changeVO.getChgId(), loginUser) || !isApprComplete(changeVO.getChgId())) {
            return "redirect:/change/detail/" + changeVO.getChgId();
        }
        changeService.applyChange(changeVO);
        // 적용 등록 시 후속 업무(배포요청/장애관리)로 이관 선택 시 대상 생성 후 이동
        if ("APPLIED".equals(changeVO.getStatus()) && transferTo != null && !transferTo.isBlank()) {
            String detailUrl = transferService.transfer(changeVO.getChgId(), transferTo, loginUser.getUsername());
            return "redirect:" + detailUrl;
        }
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    /** 변경 삭제 */
    @PostMapping("/delete/{chgId}")
    public String delete(@PathVariable Long chgId) {
        changeService.deleteChange(chgId);
        return "redirect:/change/list";
    }

    /** CAB(변경자문위원회) 심의 등록 */
    @PostMapping("/cab")
    public String cab(@ModelAttribute ChangeCabVO cabVO,
                      @AuthenticationPrincipal LoginUser loginUser) {
        // CAB 심의는 검토자(결재선 REVIEW 담당자)·운영관리자만 수행
        if (!canReview(cabVO.getChgId(), loginUser)) {
            return "redirect:/change/detail/" + cabVO.getChgId();
        }
        if (cabVO.getReviewer() == null || cabVO.getReviewer().isBlank()) {
            cabVO.setReviewer(loginUser.getUsername());
        }
        changeService.cabReview(cabVO);
        // 변경관리는 검토(REVIEW)를 CAB 심의로 수행하므로, 심의 등록 시 결재선의 검토 단계도 완료 처리한다.
        // (검토 라인이 대기로 남으면 단계 게이트에 걸려 승인이 되지 않음)
        String reviewStatus = "REJECTED".equals(cabVO.getDecision()) ? "REJECTED" : "REVIEWED";
        for (egovframework.ops.appr.service.ApprLineVO ln : apprService.selectLineList("CHANGE", cabVO.getChgId())) {
            if ("REVIEW".equals(ln.getLineType()) && "PENDING".equals(ln.getStatus())) {
                egovframework.ops.appr.service.ApprLineVO up = new egovframework.ops.appr.service.ApprLineVO();
                up.setApprId(ln.getApprId());
                up.setStatus(reviewStatus);
                up.setOpinion("[CAB 심의] " + (cabVO.getOpinion() == null ? "" : cabVO.getOpinion()));
                apprService.actLine(up);
            }
        }
        return "redirect:/change/detail/" + cabVO.getChgId();
    }

    /** 이행후검토(PIR) 기록 */
    @PostMapping("/pir")
    public String pir(@ModelAttribute ChangeVO changeVO) {
        changeService.recordPir(changeVO);
        return "redirect:/change/detail/" + changeVO.getChgId();
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("CHANGE_TYPE"));
        model.addAttribute("statusList", codeService.selectCodeList("CHANGE_STATUS"));
    }
}

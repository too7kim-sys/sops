package egovframework.com.web;

import egovframework.ops.cmm.dashboard.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인/인증 화면 컨트롤러 (Presentation 계층).
 *
 * <p>로그인 페이지와 운영현황 대시보드(메인)를 제공한다.</p>
 */
@Controller
public class EgovMainController {

    private final DashboardService dashboardService;

    public EgovMainController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** 진입점 — 메인으로 이동 */
    @GetMapping("/")
    public String index() {
        return "redirect:/main";
    }

    /** 로그인 화면 */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** 접근권한 없음 안내 */
    @GetMapping("/denied")
    public String denied(Model model) {
        model.addAttribute("menu", "");
        return "error/denied";
    }

    /** 운영현황 대시보드 */
    @GetMapping("/main")
    public String main(Model model) {
        model.addAttribute("summary", dashboardService.selectSummary());
        model.addAttribute("incidentStat", dashboardService.selectIncidentStatusStat());
        model.addAttribute("changeStat", dashboardService.selectChangeStatusStat());
        model.addAttribute("recentIncidents", dashboardService.selectRecentIncidents());
        model.addAttribute("recentChanges", dashboardService.selectRecentChanges());
        model.addAttribute("menu", "dashboard");
        return "main";
    }
}

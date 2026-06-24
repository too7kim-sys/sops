package egovframework.ops.incident.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.incident.service.EgovIncidentService;
import egovframework.ops.incident.service.IncidentVO;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 장애관리 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 장애 접수/조회/처리/종결 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/incident")
public class EgovIncidentController {

    private final EgovIncidentService incidentService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;

    public EgovIncidentController(EgovIncidentService incidentService,
                                  EgovSystemService systemService,
                                  EgovCodeService codeService) {
        this.incidentService = incidentService;
        this.systemService = systemService;
        this.codeService = codeService;
    }

    /** 장애 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") IncidentVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = incidentService.selectIncidentListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("incidentList", incidentService.selectIncidentList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("INCIDENT_STATUS"));
        model.addAttribute("menu", "incident");
        return "incident/list";
    }

    /** 장애 상세 */
    @GetMapping("/detail/{incId}")
    public String detail(@PathVariable Long incId, Model model) {
        model.addAttribute("incident", incidentService.selectIncident(incId));
        model.addAttribute("statusList", codeService.selectCodeList("INCIDENT_STATUS"));
        model.addAttribute("menu", "incident");
        return "incident/detail";
    }

    /** 장애 접수(등록) 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("incident", new IncidentVO());
        addFormCodes(model);
        model.addAttribute("menu", "incident");
        return "incident/form";
    }

    /** 장애 접수 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute IncidentVO incidentVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        incidentVO.setRegId(loginUser.getUsername());
        incidentService.insertIncident(incidentVO);
        return "redirect:/incident/detail/" + incidentVO.getIncId();
    }

    /** 장애 기본정보 수정 폼 */
    @GetMapping("/edit/{incId}")
    public String editForm(@PathVariable Long incId, Model model) {
        model.addAttribute("incident", incidentService.selectIncident(incId));
        addFormCodes(model);
        model.addAttribute("menu", "incident");
        return "incident/form";
    }

    /** 장애 기본정보 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute IncidentVO incidentVO) {
        incidentService.updateIncident(incidentVO);
        return "redirect:/incident/detail/" + incidentVO.getIncId();
    }

    /** 장애 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute IncidentVO incidentVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (incidentVO.getChargerId() == null || incidentVO.getChargerId().isBlank()) {
            incidentVO.setChargerId(loginUser.getUsername());
        }
        incidentService.processIncident(incidentVO);
        return "redirect:/incident/detail/" + incidentVO.getIncId();
    }

    /** 장애 삭제 */
    @PostMapping("/delete/{incId}")
    public String delete(@PathVariable Long incId) {
        incidentService.deleteIncident(incId);
        return "redirect:/incident/list";
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("severityList", codeService.selectCodeList("INCIDENT_SEVERITY"));
        model.addAttribute("statusList", codeService.selectCodeList("INCIDENT_STATUS"));
    }
}

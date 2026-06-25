package egovframework.ops.event.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.event.service.EgovEventService;
import egovframework.ops.event.service.EventVO;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 운영상태관리(이벤트) 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 이벤트 감지/조회/처리/종결 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/event")
public class EgovEventController {

    private final EgovEventService eventService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;

    public EgovEventController(EgovEventService eventService,
                               EgovSystemService systemService,
                               EgovCodeService codeService) {
        this.eventService = eventService;
        this.systemService = systemService;
        this.codeService = codeService;
    }

    /** 이벤트 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") EventVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = eventService.selectEventListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("eventList", eventService.selectEventList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("EVENT_STATUS"));
        model.addAttribute("menu", "event");
        return "event/list";
    }

    /** 이벤트 상세 */
    @GetMapping("/detail/{evtId}")
    public String detail(@PathVariable Long evtId, Model model) {
        model.addAttribute("event", eventService.selectEvent(evtId));
        model.addAttribute("statusList", codeService.selectCodeList("EVENT_STATUS"));
        model.addAttribute("menu", "event");
        return "event/detail";
    }

    /** 이벤트 감지(등록) 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("event", new EventVO());
        addFormCodes(model);
        model.addAttribute("menu", "event");
        return "event/form";
    }

    /** 이벤트 감지 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute EventVO eventVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        if (eventVO.getChargerId() == null || eventVO.getChargerId().isBlank()) {
            eventVO.setChargerId(loginUser.getUsername());
        }
        eventService.insertEvent(eventVO);
        return "redirect:/event/detail/" + eventVO.getEvtId();
    }

    /** 이벤트 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute EventVO eventVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (eventVO.getChargerId() == null || eventVO.getChargerId().isBlank()) {
            eventVO.setChargerId(loginUser.getUsername());
        }
        eventService.processEvent(eventVO);
        return "redirect:/event/detail/" + eventVO.getEvtId();
    }

    /** 이벤트 삭제 */
    @PostMapping("/delete/{evtId}")
    public String delete(@PathVariable Long evtId) {
        eventService.deleteEvent(evtId);
        return "redirect:/event/list";
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("EVENT_TYPE"));
        model.addAttribute("severityList", codeService.selectCodeList("EVENT_SEVERITY"));
        model.addAttribute("statusList", codeService.selectCodeList("EVENT_STATUS"));
    }
}

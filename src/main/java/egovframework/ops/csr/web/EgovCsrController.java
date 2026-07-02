package egovframework.ops.csr.web;

import egovframework.com.cmm.PaginationInfo;
import egovframework.com.config.LoginUser;
import egovframework.ops.appr.service.EgovApprService;
import egovframework.ops.cmm.code.service.EgovCodeService;
import egovframework.ops.csr.service.EgovCsrService;
import egovframework.ops.csr.service.CsrFileVO;
import egovframework.ops.csr.service.CsrTplVO;
import egovframework.ops.csr.service.CsrVO;
import egovframework.ops.system.service.EgovSystemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 요청관리(CSR) 컨트롤러 (Presentation 계층).
 *
 * <p>응용프로그램 표준운영절차 — 요청 등록/조회/처리/종결 화면을 제공한다.</p>
 */
@Controller
@RequestMapping("/csr")
public class EgovCsrController {

    private final EgovCsrService csrService;
    private final EgovSystemService systemService;
    private final EgovCodeService codeService;
    private final EgovApprService apprService;
    private final egovframework.ops.csr.service.impl.CsrTransferService transferService;

    @Value("${ops.upload.dir:${java.io.tmpdir}/egov-sop/upload}")
    private String uploadDir;

    public EgovCsrController(EgovCsrService csrService,
                             EgovSystemService systemService,
                             EgovCodeService codeService,
                                EgovApprService apprService,
                             egovframework.ops.csr.service.impl.CsrTransferService transferService) {
        this.csrService = csrService;
        this.systemService = systemService;
        this.codeService = codeService;
        this.apprService = apprService;
        this.transferService = transferService;
    }

    /** 요청 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchVO") CsrVO searchVO, Model model) {
        searchVO.initPaging();
        int totalCnt = csrService.selectCsrListCnt(searchVO);

        PaginationInfo pageInfo = new PaginationInfo();
        pageInfo.setCurrentPageNo(searchVO.getPageIndex());
        pageInfo.setRecordCountPerPage(searchVO.getPageUnit());
        pageInfo.setPageSize(searchVO.getPageSize());
        pageInfo.setTotalRecordCount(totalCnt);

        model.addAttribute("csrList", csrService.selectCsrList(searchVO));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("statusList", codeService.selectCodeList("CSR_STATUS"));
        model.addAttribute("menu", "csr");
        return "csr/list";
    }

    /** 요청 상세 */
    @GetMapping("/detail/{csrId}")
    public String detail(@PathVariable Long csrId, Model model,
                         @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("csr", csrService.selectCsr(csrId));
        model.addAttribute("statusList", codeService.selectCodeList("CSR_STATUS"));
        model.addAttribute("fileList", csrService.selectCsrFileList(csrId));
        model.addAttribute("transferTargets", egovframework.ops.csr.service.impl.CsrTransferService.TARGETS);
        // 요청 처리는 처리자(결재선 HANDLE 담당자)만 가능 — 화면 노출 제어용
        model.addAttribute("canProcess", canProcess(csrId, loginUser));
        // 처리자는 요청서를 수정할 수 없음 — 수정 버튼 노출 제어용
        model.addAttribute("isHandler", isHandler(csrId, loginUser));
        model.addAttribute("menu", "csr");
        return "csr/detail";
    }

    /** 처리자 여부 — 결재선의 처리(HANDLE) 담당자 본인 */
    private boolean isHandler(Long csrId, LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        String uid = loginUser.getUsername();
        for (egovframework.ops.appr.service.ApprLineVO ln : apprService.selectLineList("CSR", csrId)) {
            if ("HANDLE".equals(ln.getLineType()) && uid.equals(ln.getAssigneeId())) {
                return true;
            }
        }
        return false;
    }

    /** 처리 가능 여부 — 처리자 본인, 대상시스템 운영담당자, 또는 운영관리자(대리처리) */
    private boolean canProcess(Long csrId, LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        return "ADMIN".equals(loginUser.getUser().getRole())
                || isHandler(csrId, loginUser)
                || apprService.isCsrSystemManager(csrId, loginUser.getUsername());
    }

    /** 요청 이관 — 대상 모듈로 신규 레코드 생성 후 연계, 대상 상세로 이동 */
    @PostMapping("/transfer")
    public String transfer(@RequestParam Long csrId,
                           @RequestParam String targetType,
                           @AuthenticationPrincipal LoginUser loginUser) {
        String detailUrl = transferService.transfer(csrId, targetType, loginUser.getUsername());
        return "redirect:" + detailUrl;
    }

    /* ============================ 첨부파일 ============================ */

    /** 첨부파일 업로드(다중) */
    @PostMapping("/file/upload")
    public String fileUpload(@RequestParam Long csrId,
                            @RequestParam("files") MultipartFile[] files,
                            @AuthenticationPrincipal LoginUser loginUser) throws IOException {
        storeFiles(csrId, files, loginUser.getUsername());
        return "redirect:/csr/detail/" + csrId;
    }

    /** 업로드된 멀티파트 파일들을 저장하고 메타데이터를 적재(등록/수정/상세 공용) */
    private void storeFiles(Long csrId, MultipartFile[] files, String actorId) throws IOException {
        if (csrId == null || files == null) {
            return;
        }
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        for (MultipartFile mf : files) {
            if (mf == null || mf.isEmpty()) {
                continue;
            }
            // 원본 파일명에서 경로 구분자만 제거(Paths.get 은 OS 파일명 charset 영향으로 한글 깨짐/오류 → 문자열 처리)
            String origin = mf.getOriginalFilename();
            if (origin != null) {
                int sep = Math.max(origin.lastIndexOf('/'), origin.lastIndexOf('\\'));
                origin = (sep >= 0) ? origin.substring(sep + 1) : origin;
            } else {
                origin = "unnamed";
            }
            String store = UUID.randomUUID().toString().replace("-", "");
            mf.transferTo(dir.resolve(store).toFile());

            CsrFileVO vo = new CsrFileVO();
            vo.setCsrId(csrId);
            vo.setOriginNm(origin);
            vo.setStoreNm(store);
            vo.setFileSize(mf.getSize());
            vo.setContentType(mf.getContentType());
            vo.setRegId(actorId);
            csrService.insertCsrFile(vo);
        }
    }

    /** 첨부파일 다운로드 */
    @GetMapping("/file/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long fileId) {
        CsrFileVO f = csrService.selectCsrFile(fileId);
        if (f == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(uploadDir).resolve(f.getStoreNm());
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String encoded = URLEncoder.encode(f.getOriginNm(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /** 첨부파일 삭제 (returnUrl 지정 시 해당 화면으로 복귀 — 수정폼 등) */
    @PostMapping("/file/delete/{fileId}")
    public String fileDelete(@PathVariable Long fileId,
                             @RequestParam(required = false) String returnUrl) {
        CsrFileVO f = csrService.selectCsrFile(fileId);
        Long csrId = (f != null) ? f.getCsrId() : null;
        if (f != null) {
            try {
                Files.deleteIfExists(Paths.get(uploadDir).resolve(f.getStoreNm()));
            } catch (IOException ignore) {
                // 물리 파일이 없어도 메타데이터는 삭제
            }
            csrService.deleteCsrFile(fileId);
        }
        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/csr/detail/" + (csrId != null ? csrId : "");
    }

    /** 요청 등록 폼 */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("csr", new CsrVO());
        addFormCodes(model);
        model.addAttribute("menu", "csr");
        return "csr/form";
    }

    /** 요청 등록 처리 (첨부파일 포함) */
    @PostMapping("/insert")
    public String insert(@ModelAttribute CsrVO csrVO,
                         @RequestParam(value = "files", required = false) MultipartFile[] files,
                         @AuthenticationPrincipal LoginUser loginUser) throws IOException {
        csrVO.setReqId(loginUser.getUsername());
        csrService.insertCsr(csrVO);
        // 결재 기본설정(템플릿) 자동 적용 — 결재/검토/공유/처리자 라인을 기본설정에 따라 생성
        apprService.applyTemplate("CSR", csrVO.getCsrId(), loginUser.getUsername());
        storeFiles(csrVO.getCsrId(), files, loginUser.getUsername());
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 기본정보 수정 폼 (처리자는 수정 불가) */
    @GetMapping("/edit/{csrId}")
    public String editForm(@PathVariable Long csrId, Model model,
                           @AuthenticationPrincipal LoginUser loginUser) {
        if (isHandler(csrId, loginUser)) {
            return "redirect:/csr/detail/" + csrId;
        }
        model.addAttribute("csr", csrService.selectCsr(csrId));
        model.addAttribute("fileList", csrService.selectCsrFileList(csrId));
        addFormCodes(model);
        model.addAttribute("menu", "csr");
        return "csr/form";
    }

    /** 요청 기본정보 수정 처리 (첨부파일 추가 포함) */
    @PostMapping("/update")
    public String update(@ModelAttribute CsrVO csrVO,
                         @RequestParam(value = "files", required = false) MultipartFile[] files,
                         @AuthenticationPrincipal LoginUser loginUser) throws IOException {
        // 처리자는 요청서 수정 불가
        if (isHandler(csrVO.getCsrId(), loginUser)) {
            return "redirect:/csr/detail/" + csrVO.getCsrId();
        }
        csrService.updateCsr(csrVO);
        storeFiles(csrVO.getCsrId(), files, loginUser.getUsername());
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 처리(상태전이) — 분류완료(CLASSIFIED) 처리 시 이관 대상이 지정되면 함께 이관 */
    @PostMapping("/process")
    public String process(@ModelAttribute CsrVO csrVO,
                          @RequestParam(required = false) String targetType,
                          @AuthenticationPrincipal LoginUser loginUser) {
        // 요청 처리는 처리자(결재선 HANDLE 담당자)·운영관리자만 허용
        if (!canProcess(csrVO.getCsrId(), loginUser)) {
            return "redirect:/csr/detail/" + csrVO.getCsrId();
        }
        if (csrVO.getChargerId() == null || csrVO.getChargerId().isBlank()) {
            csrVO.setChargerId(loginUser.getUsername());
        }
        csrService.processCsr(csrVO);
        // 분류완료로 처리하면서 이관 대상이 선택된 경우 대상 모듈로 이관 후 대상 상세로 이동
        if ("CLASSIFIED".equals(csrVO.getStatus()) && targetType != null && !targetType.isBlank()) {
            String detailUrl = transferService.transfer(csrVO.getCsrId(), targetType, loginUser.getUsername());
            return "redirect:" + detailUrl;
        }
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 삭제 */
    @PostMapping("/delete/{csrId}")
    public String delete(@PathVariable Long csrId) {
        csrService.deleteCsr(csrId);
        return "redirect:/csr/list";
    }

    private void addFormCodes(Model model) {
        model.addAttribute("systemList", systemService.selectSystemAll());
        model.addAttribute("typeList", codeService.selectCodeList("CSR_TYPE"));
        // 요청 소분류 — 대분류(upperCode)별로 폼에서 연동 필터링
        model.addAttribute("subTypeList", codeService.selectCodeList("CSR_SUBTYPE"));
        // 소분류별 요청내용 템플릿 — 폼에서 소분류 선택 시 요청내용에 자동 반영
        model.addAttribute("csrTplList", csrService.selectCsrTplActive());
        model.addAttribute("priorityList", codeService.selectCodeList("PRIORITY"));
        model.addAttribute("statusList", codeService.selectCodeList("CSR_STATUS"));
    }

    /* ============ 요청 소분류별 요청내용 템플릿 관리 (운영관리자) ============ */

    /** 템플릿 관리 목록 */
    @GetMapping("/tpl")
    public String tplList(Model model) {
        model.addAttribute("tplList", csrService.selectCsrTplList());
        model.addAttribute("menu", "csrTpl");
        return "csr/tpl_list";
    }

    /** 템플릿 편집 폼 */
    @GetMapping("/tpl/edit")
    public String tplEdit(@RequestParam String subType, Model model) {
        model.addAttribute("tpl", csrService.selectCsrTpl(subType));
        model.addAttribute("menu", "csrTpl");
        return "csr/tpl_form";
    }

    /** 템플릿 저장 */
    @PostMapping("/tpl/save")
    public String tplSave(@ModelAttribute CsrTplVO tplVO) {
        csrService.saveCsrTpl(tplVO);
        return "redirect:/csr/tpl";
    }
}

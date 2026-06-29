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

    @Value("${ops.upload.dir:${java.io.tmpdir}/egov-sop/upload}")
    private String uploadDir;

    public EgovCsrController(EgovCsrService csrService,
                             EgovSystemService systemService,
                             EgovCodeService codeService,
                                EgovApprService apprService) {
        this.csrService = csrService;
        this.systemService = systemService;
        this.codeService = codeService;
        this.apprService = apprService;
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
    public String detail(@PathVariable Long csrId, Model model) {
        model.addAttribute("csr", csrService.selectCsr(csrId));
        model.addAttribute("statusList", codeService.selectCodeList("CSR_STATUS"));
        model.addAttribute("fileList", csrService.selectCsrFileList(csrId));
        model.addAttribute("menu", "csr");
        return "csr/detail";
    }

    /* ============================ 첨부파일 ============================ */

    /** 첨부파일 업로드(다중) */
    @PostMapping("/file/upload")
    public String fileUpload(@RequestParam Long csrId,
                            @RequestParam("files") MultipartFile[] files,
                            @AuthenticationPrincipal LoginUser loginUser) throws IOException {
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
            vo.setRegId(loginUser.getUsername());
            csrService.insertCsrFile(vo);
        }
        return "redirect:/csr/detail/" + csrId;
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

    /** 첨부파일 삭제 */
    @PostMapping("/file/delete/{fileId}")
    public String fileDelete(@PathVariable Long fileId) {
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

    /** 요청 등록 처리 */
    @PostMapping("/insert")
    public String insert(@ModelAttribute CsrVO csrVO,
                         @AuthenticationPrincipal LoginUser loginUser) {
        csrVO.setReqId(loginUser.getUsername());
        csrService.insertCsr(csrVO);
        // 결재 기본설정(템플릿) 자동 적용 — 결재/검토/공유/처리자 라인을 기본설정에 따라 생성
        apprService.applyTemplate("CSR", csrVO.getCsrId(), loginUser.getUsername());
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 기본정보 수정 폼 */
    @GetMapping("/edit/{csrId}")
    public String editForm(@PathVariable Long csrId, Model model) {
        model.addAttribute("csr", csrService.selectCsr(csrId));
        addFormCodes(model);
        model.addAttribute("menu", "csr");
        return "csr/form";
    }

    /** 요청 기본정보 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute CsrVO csrVO) {
        csrService.updateCsr(csrVO);
        return "redirect:/csr/detail/" + csrVO.getCsrId();
    }

    /** 요청 처리(상태전이) */
    @PostMapping("/process")
    public String process(@ModelAttribute CsrVO csrVO,
                          @AuthenticationPrincipal LoginUser loginUser) {
        if (csrVO.getChargerId() == null || csrVO.getChargerId().isBlank()) {
            csrVO.setChargerId(loginUser.getUsername());
        }
        csrService.processCsr(csrVO);
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

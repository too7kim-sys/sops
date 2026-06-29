<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="요청 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${csr.csrId == null}"/>

<div class="breadcrumb">표준운영절차 &gt; 요청관리 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '요청 등록' : '요청 정보 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/csr/insert' : '/csr/update'}" enctype="multipart/form-data">
    <input type="hidden" name="csrId" value="${csr.csrId}"/>
    <div class="panel">
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <div class="sysfind" id="sysFind">
                        <div class="sysfind-box" id="sysFindBox">
                            <span class="sysfind-tags" id="sysTags"></span>
                            <input type="text" id="sysSearch" class="sysfind-search" placeholder="시스템명 검색 후 선택…" autocomplete="off"/>
                        </div>
                        <div class="sysfind-results" id="sysResults" style="display:none;"></div>
                        <%-- 실제 제출값(체크박스) : 검색 UI 가 토글, 화면에는 숨김 --%>
                        <span class="sysfind-data" id="sysIdsBox" style="display:none;">
                            <c:forEach var="s" items="${systemList}">
                                <label data-nm="${fn:escapeXml(s.sysNm)}"><input type="checkbox" name="sysIds" value="${s.sysId}" ${csr.sysIds.contains(s.sysId) ? 'checked' : ''}/></label>
                            </c:forEach>
                        </span>
                    </div>
                    <div class="h-meta">검색어를 입력해 시스템을 찾아 선택하세요. 선택 항목은 위에 표시됩니다. (1개 이상)</div>
                </td>
                <th>요청 대분류 <span class="required">*</span></th>
                <td>
                    <select name="csrType" id="csrType" required>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == csr.csrType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>요청 소분류 <span class="required">*</span></th>
                <td colspan="3">
                    <select name="csrSubType" id="csrSubType" required style="min-width:240px;">
                        <%-- 대분류 선택에 따라 스크립트로 채워짐 --%>
                    </select>
                </td>
            </tr>
            <tr>
                <th>우선순위 <span class="required">*</span></th>
                <td>
                    <select name="priority" required>
                        <c:forEach var="cd" items="${priorityList}">
                            <option value="${cd.codeId}" ${cd.codeId == csr.priority ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>완료요구일</th>
                <td><input type="date" name="dueDt" value="${csr.dueDt}"/></td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${csr.title}" required/></td>
            </tr>
            <tr>
                <th>요청 내용</th>
                <td colspan="3"><textarea class="wysiwyg" id="content" name="content" rows="5">${csr.content}</textarea></td>
            </tr>
            <tr>
                <th>첨부파일</th>
                <td colspan="3">
                    <c:if test="${not empty fileList}">
                        <div style="margin-bottom:8px;">
                            <c:forEach var="f" items="${fileList}">
                                <span class="sys-chip" style="margin:2px 6px 2px 0;">
                                    <a href="${ctx}/csr/file/download/${f.fileId}">📎 ${f.originNm}</a>
                                    <button type="button" class="sys-chip-x" title="삭제" onclick="delCsrFile(${f.fileId})">×</button>
                                </span>
                            </c:forEach>
                        </div>
                    </c:if>
                    <input type="file" name="files" multiple/>
                    <div class="h-meta">여러 파일 선택 가능 · 파일당 최대 50MB · 저장 시 함께 첨부됩니다.</div>
                </td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/csr/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>

<%-- 소분류별 요청내용 템플릿 원본(HTML) : textarea 에 담아 브라우저가 엔티티를 복원하면 .value 로 원본 HTML 획득 --%>
<div style="display:none;">
    <c:forEach var="t" items="${csrTplList}">
        <textarea class="csr-tpl-data" data-sub="${t.subType}">${fn:escapeXml(t.content)}</textarea>
    </c:forEach>
</div>

<%-- 요청 대분류 → 소분류 연동 + 소분류별 요청내용 템플릿 자동주입 (에디터 초기화 이후 실행) --%>
<script>
(function () {
    var SUBTYPES = [
        <c:forEach var="st" items="${subTypeList}" varStatus="vs">{id:'${st.codeId}', nm:'${fn:escapeXml(st.codeNm)}', up:'${st.upperCode}'}<c:if test="${!vs.last}">,</c:if></c:forEach>
    ];
    var TEMPLATES = {};
    Array.prototype.forEach.call(document.querySelectorAll('.csr-tpl-data'), function (t) {
        TEMPLATES[t.getAttribute('data-sub')] = t.value;
    });
    var curSel = '${csr.csrSubType}';
    var isNew = ${csr.csrId == null};
    var major = document.getElementById('csrType');
    var sub = document.getElementById('csrSubType');
    if (!major || !sub) return;

    function fill() {
        var maj = major.value;
        sub.innerHTML = '';
        SUBTYPES.filter(function (s) { return s.up === maj; }).forEach(function (s) {
            var o = document.createElement('option');
            o.value = s.id; o.textContent = s.nm;
            if (s.id === curSel) o.selected = true;
            sub.appendChild(o);
        });
        if (!sub.options.length) {
            var o = document.createElement('option');
            o.value = ''; o.textContent = '(소분류 없음)';
            sub.appendChild(o);
        }
    }

    // 선택 소분류의 요청내용 템플릿을 에디터에 즉시 주입 (확인 없이 대체)
    function applyTpl(subCd) {
        var tpl = TEMPLATES[subCd];
        if (!tpl || !window.jQuery) return;
        var $c = jQuery('#content');
        if (!$c.length) return;
        if ($c.data('summernote')) { $c.summernote('code', tpl); } else { $c.val(tpl); }
    }

    major.addEventListener('change', function () { curSel = ''; fill(); applyTpl(sub.value); });
    sub.addEventListener('change', function () { applyTpl(sub.value); });
    fill();
    // 신규 등록 시 최초 진입한 소분류 템플릿을 자동 표시(에디터 준비 후)
    if (isNew && window.jQuery) { jQuery(function () { applyTpl(sub.value); }); }

    // 대상 시스템(체크박스) — 1개 이상 선택 검증
    var box = document.getElementById('sysIdsBox');
    var form = box ? box.closest('form') : null;
    if (form) {
        form.addEventListener('submit', function (e) {
            if (!box.querySelector('input[name=sysIds]:checked')) {
                e.preventDefault();
                alert('대상 시스템을 1개 이상 선택하세요.');
            }
        });
    }
})();
</script>

<%-- 대상 시스템 통합검색 선택기 : 검색 입력 → 결과 드롭다운 선택 → 토큰(칩) (다수 대응) --%>
<script>
(function () {
    var dataBox = document.getElementById('sysIdsBox');
    if (!dataBox) return;
    var tagsEl  = document.getElementById('sysTags');
    var input   = document.getElementById('sysSearch');
    var results = document.getElementById('sysResults');
    var MAX = 30;

    var items = Array.prototype.slice.call(dataBox.querySelectorAll('label')).map(function (l) {
        var cb = l.querySelector('input[type=checkbox]');
        return { id: cb.value, nm: (l.getAttribute('data-nm') || '').trim(), cb: cb };
    });

    function renderTags() {
        tagsEl.innerHTML = '';
        items.filter(function (it) { return it.cb.checked; }).forEach(function (it) {
            var chip = document.createElement('span');
            chip.className = 'sys-chip';
            chip.appendChild(document.createTextNode(it.nm));
            var x = document.createElement('button');
            x.type = 'button'; x.className = 'sys-chip-x'; x.textContent = '×'; x.title = '제거';
            x.addEventListener('mousedown', function (e) { e.preventDefault(); it.cb.checked = false; renderTags(); });
            chip.appendChild(x);
            tagsEl.appendChild(chip);
        });
    }

    function openResults() {
        var q = (input.value || '').toLowerCase().trim();
        var matches = items.filter(function (it) {
            return !it.cb.checked && (!q || it.nm.toLowerCase().indexOf(q) >= 0);
        });
        results.innerHTML = '';
        if (!matches.length) {
            var none = document.createElement('div');
            none.className = 'sysfind-none';
            none.textContent = q ? '검색 결과가 없습니다.' : '추가할 시스템이 없습니다.';
            results.appendChild(none);
        } else {
            matches.slice(0, MAX).forEach(function (it) {
                var d = document.createElement('div');
                d.className = 'sysfind-item';
                d.textContent = it.nm;
                d.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    it.cb.checked = true; input.value = '';
                    renderTags(); openResults(); input.focus();
                });
                results.appendChild(d);
            });
            if (matches.length > MAX) {
                var more = document.createElement('div');
                more.className = 'sysfind-none';
                more.textContent = '… 외 ' + (matches.length - MAX) + '건 · 검색어를 더 입력하세요';
                results.appendChild(more);
            }
        }
        results.style.display = '';
    }
    function closeResults() { results.style.display = 'none'; }

    input.addEventListener('focus', openResults);
    input.addEventListener('input', openResults);
    input.addEventListener('blur', function () { setTimeout(closeResults, 150); });
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            var first = results.querySelector('.sysfind-item');
            if (first) { first.dispatchEvent(new MouseEvent('mousedown')); }
        } else if (e.key === 'Backspace' && !input.value) {
            var checked = items.filter(function (it) { return it.cb.checked; });
            if (checked.length) { checked[checked.length - 1].cb.checked = false; renderTags(); openResults(); }
        }
    });
    // 입력창 영역 클릭 시 검색창 포커스
    document.getElementById('sysFindBox').addEventListener('click', function () { input.focus(); });

    renderTags();
})();
</script>

<%-- 기존 첨부파일 삭제 : 수정폼으로 복귀(returnUrl) --%>
<c:if test="${not isNew}">
<script>
function delCsrFile(fileId) {
    if (!confirm('이 첨부파일을 삭제할까요?')) { return; }
    var f = document.createElement('form');
    f.method = 'post';
    f.action = '${ctx}/csr/file/delete/' + fileId;
    f.innerHTML = '<input type="hidden" name="returnUrl" value="${ctx}/csr/edit/${csr.csrId}"/>';
    document.body.appendChild(f);
    f.submit();
}
</script>
</c:if>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

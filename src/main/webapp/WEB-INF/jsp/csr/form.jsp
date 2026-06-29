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

<form method="post" action="${ctx}${isNew ? '/csr/insert' : '/csr/update'}">
    <input type="hidden" name="csrId" value="${csr.csrId}"/>
    <div class="panel">
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <div class="chk-layer" id="sysIdsBox">
                        <c:forEach var="s" items="${systemList}">
                            <label><input type="checkbox" name="sysIds" value="${s.sysId}" ${csr.sysIds.contains(s.sysId) ? 'checked' : ''}/> ${s.sysNm}</label>
                        </c:forEach>
                    </div>
                    <div class="h-meta">필요한 대상 시스템을 모두 체크하세요. (1개 이상)</div>
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
                <th>담당자</th>
                <td colspan="3"><input type="text" name="chargerId" value="${csr.chargerId}" placeholder="처리 담당자 ID"/></td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${csr.title}" required/></td>
            </tr>
            <tr>
                <th>요청 내용</th>
                <td colspan="3"><textarea class="wysiwyg" id="content" name="content" rows="5">${csr.content}</textarea></td>
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

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

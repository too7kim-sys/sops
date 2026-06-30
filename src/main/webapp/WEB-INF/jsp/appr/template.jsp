<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="결재 기본설정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 결재선/공유 기본설정</div>
<div class="page-head">
    <div>
        <div class="page-title">결재선/공유 기본설정</div>
        <div class="page-desc">관리(업무)별 공통 기본 결재선·공유를 정의합니다. 대상은 사용자/부서/요청자/전체로 지정합니다.</div>
    </div>
</div>

<div class="panel">
    <form method="get" action="${ctx}/appr/template" class="searchbar">
        <label>업무 구분
            <select name="bizType" onchange="this.form.submit()">
                <c:forEach var="bt" items="${bizTypes}">
                    <option value="${bt.key}" ${bt.key == bizType ? 'selected' : ''}>${bt.value}</option>
                </c:forEach>
            </select>
        </label>
        <span style="color:#777;font-size:13px;">현재: <b>${bizTypeNm}</b></span>
    </form>
</div>

<div class="panel">
    <h3>기본 결재선 / 공유 목록 — ${bizTypeNm}</h3>
    <table class="list">
        <thead>
        <tr>
            <th style="width:80px;">종류</th>
            <th style="width:90px;">라인유형</th>
            <th class="center" style="width:60px;">단계</th>
            <th style="width:90px;">대상유형</th>
            <th>대상값</th>
            <th>메모</th>
            <th style="width:60px;"></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty templateList}"><tr><td colspan="7" class="empty">정의된 기본설정이 없습니다.</td></tr></c:if>
        <c:forEach var="t" items="${templateList}">
            <tr>
                <td><span class="badge ${t.kind == 'SHARE' ? 'lt-handle' : 'lt-review'}">${t.kind == 'SHARE' ? '공유' : '결재선'}</span></td>
                <td>${empty t.lineTypeNm ? '-' : t.lineTypeNm}</td>
                <td class="center">${t.stepNo}</td>
                <td>${t.targetTypeNm}</td>
                <td>
                    <c:choose>
                        <c:when test="${t.targetType == 'DEPT'}">${empty t.targetValueNm ? t.targetValue : t.targetValueNm}</c:when>
                        <c:otherwise>${empty t.targetValue ? '-' : t.targetValue}</c:otherwise>
                    </c:choose>
                </td>
                <td>${empty t.memo ? '-' : t.memo}</td>
                <td>
                    <form method="post" action="${ctx}/appr/template/delete" onsubmit="return confirm('삭제할까요?');">
                        <input type="hidden" name="tplId" value="${t.tplId}"/>
                        <input type="hidden" name="bizType" value="${bizType}"/>
                        <button type="submit" class="btn btn-danger btn-sm">×</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div class="panel">
    <h3>기본설정 추가</h3>
    <form method="post" action="${ctx}/appr/template/add" class="appr-add" data-target-form id="tplForm">
        <input type="hidden" name="bizType" value="${bizType}"/>
        <div class="appr-add-row">
            <label>종류
                <select name="kind" class="tpl-kind">
                    <option value="LINE">결재선</option>
                    <option value="SHARE">공유</option>
                </select>
            </label>
            <label class="tpl-linetype">라인유형
                <select name="lineType">
                    <c:forEach var="lty" items="${lineTypeList}">
                        <option value="${lty.codeId}">${lty.codeNm}</option>
                    </c:forEach>
                </select>
            </label>
            <label>단계
                <input type="number" name="stepNo" value="1" min="1" style="width:60px;"/>
            </label>
            <label>대상 유형
                <select name="targetType" class="target-type">
                    <c:forEach var="tt" items="${targetTypeList}">
                        <option value="${tt.codeId}">${tt.codeNm}</option>
                    </c:forEach>
                </select>
            </label>
            <label class="tgt-user">사용자
                <select name="targetValue">
                    <c:forEach var="u" items="${candidates}">
                        <option value="${u.userId}">${u.userNm} (${u.userId})</option>
                    </c:forEach>
                </select>
            </label>
            <label class="tgt-dept" style="display:none;">부서
                <span class="dept-search" style="max-width:240px;">
                    <input type="text" id="tplDept_nm" class="dept-search-disp" data-prefix="tplDept"
                           placeholder="부서 검색" autocomplete="off" readonly onclick="openDeptPopup('tplDept')"/>
                    <input type="hidden" name="targetValue" id="tplDept_val" data-dept-bind="cd"/>
                    <button type="button" class="dept-search-btn" onclick="openDeptPopup('tplDept')"><svg viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="7" cy="7" r="4.5"></circle><line x1="11" y1="11" x2="14.5" y2="14.5"></line></svg>검색</button>
                </span>
            </label>
            <label style="flex:1;">메모(공유)
                <input type="text" name="memo" placeholder="공유 메모(선택)" style="width:100%;"/>
            </label>
            <button type="submit" class="btn btn-primary btn-sm">추가</button>
        </div>
        <div class="h-meta">※ 대상 유형이 <b>부서</b>면 해당 부서원 전체, <b>요청자</b>면 해당 건 요청자, <b>전체</b>면 모든 운영자로 적용 시 전개됩니다.</div>
    </form>
</div>

<script>
(function () {
    var f = document.getElementById('tplForm');
    if (!f) return;
    function sync() {
        var tt = f.querySelector('.target-type').value;
        var kind = f.querySelector('.tpl-kind').value;
        var u = f.querySelector('.tgt-user'), d = f.querySelector('.tgt-dept'), lt = f.querySelector('.tpl-linetype');
        u.style.display = (tt === 'USER') ? '' : 'none';
        u.querySelector('select').disabled = (tt !== 'USER');
        d.style.display = (tt === 'DEPT') ? '' : 'none';
        var dv = d.querySelector('[name=targetValue]'); if (dv) dv.disabled = (tt !== 'DEPT');
        lt.style.display = (kind === 'LINE') ? '' : 'none';
        lt.querySelector('select').disabled = (kind !== 'LINE');
    }
    f.querySelector('.target-type').addEventListener('change', sync);
    f.querySelector('.tpl-kind').addEventListener('change', sync);
    sync();
})();
</script>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

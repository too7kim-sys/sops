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
    <div class="h-meta" style="margin-bottom:8px;">※ 같은 <b>단계</b>의 결재선은 <b>병렬</b>, 단계는 순차 진행됩니다. 대상 <b>시스템·요청 분류</b>로 스코프를 지정하면, 적용 시 <b>가장 구체적인 설정</b>(시스템+분류 &gt; 시스템 &gt; 분류 &gt; 전체)이 선택됩니다. 라인유형·단계·메모는 행에서 바로 수정할 수 있습니다.</div>
    <%-- 행별 수정/삭제 폼은 표 밖에 선언하고 input 은 form= 으로 연결 --%>
    <c:forEach var="t" items="${templateList}">
        <form id="tplU_${t.tplId}" method="post" action="${ctx}/appr/template/update">
            <input type="hidden" name="bizType" value="${bizType}"/>
            <input type="hidden" name="tplId" value="${t.tplId}"/>
            <input type="hidden" name="kind" value="${t.kind}"/>
        </form>
        <form id="tplD_${t.tplId}" method="post" action="${ctx}/appr/template/delete" onsubmit="return confirm('삭제할까요?');">
            <input type="hidden" name="tplId" value="${t.tplId}"/>
            <input type="hidden" name="bizType" value="${bizType}"/>
        </form>
    </c:forEach>
    <table class="list">
        <thead>
        <tr>
            <th style="width:84px;">종류</th>
            <th style="width:130px;">대상 시스템</th>
            <th style="width:110px;">요청 분류</th>
            <th style="width:100px;">라인유형</th>
            <th class="center" style="width:56px;">단계</th>
            <th style="width:74px;">대상유형</th>
            <th>대상값</th>
            <th>메모</th>
            <th class="center" style="width:110px;">관리</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty templateList}"><tr><td colspan="9" class="empty">정의된 기본설정이 없습니다.</td></tr></c:if>
        <c:forEach var="t" items="${templateList}">
            <tr>
                <td><span class="badge ${t.kind == 'SHARE' ? 'lt-handle' : 'lt-review'}">${t.kind == 'SHARE' ? '공유' : '결재선'}</span></td>
                <td>${empty t.sysId ? '전체' : (empty t.sysNm ? t.sysId : t.sysNm)}</td>
                <td>${empty t.classCd ? '전체' : (empty t.classNm ? t.classCd : t.classNm)}</td>
                <td>
                    <c:choose>
                        <c:when test="${t.kind == 'SHARE'}">-</c:when>
                        <c:otherwise>
                            <select name="lineType" form="tplU_${t.tplId}" style="width:100%;">
                                <c:forEach var="lty" items="${lineTypeList}">
                                    <option value="${lty.codeId}" ${lty.codeId == t.lineType ? 'selected' : ''}>${lty.codeNm}</option>
                                </c:forEach>
                            </select>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="center"><input type="number" name="stepNo" value="${t.stepNo}" min="1" style="width:52px;text-align:center;" form="tplU_${t.tplId}"/></td>
                <td>${t.targetTypeNm}</td>
                <td>
                    <c:choose>
                        <c:when test="${t.targetType == 'DEPT'}">${empty t.targetValueNm ? t.targetValue : t.targetValueNm}</c:when>
                        <c:otherwise>${empty t.targetValue ? '-' : t.targetValue}</c:otherwise>
                    </c:choose>
                </td>
                <td><input type="text" name="memo" value="${t.memo}" style="width:100%;" form="tplU_${t.tplId}"/></td>
                <td class="center" style="white-space:nowrap;">
                    <button type="submit" class="btn btn-primary btn-sm" form="tplU_${t.tplId}">저장</button>
                    <button type="submit" class="btn btn-danger btn-sm" form="tplD_${t.tplId}">삭제</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="h-meta" style="margin-top:6px;">※ 대상(유형/값) 변경은 행 삭제 후 아래에서 다시 추가하세요.</div>
</div>

<div class="panel">
    <h3>기본설정 추가</h3>
    <form method="post" action="${ctx}/appr/template/add" class="appr-add" data-target-form id="tplForm">
        <input type="hidden" name="bizType" value="${bizType}"/>
        <div class="appr-add-row">
            <label>대상 시스템
                <select name="sysId">
                    <option value="">전체</option>
                    <c:forEach var="s" items="${systemList}">
                        <option value="${s.sysId}">${s.sysNm}</option>
                    </c:forEach>
                </select>
            </label>
            <label>요청 분류
                <select name="classCd" ${empty classList ? 'disabled' : ''}>
                    <option value="">전체</option>
                    <c:forEach var="cc" items="${classList}">
                        <option value="${cc.codeId}">${cc.codeNm}</option>
                    </c:forEach>
                </select>
            </label>
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
                <span class="dept-search" style="max-width:240px;">
                    <input type="text" id="tplUser_nm" class="dept-search-disp" data-prefix="tplUser"
                           placeholder="사용자 검색" autocomplete="off" readonly onclick="openUserPopup('tplUser')"/>
                    <input type="hidden" name="targetValue" id="tplUser_val"/>
                    <button type="button" class="dept-search-btn" onclick="openUserPopup('tplUser')"><svg viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="7" cy="7" r="4.5"></circle><line x1="11" y1="11" x2="14.5" y2="14.5"></line></svg>검색</button>
                </span>
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
        var uv = u.querySelector('[name=targetValue]'); if (uv) uv.disabled = (tt !== 'USER');
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

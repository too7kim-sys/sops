<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="문제 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 문제관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">문제 상세 <span style="color:#888;font-weight:400;">(PRB-${problem.prbId})</span></div>
    <div class="toolbar">
        <form action="${ctx}/problem/delete/${problem.prbId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>진행 단계 <span class="badge st-${fn:toLowerCase(problem.status)}" style="margin-left:6px;">${problem.statusNm}</span></h3>
    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
        <jsp:param name="type" value="PROBLEM"/>
        <jsp:param name="status" value="${problem.status}"/>
        <jsp:param name="statusNm" value="${problem.statusNm}"/>
        <jsp:param name="mode" value="full"/>
    </jsp:include>
</div>

<div class="grid-2">
    <div class="panel">
        <h3>문제 정보</h3>
        <table class="form">
            <tr><th>대상 시스템</th><td>${problem.sysNm}</td></tr>
            <tr><th>제목</th><td>${problem.title}</td></tr>
            <tr><th>우선순위 / 상태</th><td>
                <span class="badge lv-${fn:toLowerCase(problem.priority)}">${problem.priorityNm}</span>
                &nbsp;
                <span class="badge st-${fn:toLowerCase(problem.status)}">${problem.statusNm}</span>
            </td></tr>
            <tr><th>담당자</th><td>${empty problem.chargerId ? '-' : problem.chargerId}</td></tr>
            <tr><th>등록 / 해결</th><td>${problem.regDt} / ${empty problem.resolveDt ? '-' : problem.resolveDt}</td></tr>
            <tr><th>문제 내용</th><td><div class="rte-view">${problem.content}</div></td></tr>
            <tr><th>근본 원인</th><td><div class="rte-view">${empty problem.rootCause ? '-' : problem.rootCause}</div></td></tr>
            <tr><th>해결책</th><td><div class="rte-view">${empty problem.solution ? '-' : problem.solution}</div></td></tr>
        </table>
    </div>

    <div>
        <c:if test="${problem.status != 'CLOSED'}">
        <div class="panel">
            <h3>문제 처리</h3>
            <form method="post" action="${ctx}/problem/process">
                <input type="hidden" name="prbId" value="${problem.prbId}"/>
                <table class="form">
                    <tr><th>처리 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <c:forEach var="cd" items="${statusList}">
                                    <option value="${cd.codeId}" ${cd.codeId == problem.status ? 'selected' : ''}>${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>담당자</th><td><input type="text" name="chargerId" value="${problem.chargerId}"/></td></tr>
                    <tr><th>근본 원인</th><td><textarea class="wysiwyg" name="rootCause" rows="3">${problem.rootCause}</textarea></td></tr>
                    <tr><th>해결책</th><td><textarea class="wysiwyg" name="solution" rows="3">${problem.solution}</textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;">
                    <button type="submit" class="btn btn-success">처리 등록</button>
                </div>
            </form>
        </div>
        </c:if>

        <div class="panel">
            <h3>처리 이력</h3>
            <ul class="history">
                <c:forEach var="h" items="${problem.historyList}">
                    <li>
                        <span class="badge st-${fn:toLowerCase(h.status)}">${h.statusNm}</span>
                        <span style="white-space:pre-line;">${h.content}</span>
                        <div class="h-meta">${h.procId} · ${h.procDt}</div>
                    </li>
                </c:forEach>
                <c:if test="${empty problem.historyList}"><li style="border:none;">이력이 없습니다.</li></c:if>
            </ul>
        </div>
    </div>
</div>

<div class="panel">
    <h3>연계 장애</h3>
    <table class="list">
        <thead>
        <tr>
            <th class="center" style="width:90px;">장애ID</th>
            <th>제목</th>
            <th class="center" style="width:120px;">상태</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="inc" items="${problem.incList}">
            <tr>
                <td class="center"><a href="${ctx}/incident/detail/${inc.incId}">INC-${inc.incId}</a></td>
                <td>${inc.title}</td>
                <td class="center">${inc.statusNm}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty problem.incList}"><tr><td colspan="3" class="empty">연계된 장애가 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <form method="post" action="${ctx}/problem/linkInc" class="searchbar" style="margin-top:12px;">
        <input type="hidden" name="prbId" value="${problem.prbId}"/>
        <input type="number" name="incId" placeholder="연계할 장애 ID" required/>
        <button type="submit" class="btn btn-default">연계 추가</button>
    </form>
</div>

<div class="panel">
    <h3>알려진 오류 (KEDB)</h3>
    <table class="list">
        <thead>
        <tr>
            <th style="width:200px;">제목</th>
            <th>증상</th>
            <th>원인</th>
            <th>임시조치</th>
            <th>해결책</th>
            <th class="center" style="width:130px;">등록일시</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="k" items="${problem.kedbList}">
            <tr>
                <td>${k.title}</td>
                <td style="white-space:pre-line;">${empty k.symptom ? '-' : k.symptom}</td>
                <td style="white-space:pre-line;">${empty k.cause ? '-' : k.cause}</td>
                <td style="white-space:pre-line;">${empty k.workaround ? '-' : k.workaround}</td>
                <td style="white-space:pre-line;">${empty k.solution ? '-' : k.solution}</td>
                <td class="center">${k.regDt}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty problem.kedbList}"><tr><td colspan="6" class="empty">등록된 알려진 오류가 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <form method="post" action="${ctx}/problem/kedb" style="margin-top:12px;">
        <input type="hidden" name="prbId" value="${problem.prbId}"/>
        <table class="form">
            <tr><th>제목 <span class="required">*</span></th><td colspan="3"><input type="text" name="title" required/></td></tr>
            <tr><th>증상</th><td><textarea name="symptom" rows="2"></textarea></td>
                <th>원인</th><td><textarea name="cause" rows="2"></textarea></td></tr>
            <tr><th>임시조치</th><td><textarea name="workaround" rows="2"></textarea></td>
                <th>해결책</th><td><textarea name="solution" rows="2"></textarea></td></tr>
        </table>
        <div class="right" style="margin-top:12px;">
            <button type="submit" class="btn btn-default">KEDB 추가</button>
        </div>
    </form>
</div>

<div class="toolbar"><a href="${ctx}/problem/list" class="btn btn-default">＜ 목록</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="PROBLEM"/>
    <c:param name="bizId" value="${problem.prbId}"/>
    <c:param name="returnUrl" value="/problem/detail/${problem.prbId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

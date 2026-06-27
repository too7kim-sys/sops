<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="장애 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 장애관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">장애 상세 <span style="color:#888;font-weight:400;">(INC-${incident.incId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/incident/edit/${incident.incId}" class="btn btn-default">수정</a>
        <form action="${ctx}/incident/delete/${incident.incId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>진행 단계 <span class="badge st-${fn:toLowerCase(incident.status)}" style="margin-left:6px;">${incident.statusNm}</span></h3>
    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
        <jsp:param name="type" value="INCIDENT"/>
        <jsp:param name="status" value="${incident.status}"/>
        <jsp:param name="statusNm" value="${incident.statusNm}"/>
        <jsp:param name="mode" value="full"/>
    </jsp:include>
</div>

<div class="detail-stack">
    <div class="panel">
        <h3>장애 정보</h3>
        <table class="form">
            <tr><th>대상 시스템</th><td>${incident.sysNm}</td></tr>
            <tr><th>제목</th><td>${incident.title}</td></tr>
            <tr><th>등급 / 상태</th><td>
                <span class="badge sev-${incident.severity}">${incident.severity}등급</span>&nbsp;
                <span class="badge st-${fn:toLowerCase(incident.status)}">${incident.statusNm}</span>
            </td></tr>
            <tr><th>발생 / 접수</th><td>${incident.occrDt} / ${incident.rcptDt}</td></tr>
            <tr><th>목표복구일시</th><td>${empty incident.targetResolveDt ? '-' : incident.targetResolveDt}</td></tr>
            <c:if test="${incident.slaStatus != null}">
            <tr><th>SLA</th><td>
                <span class="badge ${incident.slaStatus == '준수' ? 'sla-ok' : (incident.slaStatus == '진행중' ? 'sla-warn' : 'sla-viol')}">${incident.slaStatus}</span>
            </td></tr>
            </c:if>
            <tr><th>조치완료</th><td>${empty incident.resolveDt ? '-' : incident.resolveDt}</td></tr>
            <tr><th>연계 문제</th><td><c:choose><c:when test="${incident.refPrbId != null}">PRB-${incident.refPrbId}</c:when><c:otherwise>-</c:otherwise></c:choose></td></tr>
            <tr><th>담당자</th><td>${empty incident.chargerId ? '-' : incident.chargerId}</td></tr>
            <tr><th>장애 내용</th><td><div class="rte-view">${incident.content}</div></td></tr>
            <tr><th>장애 원인</th><td><div class="rte-view">${empty incident.cause ? '-' : incident.cause}</div></td></tr>
            <tr><th>조치 내용</th><td><div class="rte-view">${empty incident.action ? '-' : incident.action}</div></td></tr>
        </table>
    </div>

    <div>
        <c:if test="${incident.status != 'CLOSED'}">
        <div class="panel">
            <h3>장애 처리</h3>
            <form method="post" action="${ctx}/incident/process">
                <input type="hidden" name="incId" value="${incident.incId}"/>
                <table class="form">
                    <tr><th>처리 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <c:forEach var="cd" items="${statusList}">
                                    <option value="${cd.codeId}" ${cd.codeId == incident.status ? 'selected' : ''}>${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>담당자</th><td><input type="text" name="chargerId" value="${incident.chargerId}"/></td></tr>
                    <tr><th>연계 문제</th><td><input type="number" name="refPrbId" value="${incident.refPrbId}" placeholder="연계 문제 ID (선택)"/></td></tr>
                    <tr><th>장애 원인</th><td><textarea class="wysiwyg" name="cause" rows="3">${incident.cause}</textarea></td></tr>
                    <tr><th>조치 내용</th><td><textarea class="wysiwyg" name="action" rows="3">${incident.action}</textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;"><button type="submit" class="btn btn-success">처리 등록</button></div>
            </form>
        </div>
        </c:if>

        <div class="panel">
            <h3>처리 이력</h3>
            <ul class="history">
                <c:forEach var="h" items="${incident.historyList}">
                    <li>
                        <span class="badge st-${fn:toLowerCase(h.status)}">${h.statusNm}</span>
                        <span style="white-space:pre-line;">${h.content}</span>
                        <div class="h-meta">${h.procId} · ${h.procDt}</div>
                    </li>
                </c:forEach>
                <c:if test="${empty incident.historyList}"><li style="border:none;">이력이 없습니다.</li></c:if>
            </ul>
        </div>

        <div class="panel">
            <h3>에스컬레이션</h3>
            <ul class="history">
                <c:forEach var="e" items="${incident.escalList}">
                    <li>
                        <span class="badge">${empty e.escalLevelNm ? e.escalLevel : e.escalLevelNm}</span>
                        <c:if test="${not empty e.escalTo}">→ ${e.escalTo}</c:if>
                        <span style="white-space:pre-line;">${e.reason}</span>
                        <div class="h-meta">${e.escalBy} · ${e.escalDt}</div>
                    </li>
                </c:forEach>
                <c:if test="${empty incident.escalList}"><li style="border:none;">에스컬레이션 이력이 없습니다.</li></c:if>
            </ul>
            <form method="post" action="${ctx}/incident/escalate" style="margin-top:12px;">
                <input type="hidden" name="incId" value="${incident.incId}"/>
                <table class="form">
                    <tr><th>단계 <span class="required">*</span></th>
                        <td>
                            <select name="escalLevel" required>
                                <option value="">선택</option>
                                <c:forEach var="cd" items="${escalLevelList}">
                                    <option value="${cd.codeId}">${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>대상</th><td><input type="text" name="escalTo" placeholder="에스컬레이션 대상"/></td></tr>
                    <tr><th>사유</th><td><textarea name="reason" rows="2"></textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;"><button type="submit" class="btn btn-primary">에스컬레이션 등록</button></div>
            </form>
        </div>
    </div>
</div>

<div class="toolbar"><a href="${ctx}/incident/list" class="btn btn-default">＜ 목록</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="INCIDENT"/>
    <c:param name="bizId" value="${incident.incId}"/>
    <c:param name="returnUrl" value="/incident/detail/${incident.incId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

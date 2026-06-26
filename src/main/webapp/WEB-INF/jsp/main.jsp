<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="운영현황"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-head">
    <div>
        <div class="page-title">운영현황 대시보드</div>
        <div class="page-desc">응용프로그램 표준운영절차 통합 모니터링</div>
    </div>
</div>

<div class="cards">
    <div class="card crit"><div class="label">진행중 장애</div><div class="value">${summary.openIncident}</div></div>
    <div class="card warn"><div class="label">승인대기 변경</div><div class="value">${summary.pendingChange}</div></div>
    <div class="card info"><div class="label">금일 배포</div><div class="value">${summary.todayRelease}</div></div>
    <div class="card warn"><div class="label">금일 점검이상</div><div class="value">${summary.abnormalCheck}</div></div>
    <div class="card ok"><div class="label">운영 시스템</div><div class="value">${summary.systemCnt}</div></div>
</div>

<div class="grid-2">
    <div class="panel">
        <h3>장애 처리현황</h3>
        <div class="statbar">
            <c:forEach var="s" items="${incidentStat}">
                <span class="chip">${s.name}<b>${s.cnt}</b></span>
            </c:forEach>
        </div>
    </div>
    <div class="panel">
        <h3>변경 처리현황</h3>
        <div class="statbar">
            <c:forEach var="s" items="${changeStat}">
                <span class="chip">${s.name}<b>${s.cnt}</b></span>
            </c:forEach>
        </div>
    </div>
</div>

<div class="grid-2">
    <div class="panel">
        <h3>최근 장애 접수</h3>
        <table class="list">
            <thead><tr><th>시스템</th><th>제목</th><th class="center">등급</th><th class="center">상태</th></tr></thead>
            <tbody>
            <c:forEach var="i" items="${recentIncidents}">
                <tr>
                    <td>${i.sysNm}</td>
                    <td><a href="${ctx}/incident/detail/${i.incId}">${i.title}</a></td>
                    <td class="center"><span class="badge sev-${i.severity}">${i.severity}등급</span></td>
                    <td class="center"><span class="badge st-${fn:toLowerCase(i.status)}">${i.statusNm}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentIncidents}"><tr><td colspan="4" class="empty">데이터가 없습니다.</td></tr></c:if>
            </tbody>
        </table>
    </div>
    <div class="panel">
        <h3>최근 변경 요청</h3>
        <table class="list">
            <thead><tr><th>시스템</th><th>제목</th><th class="center">상태</th></tr></thead>
            <tbody>
            <c:forEach var="c" items="${recentChanges}">
                <tr>
                    <td>${c.sysNm}</td>
                    <td><a href="${ctx}/change/detail/${c.chgId}">${c.title}</a></td>
                    <td class="center"><span class="badge st-${fn:toLowerCase(c.status)}">${c.statusNm}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentChanges}"><tr><td colspan="3" class="empty">데이터가 없습니다.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

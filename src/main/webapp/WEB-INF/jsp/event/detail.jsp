<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="pageTitle" value="이벤트 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 운영상태관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">이벤트 상세 <span style="color:#888;font-weight:400;">(EVT-${event.evtId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/event/list" class="btn btn-default">목록</a>
        <form action="${ctx}/event/delete/${event.evtId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>진행 단계 <span class="badge st-${fn:toLowerCase(event.status)}" style="margin-left:6px;">${event.statusNm}</span></h3>
    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
        <jsp:param name="type" value="EVENT"/>
        <jsp:param name="status" value="${event.status}"/>
        <jsp:param name="statusNm" value="${event.statusNm}"/>
        <jsp:param name="mode" value="full"/>
    </jsp:include>
</div>

<div class="detail-stack">
    <div class="panel">
        <h3>이벤트 정보</h3>
        <table class="form">
            <tr>
                <th>대상 시스템</th><td>${event.sysNm}</td>
                <th>유형</th><td>${event.evtTypeNm}</td>
            </tr>
            <tr><th>제목</th><td colspan="3">${event.title}</td></tr>
            <tr>
                <th>심각도 / 상태</th><td>
                    <span class="badge lv-${fn:toLowerCase(event.severity)}">${event.severityNm}</span>&nbsp;
                    <span class="badge st-${fn:toLowerCase(event.status)}">${event.statusNm}</span>
                </td>
                <th>발생일시</th><td>${empty event.occrDt ? '-' : event.occrDt}</td>
            </tr>
            <tr>
                <th>소요일(근무일)</th>
                <td colspan="3">
                    <c:set var="elapsedWd" value="${uf:wdays(event.occrDt, '')}"/>
                    <c:choose><c:when test="${empty elapsedWd}">-</c:when>
                    <c:otherwise><b>${elapsedWd}</b>일 <span class="h-meta">(발생 ~ 오늘 기준)</span></c:otherwise></c:choose>
                </td>
            </tr>
            <tr>
                <th>처리자</th><td>${empty event.chargerId ? '-' : uf:nm(userNameMap, event.chargerId)}</td>
                <th>연계 장애</th><td>
                    <c:choose>
                        <c:when test="${event.linkedIncId != null}"><a href="${ctx}/incident/detail/${event.linkedIncId}">INC-${event.linkedIncId}</a></c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr><th>내용</th><td colspan="3"><div class="rte-view">${event.content}</div></td></tr>
            <tr><th>조치 내용</th><td colspan="3"><div class="rte-view">${empty event.action ? '-' : event.action}</div></td></tr>
        </table>
    </div>

    <div>
        <c:if test="${event.status != 'CLOSED'}">
        <div class="panel">
            <h3>이벤트 처리</h3>
            <form method="post" action="${ctx}/event/process">
                <input type="hidden" name="evtId" value="${event.evtId}"/>
                <table class="form">
                    <tr><th>처리 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <c:forEach var="cd" items="${statusList}">
                                    <option value="${cd.codeId}" ${cd.codeId == event.status ? 'selected' : ''}>${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>장애 연계<br/><small style="color:#999;font-weight:400;">(에스컬레이션 시 장애ID)</small></th>
                        <td><input type="number" name="linkedIncId" value="${event.linkedIncId}" placeholder="연계 장애 ID"/></td></tr>
                    <tr><th>조치 내용</th><td><textarea class="wysiwyg" name="action" rows="3">${event.action}</textarea></td></tr>
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
                <c:forEach var="h" items="${event.historyList}">
                    <li>
                        <span class="badge st-${fn:toLowerCase(h.status)}">${h.statusNm}</span>
                        <span style="white-space:pre-line;">${h.content}</span>
                        <div class="h-meta">${uf:nm(userNameMap, h.procId)} · ${h.procDt}</div>
                    </li>
                </c:forEach>
                <c:if test="${empty event.historyList}"><li style="border:none;">이력이 없습니다.</li></c:if>
            </ul>
        </div>
    </div>
</div>

<div class="toolbar" style="justify-content:flex-end;"><a href="${ctx}/event/list" class="btn btn-default">목록 ＞</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="EVENT"/>
    <c:param name="bizId" value="${event.evtId}"/>
    <c:param name="returnUrl" value="/event/detail/${event.evtId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

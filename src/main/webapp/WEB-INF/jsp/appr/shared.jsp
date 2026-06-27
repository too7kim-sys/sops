<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="공유함"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">내 작업 &gt; 공유함</div>
<div class="page-head">
    <div class="page-title">공유함 <span style="color:#888;font-weight:400;">나에게 공유된 업무</span></div>
</div>

<div class="panel">
    <table class="list">
        <thead>
        <tr>
            <th style="width:110px;">업무</th>
            <th style="width:80px;">번호</th>
            <th>공유 메모</th>
            <th style="width:70px;">열람</th>
            <th style="width:160px;">공유자 / 일시</th>
            <th style="width:80px;"></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty sharedList}"><tr><td colspan="6" class="empty">공유받은 업무가 없습니다.</td></tr></c:if>
        <c:forEach var="s" items="${sharedList}">
            <tr>
                <td><span class="badge">${s.bizTypeNm}</span></td>
                <td>${s.bizType}-${s.bizId}</td>
                <td>${empty s.shareMemo ? '-' : s.shareMemo}</td>
                <td><span class="badge ${s.readAt == 'Y' ? 'st-approved' : 'st-pending'}">${s.readAt == 'Y' ? '열람' : '미열람'}</span></td>
                <td>${empty s.sharedByNm ? s.sharedBy : s.sharedByNm}<div class="h-meta">${s.sharedDt}</div></td>
                <td><a href="${ctx}${s.detailUrl}" class="btn btn-default btn-sm">바로가기</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

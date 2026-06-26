<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:if test="${pageInfo != null and pageInfo.totalPageCount > 1}">
<div class="paging">
    <c:if test="${pageInfo.currentPageNo > 1}">
        <a href="${ctx}${param.baseUrl}?pageIndex=${pageInfo.currentPageNo - 1}">‹ 이전</a>
    </c:if>
    <c:forEach var="p" begin="${pageInfo.firstPageNoOnPageList}" end="${pageInfo.lastPageNoOnPageList}">
        <a href="${ctx}${param.baseUrl}?pageIndex=${p}" class="${p == pageInfo.currentPageNo ? 'on' : ''}">${p}</a>
    </c:forEach>
    <c:if test="${pageInfo.currentPageNo < pageInfo.totalPageCount}">
        <a href="${ctx}${param.baseUrl}?pageIndex=${pageInfo.currentPageNo + 1}">다음 ›</a>
    </c:if>
</div>
</c:if>

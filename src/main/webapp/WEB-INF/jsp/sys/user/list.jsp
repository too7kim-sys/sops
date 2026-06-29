<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="사용자관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 사용자관리</div>
<div class="page-head">
    <div>
        <div class="page-title">사용자관리</div>
        <div class="page-desc">운영자 계정 등록 및 권한 관리</div>
    </div>
    <div class="toolbar">
        <a href="${ctx}/sys/user/write" class="btn btn-primary">＋ 사용자 등록</a>
    </div>
</div>

<form method="get" action="${ctx}/sys/user/list" class="searchbar">
    <select name="searchCondition">
        <option value="">전체 권한</option>
        <option value="ADMIN" ${searchVO.searchCondition == 'ADMIN' ? 'selected' : ''}>운영관리자</option>
        <option value="OPERATOR" ${searchVO.searchCondition == 'OPERATOR' ? 'selected' : ''}>운영자</option>
        <option value="USER" ${searchVO.searchCondition == 'USER' ? 'selected' : ''}>일반사용자</option>
    </select>
    <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}" placeholder="사용자ID/사용자명 검색"/>
    <button type="submit" class="btn btn-default">검색</button>
</form>

<div class="panel mb0">
    <p style="margin-bottom:10px;color:#777;font-size:13px;">총 <b>${totalCnt}</b>건</p>
    <table class="list">
        <thead>
        <tr>
            <th style="width:140px;">사용자ID</th>
            <th style="width:140px;">사용자명</th>
            <th class="center" style="width:80px;">직급</th>
            <th class="center" style="width:110px;">권한</th>
            <th style="width:140px;">부서</th>
            <th>이메일</th>
            <th class="center" style="width:80px;">사용여부</th>
            <th class="center" style="width:130px;">등록일</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${userList}">
            <tr>
                <td><a href="${ctx}/sys/user/edit/${u.userId}">${u.userId}</a></td>
                <td>${u.userNm}</td>
                <td class="center">${empty u.positn ? '-' : u.positn}</td>
                <td class="center">
                    <c:choose>
                        <c:when test="${u.role == 'ADMIN'}">운영관리자</c:when>
                        <c:when test="${u.role == 'OPERATOR'}">운영자</c:when>
                        <c:when test="${u.role == 'USER'}">일반사용자</c:when>
                        <c:otherwise>${u.role}</c:otherwise>
                    </c:choose>
                </td>
                <td>${u.deptNm}</td>
                <td>${u.email}</td>
                <td class="center">${u.useAt}</td>
                <td class="center">${u.regDt}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty userList}"><tr><td colspan="8" class="empty">등록된 사용자가 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/sys/user/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

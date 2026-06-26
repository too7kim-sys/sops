<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="공통코드관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 공통코드관리</div>
<div class="page-head">
    <div>
        <div class="page-title">공통코드관리</div>
        <div class="page-desc">코드그룹별 공통코드 등록 및 관리</div>
    </div>
    <div class="toolbar">
        <a href="${ctx}/sys/code/write" class="btn btn-primary">＋ 코드 등록</a>
    </div>
</div>

<form method="get" action="${ctx}/sys/code/list" class="searchbar">
    <input type="text" name="searchCondition" value="${searchVO.searchCondition}" placeholder="코드그룹"/>
    <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}" placeholder="코드값/코드명 검색"/>
    <button type="submit" class="btn btn-default">검색</button>
</form>

<div class="panel mb0">
    <p style="margin-bottom:10px;color:#777;font-size:13px;">총 <b>${totalCnt}</b>건</p>
    <table class="list">
        <thead>
        <tr>
            <th style="width:200px;">코드그룹</th>
            <th style="width:160px;">코드값</th>
            <th>코드명</th>
            <th class="center" style="width:90px;">정렬순서</th>
            <th class="center" style="width:80px;">사용여부</th>
            <th class="center" style="width:80px;">수정</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="c" items="${codeList}">
            <tr>
                <td>${c.codeGrp}</td>
                <td>${c.codeId}</td>
                <td>${c.codeNm}</td>
                <td class="center">${c.sortOrdr}</td>
                <td class="center">${c.useAt}</td>
                <td class="center">
                    <a href="${ctx}/sys/code/edit?codeGrp=${c.codeGrp}&codeId=${c.codeId}&codeNm=${c.codeNm}&sortOrdr=${c.sortOrdr}&useAt=${c.useAt}">수정</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty codeList}"><tr><td colspan="6" class="empty">등록된 코드가 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/sys/code/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

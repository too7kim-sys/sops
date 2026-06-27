<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="요청관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 요청관리</div>
<div class="page-head">
    <div>
        <div class="page-title">요청관리</div>
        <div class="page-desc">요청 → 접수 → 분류 → 처리 → 종결 표준 처리절차</div>
    </div>
    <div class="toolbar">
        <a href="${ctx}/csr/write" class="btn btn-primary">＋ 요청 등록</a>
    </div>
</div>

<form method="get" action="${ctx}/csr/list" class="searchbar">
    <select name="searchSysId">
        <option value="">전체 시스템</option>
        <c:forEach var="s" items="${systemList}">
            <option value="${s.sysId}" ${s.sysId == searchVO.searchSysId ? 'selected' : ''}>${s.sysNm}</option>
        </c:forEach>
    </select>
    <select name="searchStatus">
        <option value="">전체 상태</option>
        <c:forEach var="cd" items="${statusList}">
            <option value="${cd.codeId}" ${cd.codeId == searchVO.searchStatus ? 'selected' : ''}>${cd.codeNm}</option>
        </c:forEach>
    </select>
    <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}" placeholder="제목 검색"/>
    <button type="submit" class="btn btn-default">검색</button>
</form>

<div class="panel mb0">
    <p style="margin-bottom:10px;color:#777;font-size:13px;">총 <b>${totalCnt}</b>건</p>
    <table class="list">
        <thead>
        <tr>
            <th class="center" style="width:70px;">번호</th>
            <th style="width:160px;">시스템</th>
            <th>제목</th>
            <th class="center" style="width:90px;">유형</th>
            <th class="center" style="width:90px;">우선순위</th>
            <th class="center" style="width:210px;">현재 단계</th>
            <th class="center" style="width:110px;">요청자</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="c" items="${csrList}">
            <tr>
                <td class="center">${c.csrId}</td>
                <td>${c.sysNm}</td>
                <td><a href="${ctx}/csr/detail/${c.csrId}">${c.title}</a></td>
                <td class="center">${c.csrTypeNm}</td>
                <td class="center">${c.priorityNm}</td>
                <td>
                    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
                        <jsp:param name="type" value="CSR"/>
                        <jsp:param name="status" value="${c.status}"/>
                        <jsp:param name="statusNm" value="${c.statusNm}"/>
                        <jsp:param name="mode" value="mini"/>
                    </jsp:include>
                </td>
                <td class="center">${c.reqId}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty csrList}"><tr><td colspan="7" class="empty">등록된 요청이 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/csr/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

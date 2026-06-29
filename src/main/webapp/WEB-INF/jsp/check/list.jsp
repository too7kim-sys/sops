<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="pageTitle" value="운영점검"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 운영점검</div>
<div class="page-head">
    <div>
        <div class="page-title">운영점검</div>
        <div class="page-desc">일일/정기 운영점검 등록 및 점검결과 관리</div>
    </div>
    <div class="toolbar">
        <a href="${ctx}/check/write" class="btn btn-primary">＋ 점검 등록</a>
    </div>
</div>

<form method="get" action="${ctx}/check/list" class="searchbar">
    <select name="searchSysId">
        <option value="">전체 시스템</option>
        <c:forEach var="s" items="${systemList}">
            <option value="${s.sysId}" ${s.sysId == searchVO.searchSysId ? 'selected' : ''}>${s.sysNm}</option>
        </c:forEach>
    </select>
    <select name="searchStatus">
        <option value="">전체 결과</option>
        <c:forEach var="cd" items="${resultList}">
            <option value="${cd.codeId}" ${cd.codeId == searchVO.searchStatus ? 'selected' : ''}>${cd.codeNm}</option>
        </c:forEach>
    </select>
    <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}" placeholder="점검자 검색"/>
    <button type="submit" class="btn btn-default">검색</button>
</form>

<div class="panel mb0">
    <p style="margin-bottom:10px;color:#777;font-size:13px;">총 <b>${totalCnt}</b>건</p>
    <table class="list">
        <thead>
        <tr>
            <th class="center" style="width:70px;">번호</th>
            <th style="width:180px;">시스템</th>
            <th class="center" style="width:100px;">점검유형</th>
            <th class="center" style="width:120px;">점검일자</th>
            <th class="center" style="width:110px;">점검자</th>
            <th class="center" style="width:100px;">종합결과</th>
            <th class="center" style="width:120px;">항목/이상</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="c" items="${checkList}">
            <tr>
                <td class="center">${c.chkId}</td>
                <td><a href="${ctx}/check/detail/${c.chkId}">${c.sysNm}</a></td>
                <td class="center">${c.chkTypeNm}</td>
                <td class="center">${c.chkDt}</td>
                <td class="center">${uf:nm(userNameMap, c.chkrId)}</td>
                <td class="center"><span class="badge st-${fn:toLowerCase(c.result)}">${c.resultNm}</span></td>
                <td class="center"><span>${c.itemCnt}</span> / <b class="${c.abnormalCnt > 0 ? 'st-abnormal' : ''}">${c.abnormalCnt}</b></td>
            </tr>
        </c:forEach>
        <c:if test="${empty checkList}"><tr><td colspan="7" class="empty">등록된 운영점검이 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/check/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

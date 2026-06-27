<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="형상관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 형상관리</div>
<div class="page-head">
    <div>
        <div class="page-title">형상관리</div>
        <div class="page-desc">형상식별 → 기준선 → 체크아웃/체크인 → 형상감사 표준 형상통제절차</div>
    </div>
    <div class="toolbar">
        <a href="${ctx}/ci/write" class="btn btn-primary">＋ 형상항목 식별</a>
    </div>
</div>

<form method="get" action="${ctx}/ci/list" class="searchbar">
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
    <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}" placeholder="형상항목명 검색"/>
    <button type="submit" class="btn btn-default">검색</button>
</form>

<div class="panel mb0">
    <p style="margin-bottom:10px;color:#777;font-size:13px;">총 <b>${totalCnt}</b>건</p>
    <table class="list">
        <thead>
        <tr>
            <th class="center" style="width:70px;">번호</th>
            <th style="width:160px;">시스템</th>
            <th>형상항목명</th>
            <th class="center" style="width:100px;">유형</th>
            <th class="center" style="width:90px;">버전</th>
            <th class="center" style="width:210px;">현재 단계</th>
            <th class="center" style="width:110px;">담당자</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="c" items="${ciList}">
            <tr>
                <td class="center">${c.ciId}</td>
                <td>${c.sysNm}</td>
                <td><a href="${ctx}/ci/detail/${c.ciId}">${c.ciNm}</a></td>
                <td class="center">${c.ciTypeNm}</td>
                <td class="center">${empty c.ver ? '-' : c.ver}</td>
                <td>
                    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
                        <jsp:param name="type" value="CI"/>
                        <jsp:param name="status" value="${c.ciStatus}"/>
                        <jsp:param name="statusNm" value="${c.ciStatusNm}"/>
                        <jsp:param name="mode" value="mini"/>
                    </jsp:include>
                </td>
                <td class="center">${empty c.ownerId ? '-' : c.ownerId}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty ciList}"><tr><td colspan="7" class="empty">등록된 형상항목이 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/ci/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

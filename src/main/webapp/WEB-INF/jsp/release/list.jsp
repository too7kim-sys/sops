<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="pageTitle" value="배포관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 배포관리</div>
<div class="page-head">
    <div>
        <div class="page-title">배포관리</div>
        <div class="page-desc">배포계획 → 승인 → 배포 → 완료/롤백 표준 처리절차</div>
    </div>
    <div class="toolbar">
        <a href="${ctx}/release/write" class="btn btn-primary">＋ 배포계획 등록</a>
    </div>
</div>

<form method="get" action="${ctx}/release/list" class="searchbar">
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
            <th class="center" style="width:110px;">버전</th>
            <th>제목</th>
            <th class="center" style="width:210px;">현재 단계</th>
            <th class="center" style="width:90px;">담당자</th>
            <th class="center" style="width:130px;">배포예정일</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${releaseList}">
            <tr>
                <td class="center">${r.relId}</td>
                <td>${r.sysNm}</td>
                <td class="center">${r.ver}</td>
                <td><a href="${ctx}/release/detail/${r.relId}">${r.title}</a></td>
                <td>
                    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
                        <jsp:param name="type" value="RELEASE"/>
                        <jsp:param name="status" value="${r.status}"/>
                        <jsp:param name="statusNm" value="${r.statusNm}"/>
                        <jsp:param name="mode" value="mini"/>
                    </jsp:include>
                </td>
                <td class="center">${uf:nm(userNameMap, r.chargerId)}</td>
                <td class="center">${r.planDt}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty releaseList}"><tr><td colspan="7" class="empty">등록된 배포가 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/release/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

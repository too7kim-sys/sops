<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="응용시스템"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 응용시스템</div>
<div class="page-head">
    <div>
        <div class="page-title">응용시스템 마스터</div>
        <div class="page-desc">운영대상 응용시스템 기준정보 관리</div>
    </div>
    <div class="toolbar">
        <button type="button" class="btn btn-default" onclick="exportListExcel()">⬇ 엑셀</button>
        <a href="${ctx}/system/write" class="btn btn-primary">＋ 시스템 등록</a>
    </div>
</div>

<form method="get" action="${ctx}/system/list" class="searchbar">
    <select name="searchCondition">
        <option value="">전체 등급</option>
        <option value="1" ${searchVO.searchCondition == '1' ? 'selected' : ''}>1등급(상)</option>
        <option value="2" ${searchVO.searchCondition == '2' ? 'selected' : ''}>2등급(중)</option>
        <option value="3" ${searchVO.searchCondition == '3' ? 'selected' : ''}>3등급(하)</option>
    </select>
    <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}" placeholder="시스템ID/시스템명/담당자 검색"/>
    <button type="submit" class="btn btn-default">검색</button>
</form>

<div class="panel mb0">
    <p style="margin-bottom:10px;color:#777;font-size:13px;">총 <b>${totalCnt}</b>건</p>
    <div class="list-scroll"><table class="list">
        <thead>
        <tr>
            <th style="width:140px;">시스템ID</th>
            <th>시스템명</th>
            <th class="center" style="width:110px;">운영담당자</th>
            <th class="center" style="width:130px;">운영부서</th>
            <th class="center" style="width:110px;">중요도등급</th>
            <th class="center" style="width:90px;">사용여부</th>
            <th class="center" style="width:130px;">등록일</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${systemList}">
            <tr>
                <td><a href="${ctx}/system/detail/${s.sysId}">${s.sysId}</a></td>
                <td>${s.sysNm}</td>
                <td class="center">${s.mngrNm}</td>
                <td class="center">${s.mngrDept}</td>
                <td class="center">
                    <c:choose>
                        <c:when test="${s.grad == '1'}">1등급(상)</c:when>
                        <c:when test="${s.grad == '2'}">2등급(중)</c:when>
                        <c:when test="${s.grad == '3'}">3등급(하)</c:when>
                        <c:otherwise>${s.grad}</c:otherwise>
                    </c:choose>
                </td>
                <td class="center">${s.useAt == 'Y' ? '사용' : '미사용'}</td>
                <td class="center">${s.regDt}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty systemList}"><tr><td colspan="7" class="empty">등록된 시스템이 없습니다.</td></tr></c:if>
        </tbody>
    </table></div>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/system/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

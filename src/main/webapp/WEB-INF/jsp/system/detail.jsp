<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="응용시스템 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 응용시스템 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">응용시스템 상세 <span style="color:#888;font-weight:400;">(${system.sysId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/system/edit/${system.sysId}" class="btn btn-default">수정</a>
        <form action="${ctx}/system/delete/${system.sysId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>시스템 정보</h3>
    <table class="form">
        <tr><th>시스템ID</th><td>${system.sysId}</td></tr>
        <tr><th>시스템명</th><td>${system.sysNm}</td></tr>
        <tr><th>중요도등급</th><td>
            <c:choose>
                <c:when test="${system.grad == '1'}">1등급(상)</c:when>
                <c:when test="${system.grad == '2'}">2등급(중)</c:when>
                <c:when test="${system.grad == '3'}">3등급(하)</c:when>
                <c:otherwise>${system.grad}</c:otherwise>
            </c:choose>
        </td></tr>
        <tr><th>운영담당자</th><td>${empty system.mngrNm ? '-' : system.mngrNm}</td></tr>
        <tr><th>운영부서</th><td>${empty system.mngrDept ? '-' : system.mngrDept}</td></tr>
        <tr><th>사용여부</th><td>${system.useAt == 'Y' ? '사용' : '미사용'}</td></tr>
        <tr><th>등록일</th><td>${empty system.regDt ? '-' : system.regDt}</td></tr>
        <tr><th>시스템설명</th><td><div class="rte-view">${empty system.sysDesc ? '-' : system.sysDesc}</div></td></tr>
    </table>
</div>

<div class="toolbar"><a href="${ctx}/system/list" class="btn btn-default">＜ 목록</a></div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

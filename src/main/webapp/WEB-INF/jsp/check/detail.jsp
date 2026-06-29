<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="pageTitle" value="운영점검 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 운영점검 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">운영점검 상세 <span style="color:#888;font-weight:400;">(CHK-${check.chkId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/check/list" class="btn btn-default">목록</a>
        <form action="${ctx}/check/delete/${check.chkId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>점검 정보</h3>
    <table class="form">
        <tr><th>대상 시스템</th><td>${check.sysNm}</td>
            <th>점검 유형</th><td>${check.chkTypeNm}</td></tr>
        <tr><th>점검 일자</th><td>${check.chkDt}</td>
            <th>점검자</th><td>${empty check.chkrId ? '-' : uf:nm(userNameMap, check.chkrId)}</td></tr>
        <tr><th>종합 결과</th><td>
                <span class="badge st-${fn:toLowerCase(check.result)}">${check.resultNm}</span>
            </td>
            <th>등록 일시</th><td>${check.regDt}</td></tr>
        <tr><th>비고</th><td colspan="3" style="white-space:pre-line;">${empty check.remark ? '-' : check.remark}</td></tr>
    </table>
</div>

<div class="panel">
    <h3>점검 항목</h3>
    <table class="list">
        <thead>
        <tr>
            <th class="center" style="width:50px;">#</th>
            <th>항목명</th>
            <th class="center" style="width:120px;">결과</th>
            <th>비고</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="it" items="${check.itemList}" varStatus="stat">
            <tr>
                <td class="center">${stat.count}</td>
                <td>${it.itemNm}</td>
                <td class="center"><span class="badge st-${fn:toLowerCase(it.itemResult)}">${it.itemResult == 'ABNORMAL' ? '이상' : '정상'}</span></td>
                <td>${empty it.itemRemark ? '-' : it.itemRemark}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty check.itemList}"><tr><td colspan="4" class="empty">점검 항목이 없습니다.</td></tr></c:if>
        </tbody>
    </table>
</div>

<div class="toolbar" style="justify-content:flex-end;"><a href="${ctx}/check/list" class="btn btn-default">목록 ＞</a></div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="테스트 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 테스트관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">테스트 상세 <span style="color:#888;font-weight:400;">(TEST-${test.testId})</span></div>
    <div class="toolbar">
        <form action="${ctx}/test/delete/${test.testId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="grid-2">
    <div class="panel">
        <h3>테스트 정보</h3>
        <table class="form">
            <tr><th>대상 시스템</th><td>${test.sysNm}</td></tr>
            <tr><th>제목</th><td>${test.title}</td></tr>
            <tr><th>유형 / 환경</th><td><span>${test.testTypeNm}</span> / <span>${test.testEnvNm}</span></td></tr>
            <tr><th>상태</th><td>
                <span class="badge st-${fn:toLowerCase(test.status)}">${test.statusNm}</span>
            </td></tr>
            <tr><th>연계 변경 ID</th><td>${empty test.chgId ? '-' : test.chgId}</td></tr>
            <tr><th>예정일</th><td>${empty test.planDt ? '-' : test.planDt}</td></tr>
            <tr><th>테스터</th><td>${empty test.testerId ? '-' : test.testerId}</td></tr>
            <tr><th>등록 일시</th><td>${test.regDt}</td></tr>
            <tr><th>결과 요약</th><td><div class="rte-view">${empty test.resultSummary ? '-' : test.resultSummary}</div></td></tr>
        </table>
    </div>

    <div>
        <c:if test="${test.status != 'CLOSED' and test.status != 'FAILED'}">
        <div class="panel">
            <h3>테스트 처리</h3>
            <form method="post" action="${ctx}/test/process">
                <input type="hidden" name="testId" value="${test.testId}"/>
                <table class="form">
                    <tr><th>처리 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <c:forEach var="cd" items="${statusList}">
                                    <option value="${cd.codeId}" ${cd.codeId == test.status ? 'selected' : ''}>${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>테스터</th><td><input type="text" name="testerId" value="${test.testerId}"/></td></tr>
                    <tr><th>결과 요약</th><td><textarea class="wysiwyg" name="resultSummary" rows="4">${test.resultSummary}</textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;">
                    <button type="submit" class="btn btn-success">처리 등록</button>
                </div>
            </form>
        </div>
        </c:if>

        <div class="panel">
            <h3>테스트 케이스</h3>
            <table class="list">
                <thead>
                <tr>
                    <th class="center" style="width:50px;">#</th>
                    <th>케이스명</th>
                    <th>기대 결과</th>
                    <th class="center" style="width:90px;">결과</th>
                    <th>비고</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="cs" items="${test.caseList}" varStatus="stat">
                    <tr>
                        <td class="center">${stat.count}</td>
                        <td>${cs.caseNm}</td>
                        <td>${empty cs.expected ? '-' : cs.expected}</td>
                        <td class="center"><span class="badge st-${fn:toLowerCase(cs.caseResult)}">${cs.caseResultNm}</span></td>
                        <td>${empty cs.remark ? '-' : cs.remark}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty test.caseList}"><tr><td colspan="5" class="empty">테스트 케이스가 없습니다.</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="toolbar"><a href="${ctx}/test/list" class="btn btn-default">＜ 목록</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="TEST"/>
    <c:param name="bizId" value="${test.testId}"/>
    <c:param name="returnUrl" value="/test/detail/${test.testId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

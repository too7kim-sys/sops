<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="요청 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 요청관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">요청 상세 <span style="color:#888;font-weight:400;">(CSR-${csr.csrId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/csr/edit/${csr.csrId}" class="btn btn-default">수정</a>
        <form action="${ctx}/csr/delete/${csr.csrId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>진행 단계 <span class="badge st-${fn:toLowerCase(csr.status)}" style="margin-left:6px;">${csr.statusNm}</span></h3>
    <jsp:include page="/WEB-INF/jsp/include/csr-stage.jsp">
        <jsp:param name="status" value="${csr.status}"/>
        <jsp:param name="mode" value="full"/>
    </jsp:include>
</div>

<div class="grid-2">
    <div class="panel">
        <h3>요청 정보</h3>
        <table class="form">
            <tr><th>대상 시스템</th><td>${csr.sysNm}</td></tr>
            <tr><th>제목</th><td>${csr.title}</td></tr>
            <tr><th>유형 / 우선순위</th><td>
                <span>${csr.csrTypeNm}</span>
                &nbsp;/&nbsp;
                <span>${csr.priorityNm}</span>
            </td></tr>
            <tr><th>상태</th><td>
                <span class="badge st-${fn:toLowerCase(csr.status)}">${csr.statusNm}</span>
            </td></tr>
            <tr><th>요청자 / 요청일시</th><td><span>${empty csr.reqId ? '-' : csr.reqId}</span> / <span>${empty csr.reqDt ? '-' : csr.reqDt}</span></td></tr>
            <tr><th>담당자</th><td>${empty csr.chargerId ? '-' : csr.chargerId}</td></tr>
            <tr><th>처리일시</th><td>${empty csr.procDt ? '-' : csr.procDt}</td></tr>
            <tr><th>요청 내용</th><td><div class="rte-view">${csr.content}</div></td></tr>
            <tr><th>처리 내용</th><td><div class="rte-view">${empty csr.procContent ? '-' : csr.procContent}</div></td></tr>
        </table>
    </div>

    <div>
        <c:if test="${csr.status != 'CLOSED' and csr.status != 'REJECTED'}">
        <div class="panel">
            <h3>요청 처리</h3>
            <form method="post" action="${ctx}/csr/process">
                <input type="hidden" name="csrId" value="${csr.csrId}"/>
                <table class="form">
                    <tr><th>처리 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <c:forEach var="cd" items="${statusList}">
                                    <option value="${cd.codeId}" ${cd.codeId == csr.status ? 'selected' : ''}>${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>담당자</th><td><input type="text" name="chargerId" value="${csr.chargerId}"/></td></tr>
                    <tr><th>처리 내용</th><td><textarea class="wysiwyg" name="procContent" rows="4">${csr.procContent}</textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;">
                    <button type="submit" class="btn btn-success">처리 등록</button>
                </div>
            </form>
        </div>
        </c:if>

        <div class="panel">
            <h3>처리 이력</h3>
            <ul class="history">
                <c:forEach var="h" items="${csr.historyList}">
                    <li>
                        <span class="badge st-${fn:toLowerCase(h.status)}">${h.statusNm}</span>
                        <span style="white-space:pre-line;">${h.content}</span>
                        <div class="h-meta"><span>${h.procId}</span> · <span>${h.procDt}</span></div>
                    </li>
                </c:forEach>
                <c:if test="${empty csr.historyList}"><li style="border:none;">이력이 없습니다.</li></c:if>
            </ul>
        </div>
    </div>
</div>

<div class="toolbar"><a href="${ctx}/csr/list" class="btn btn-default">＜ 목록</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="CSR"/>
    <c:param name="bizId" value="${csr.csrId}"/>
    <c:param name="returnUrl" value="/csr/detail/${csr.csrId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

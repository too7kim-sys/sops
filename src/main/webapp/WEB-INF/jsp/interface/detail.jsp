<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="pageTitle" value="연계 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 연계관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">연계 상세 <span style="color:#888;font-weight:400;">(INTF-${itf.intfId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/interface/list" class="btn btn-default">목록</a>
        <form action="${ctx}/interface/delete/${itf.intfId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>진행 단계 <span class="badge st-${fn:toLowerCase(itf.status)}" style="margin-left:6px;">${itf.statusNm}</span></h3>
    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
        <jsp:param name="type" value="INTERFACE"/>
        <jsp:param name="status" value="${itf.status}"/>
        <jsp:param name="statusNm" value="${itf.statusNm}"/>
        <jsp:param name="mode" value="full"/>
    </jsp:include>
</div>

<div class="detail-stack">
    <div class="panel">
        <h3>연계 정보</h3>
        <table class="form">
            <tr><th>대상 시스템</th><td>${itf.sysNm}</td></tr>
            <tr><th>제목</th><td>${itf.title}</td></tr>
            <tr><th>상대 시스템</th><td>${empty itf.partnerSys ? '-' : itf.partnerSys}</td></tr>
            <tr><th>연계 유형 / 상태</th><td>
                <span>${itf.ifTypeNm}</span>
                &nbsp;
                <span class="badge st-${fn:toLowerCase(itf.status)}">${itf.statusNm}</span>
            </td></tr>
            <tr><th>요청자 / 요청일시</th><td><span>${uf:nm(userNameMap, itf.reqId)}</span> / <span>${itf.reqDt}</span></td></tr>
            <tr><th>처리자</th><td>${empty itf.chargerId ? '-' : uf:nm(userNameMap, itf.chargerId)}</td></tr>
            <tr><th>예정일 / 완료일시</th><td><span>${empty itf.planDt ? '-' : itf.planDt}</span> / <span>${empty itf.completeDt ? '-' : itf.completeDt}</span></td></tr>
            <tr><th>연계 데이터</th><td><div class="rte-view">${empty itf.dataDesc ? '-' : itf.dataDesc}</div></td></tr>
            <tr><th>처리 결과</th><td><div class="rte-view">${empty itf.result ? '-' : itf.result}</div></td></tr>
        </table>
    </div>

    <div>
        <c:if test="${itf.status != 'COMPLETED' and itf.status != 'REJECTED'}">
        <div class="panel">
            <h3>연계 처리</h3>
            <form method="post" action="${ctx}/interface/process">
                <input type="hidden" name="intfId" value="${itf.intfId}"/>
                <table class="form">
                    <tr><th>처리 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <c:forEach var="cd" items="${statusList}">
                                    <option value="${cd.codeId}" ${cd.codeId == itf.status ? 'selected' : ''}>${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>처리자</th><td><select name="chargerId"><option value="">선택</option><c:forEach var="__u" items="${userNameMap}"><option value="${__u.key}" ${__u.key == itf.chargerId ? 'selected' : ''}>${__u.value}</option></c:forEach></select></td></tr>
                    <tr><th>처리 결과</th><td><textarea class="wysiwyg" name="result" rows="4">${itf.result}</textarea></td></tr>
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
                <c:forEach var="h" items="${itf.historyList}">
                    <li>
                        <span class="badge st-${fn:toLowerCase(h.status)}">${h.statusNm}</span>
                        <span style="white-space:pre-line;">${h.content}</span>
                        <div class="h-meta"><span>${uf:nm(userNameMap, h.procId)}</span> · <span>${h.procDt}</span></div>
                    </li>
                </c:forEach>
                <c:if test="${empty itf.historyList}"><li style="border:none;">이력이 없습니다.</li></c:if>
            </ul>
        </div>
    </div>
</div>

<div class="toolbar" style="justify-content:flex-end;"><a href="${ctx}/interface/list" class="btn btn-default">목록 ＞</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="INTERFACE"/>
    <c:param name="bizId" value="${itf.intfId}"/>
    <c:param name="returnUrl" value="/interface/detail/${itf.intfId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

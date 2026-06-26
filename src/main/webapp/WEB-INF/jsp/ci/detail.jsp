<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="형상항목 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 형상관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">형상항목 상세 <span style="color:#888;font-weight:400;">(CI-${ci.ciId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/ci/edit/${ci.ciId}" class="btn btn-default">수정</a>
        <form action="${ctx}/ci/delete/${ci.ciId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="grid-2">
    <div class="panel">
        <h3>형상 정보</h3>
        <table class="form">
            <tr><th>대상 시스템</th><td>${ci.sysNm}</td></tr>
            <tr><th>형상항목명</th><td>${ci.ciNm}</td></tr>
            <tr><th>유형 / 버전</th><td>${ci.ciTypeNm} / ${empty ci.ver ? '-' : ci.ver}</td></tr>
            <tr><th>형상 상태</th><td>
                <span class="badge st-${fn:toLowerCase(ci.ciStatus)}">${ci.ciStatusNm}</span>
            </td></tr>
            <tr><th>담당자</th><td>${empty ci.ownerId ? '-' : ci.ownerId}</td></tr>
            <tr><th>저장 위치</th><td>${empty ci.location ? '-' : ci.location}</td></tr>
            <tr><th>등록 일시</th><td>${ci.regDt}</td></tr>
            <tr><th>설명</th><td><div class="rte-view">${empty ci.ciDesc ? '-' : ci.ciDesc}</div></td></tr>
        </table>
    </div>

    <div>
        <div class="panel">
            <h3>형상 통제/감사</h3>
            <form method="post" action="${ctx}/ci/process">
                <input type="hidden" name="ciId" value="${ci.ciId}"/>
                <table class="form">
                    <tr><th>변경 유형 <span class="required">*</span></th>
                        <td>
                            <select name="chgType" required>
                                <option value="">선택</option>
                                <c:forEach var="cd" items="${chgTypeList}">
                                    <option value="${cd.codeId}">${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>버전</th><td><input type="text" name="ver" value="${ci.ver}" placeholder="기준선 시 갱신 버전"/></td></tr>
                    <tr><th>내용</th><td><textarea class="wysiwyg" name="content" rows="4" placeholder="형상통제/감사 내용"></textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;">
                    <button type="submit" class="btn btn-success">처리 등록</button>
                </div>
            </form>
        </div>

        <div class="panel">
            <h3>형상 이력</h3>
            <ul class="history">
                <c:forEach var="h" items="${ci.historyList}">
                    <li>
                        <span class="badge st-${fn:toLowerCase(h.chgType)}">${h.chgTypeNm}</span>
                        <c:if test="${h.ver != null and h.ver != ''}"><span style="color:#888;">[v${h.ver}]</span></c:if>
                        <span style="white-space:pre-line;">${h.content}</span>
                        <div class="h-meta">${h.procId} · ${h.procDt}</div>
                    </li>
                </c:forEach>
                <c:if test="${empty ci.historyList}"><li style="border:none;">이력이 없습니다.</li></c:if>
            </ul>
        </div>
    </div>
</div>

<div class="toolbar"><a href="${ctx}/ci/list" class="btn btn-default">＜ 목록</a></div>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

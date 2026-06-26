<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="장애 접수/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${incident.incId == null}"/>

<div class="breadcrumb">표준운영절차 &gt; 장애관리 &gt; ${isNew ? '접수' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '장애 접수' : '장애 정보 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/incident/insert' : '/incident/update'}">
    <c:if test="${not isNew}"><input type="hidden" name="incId" value="${incident.incId}"/></c:if>
    <div class="panel">
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == incident.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>장애 등급 <span class="required">*</span></th>
                <td>
                    <select name="severity" required>
                        <c:forEach var="cd" items="${severityList}">
                            <option value="${cd.codeId}" ${cd.codeId == incident.severity ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${incident.title}" required/></td>
            </tr>
            <tr>
                <th>발생일시</th>
                <td><input type="datetime-local" name="occrDt" value="${fn:replace(incident.occrDt,' ','T')}"/></td>
                <th>담당자</th>
                <td><input type="text" name="chargerId" value="${incident.chargerId}" placeholder="처리 담당자 ID"/></td>
            </tr>
            <tr>
                <th>장애 내용</th>
                <td colspan="3"><textarea class="wysiwyg" name="content" rows="5">${incident.content}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/incident/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

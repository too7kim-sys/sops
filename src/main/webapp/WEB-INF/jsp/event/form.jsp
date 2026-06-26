<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="이벤트 등록"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 운영상태관리 &gt; 등록</div>
<div class="page-head">
    <div class="page-title">이벤트 등록</div>
</div>

<form method="post" action="${ctx}/event/insert">
    <div class="panel">
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == event.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>이벤트 유형 <span class="required">*</span></th>
                <td>
                    <select name="evtType" required>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == event.evtType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>심각도 <span class="required">*</span></th>
                <td>
                    <select name="severity" required>
                        <c:forEach var="cd" items="${severityList}">
                            <option value="${cd.codeId}" ${cd.codeId == event.severity ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>발생일시</th>
                <td><input type="datetime-local" name="occrDt" value="${fn:replace(event.occrDt,' ','T')}"/></td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${event.title}" required/></td>
            </tr>
            <tr>
                <th>내용</th>
                <td colspan="3"><textarea class="wysiwyg" name="content" rows="5">${event.content}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/event/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

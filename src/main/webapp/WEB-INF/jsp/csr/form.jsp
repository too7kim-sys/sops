<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="요청 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${csr.csrId == null}"/>

<div class="breadcrumb">표준운영절차 &gt; 요청관리 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '요청 등록' : '요청 정보 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/csr/insert' : '/csr/update'}">
    <input type="hidden" name="csrId" value="${csr.csrId}"/>
    <div class="panel">
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == csr.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>요청 유형 <span class="required">*</span></th>
                <td>
                    <select name="csrType" required>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == csr.csrType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>우선순위 <span class="required">*</span></th>
                <td>
                    <select name="priority" required>
                        <c:forEach var="cd" items="${priorityList}">
                            <option value="${cd.codeId}" ${cd.codeId == csr.priority ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>담당자</th>
                <td><input type="text" name="chargerId" value="${csr.chargerId}" placeholder="처리 담당자 ID"/></td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${csr.title}" required/></td>
            </tr>
            <tr>
                <th>요청 내용</th>
                <td colspan="3"><textarea class="wysiwyg" name="content" rows="5">${csr.content}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/csr/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

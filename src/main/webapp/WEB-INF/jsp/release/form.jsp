<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="배포계획 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${release.relId == null}"/>

<div class="breadcrumb">표준운영절차 &gt; 배포관리 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '배포계획 등록' : '배포 정보 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/release/insert' : '/release/update'}">
    <input type="hidden" name="relId" value="${release.relId}"/>
    <div class="panel">
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == release.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>버전 <span class="required">*</span></th>
                <td><input type="text" name="ver" value="${release.ver}" placeholder="예: v1.2.0" required/></td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${release.title}" required/></td>
            </tr>
            <tr>
                <th>연계 변경ID</th>
                <td colspan="3">
                    <input type="number" name="chgId" value="${release.chgId}" placeholder="변경요청 ID(선택)"/>
                    <span class="h-meta">배포 예정일시(시작~배포 일시)는 결재 완료 후 [배포 처리]에서 등록합니다.</span>
                </td>
            </tr>
            <tr>
                <th>배포 내용</th>
                <td colspan="3"><textarea class="wysiwyg" name="content" rows="5">${release.content}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/release/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

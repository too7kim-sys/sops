<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="변경요청/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${change.chgId == null}"/>

<div class="breadcrumb">표준운영절차 &gt; 변경관리 &gt; ${isNew ? '요청' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '변경요청' : '변경 정보 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/change/insert' : '/change/update'}">
    <input type="hidden" name="chgId" value="${change.chgId}"/>
    <div class="panel">
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == change.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>변경 유형 <span class="required">*</span></th>
                <td>
                    <select name="chgType" required>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == change.chgType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${change.title}" required/></td>
            </tr>
            <tr>
                <th>적용 예정일</th>
                <td colspan="3"><input type="date" name="planDt" value="${change.planDt}"/></td>
            </tr>
            <tr>
                <th>변경 사유</th>
                <td colspan="3"><textarea name="reason" rows="3">${change.reason}</textarea></td>
            </tr>
            <tr>
                <th>변경 내용</th>
                <td colspan="3"><textarea name="content" rows="5">${change.content}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/change/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="형상항목 식별/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${ci.ciId == null}"/>

<div class="breadcrumb">표준운영절차 &gt; 형상관리 &gt; ${isNew ? '식별' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '형상항목 식별' : '형상항목 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/ci/insert' : '/ci/update'}">
    <input type="hidden" name="ciId" value="${ci.ciId}"/>
    <div class="panel">
        <h3>형상 정보</h3>
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == ci.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>유형 <span class="required">*</span></th>
                <td>
                    <select name="ciType" required>
                        <option value="">선택</option>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == ci.ciType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>형상항목명 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="ciNm" value="${ci.ciNm}" required/></td>
            </tr>
            <tr>
                <th>버전</th>
                <td><input type="text" name="ver" value="${ci.ver}" placeholder="예: 1.0.0"/></td>
                <th>처리자</th>
                <td><select name="ownerId"><option value="">선택</option><c:forEach var="__u" items="${userNameMap}"><option value="${__u.key}" ${__u.key == ci.ownerId ? 'selected' : ''}>${__u.value}</option></c:forEach></select></td>
            </tr>
            <tr>
                <th>저장 위치</th>
                <td colspan="3"><input type="text" name="location" value="${ci.location}" placeholder="예: git 저장소 경로, 형상 보관 위치"/></td>
            </tr>
            <tr>
                <th>설명</th>
                <td colspan="3"><textarea class="wysiwyg" name="ciDesc" rows="5">${ci.ciDesc}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/ci/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

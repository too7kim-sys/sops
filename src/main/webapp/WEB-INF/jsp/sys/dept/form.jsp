<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="부서 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${dept.deptId == null}"/>

<div class="breadcrumb">기준정보 관리 &gt; 부서관리 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '부서 등록' : '부서 정보 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/sys/dept/insert' : '/sys/dept/update'}">
    <c:if test="${not isNew}"><input type="hidden" name="deptId" value="${dept.deptId}"/></c:if>
    <div class="panel">
        <table class="form">
            <tr>
                <th>부서코드</th>
                <td><input type="text" name="deptCd" value="${dept.deptCd}" placeholder="예) D100"/></td>
                <th>부서명 <span class="required">*</span></th>
                <td><input type="text" name="deptNm" value="${dept.deptNm}" required/></td>
            </tr>
            <tr>
                <th>상위부서</th>
                <td>
                    <select name="upperId">
                        <option value="">없음(최상위)</option>
                        <c:forEach var="p" items="${deptComboList}">
                            <c:if test="${p.deptId != dept.deptId}">
                                <option value="${p.deptId}" ${p.deptId == dept.upperId ? 'selected' : ''}>${p.deptNm}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </td>
                <th>부서장</th>
                <td>
                    <select name="mngrId">
                        <option value="">미지정</option>
                        <c:forEach var="u" items="${userList}">
                            <option value="${u.userId}" ${u.userId == dept.mngrId ? 'selected' : ''}>${u.userNm} (${u.userId})</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>정렬순서</th>
                <td><input type="number" name="sortOrdr" value="${empty dept.sortOrdr ? 1 : dept.sortOrdr}" min="1" style="width:80px;"/></td>
                <th>사용여부 <span class="required">*</span></th>
                <td>
                    <select name="useAt" required>
                        <option value="Y" ${(dept.useAt == 'Y' or empty dept.useAt) ? 'selected' : ''}>사용</option>
                        <option value="N" ${dept.useAt == 'N' ? 'selected' : ''}>미사용</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>설명</th>
                <td colspan="3"><textarea name="deptDesc" rows="3">${dept.deptDesc}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/sys/dept/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

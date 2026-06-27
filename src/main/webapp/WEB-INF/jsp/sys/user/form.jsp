<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="사용자 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${user.userId == null or user.userId == ''}"/>

<div class="breadcrumb">기준정보 관리 &gt; 사용자관리 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '사용자 등록' : '사용자 정보 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/sys/user/insert' : '/sys/user/update'}">
    <div class="panel">
        <table class="form">
            <tr>
                <th>사용자ID <span class="required">*</span></th>
                <td>
                    <input type="text" name="userId" value="${user.userId}" required ${isNew ? '' : 'readonly'}/>
                </td>
                <th>사용자명 <span class="required">*</span></th>
                <td><input type="text" name="userNm" value="${user.userNm}" required/></td>
            </tr>
            <tr>
                <th>비밀번호 <c:if test="${isNew}"><span class="required">*</span></c:if></th>
                <td colspan="3">
                    <input type="password" name="password" ${isNew ? 'required' : ''}
                           placeholder="${isNew ? '비밀번호 입력' : '변경시에만 입력'}"/>
                </td>
            </tr>
            <tr>
                <th>권한 <span class="required">*</span></th>
                <td>
                    <select name="role" required>
                        <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>운영관리자</option>
                        <option value="OPERATOR" ${user.role == 'OPERATOR' ? 'selected' : ''}>운영자</option>
                        <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>일반사용자</option>
                    </select>
                </td>
                <th>사용여부 <span class="required">*</span></th>
                <td>
                    <select name="useAt" required>
                        <option value="Y" ${(user.useAt == 'Y' or user.useAt == null or user.useAt == '') ? 'selected' : ''}>Y</option>
                        <option value="N" ${user.useAt == 'N' ? 'selected' : ''}>N</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>부서</th>
                <td>
                    <input type="text" name="deptNm" id="userDept_nm" class="dept-search-disp" data-prefix="userDept"
                           value="${user.deptNm}" placeholder="부서명 입력 후 Enter 또는 검색" autocomplete="off"/>
                    <button type="button" class="btn btn-default btn-sm" onclick="openDeptPopup('userDept')">검색</button>
                </td>
                <th>연락처</th>
                <td><input type="text" name="telno" value="${user.telno}"/></td>
            </tr>
            <tr>
                <th>이메일</th>
                <td colspan="3"><input type="email" name="email" value="${user.email}"/></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/sys/user/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

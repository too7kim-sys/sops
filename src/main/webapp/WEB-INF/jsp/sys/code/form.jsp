<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="공통코드 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${code.codeId == null or code.codeId == ''}"/>

<div class="breadcrumb">기준정보 관리 &gt; 공통코드관리 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '공통코드 등록' : '공통코드 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/sys/code/insert' : '/sys/code/update'}">
    <div class="panel">
        <table class="form">
            <tr>
                <th>코드그룹 <span class="required">*</span></th>
                <td>
                    <input type="text" name="codeGrp" value="${code.codeGrp}" required ${isNew ? '' : 'readonly'}/>
                </td>
                <th>코드그룹명 <span class="required">*</span></th>
                <td>
                    <input type="text" name="grpNm" value="${code.grpNm}" required placeholder="예) 요청 상태"/>
                </td>
            </tr>
            <tr>
                <th>코드값 <span class="required">*</span></th>
                <td>
                    <input type="text" name="codeId" value="${code.codeId}" required ${isNew ? '' : 'readonly'}/>
                </td>
                <th>코드명 <span class="required">*</span></th>
                <td><input type="text" name="codeNm" value="${code.codeNm}" required/></td>
            </tr>
            <tr>
                <th>정렬순서</th>
                <td><input type="number" name="sortOrdr" value="${code.sortOrdr}"/></td>
                <th>사용여부 <span class="required">*</span></th>
                <td>
                    <select name="useAt" required>
                        <option value="Y" ${(code.useAt == 'Y' or code.useAt == null or code.useAt == '') ? 'selected' : ''}>Y</option>
                        <option value="N" ${code.useAt == 'N' ? 'selected' : ''}>N</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>상위코드값</th>
                <td colspan="3">
                    <input type="text" name="upperCode" value="${code.upperCode}" placeholder="계층코드인 경우 상위 코드값(예: CSR_SUBTYPE → CSR_TYPE 코드)"/>
                    <div class="h-meta">소분류 코드일 때만 입력합니다. 예) 요청 소분류(CSR_SUBTYPE)의 상위값은 요청 대분류(CSR_TYPE) 코드값(GENERAL/INCIDENT/CHANGE/BACKUP/CONFIG)</div>
                </td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/sys/code/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

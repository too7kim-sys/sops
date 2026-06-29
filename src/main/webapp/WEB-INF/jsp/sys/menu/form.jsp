<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="메뉴 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${menuVO.menuId == null}"/>

<div class="breadcrumb">기준정보 관리 &gt; 메뉴관리 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '메뉴 등록' : '메뉴 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/sys/menu/insert' : '/sys/menu/update'}">
    <input type="hidden" name="menuId" value="${menuVO.menuId}"/>
    <div class="panel">
        <table class="form">
            <tr>
                <th>메뉴명 <span class="required">*</span></th>
                <td><input type="text" name="menuNm" value="${menuVO.menuNm}" required/></td>
                <th>유형 <span class="required">*</span></th>
                <td>
                    <select name="menuType" id="menuType" required>
                        <option value="ITEM"  ${menuVO.menuType == 'GROUP' ? '' : 'selected'}>메뉴(ITEM)</option>
                        <option value="GROUP" ${menuVO.menuType == 'GROUP' ? 'selected' : ''}>그룹(GROUP)</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>URL</th>
                <td><input type="text" name="menuUrl" value="${menuVO.menuUrl}" placeholder="예) /csr/list (그룹은 비움)"/></td>
                <th>활성키</th>
                <td><input type="text" name="menuKey" value="${menuVO.menuKey}" placeholder="예) csr (활성표시용)"/></td>
            </tr>
            <tr>
                <th>상위 그룹</th>
                <td>
                    <select name="upperId">
                        <option value="">(최상위)</option>
                        <c:forEach var="g" items="${groupList}">
                            <c:if test="${g.menuId != menuVO.menuId}">
                            <option value="${g.menuId}" ${g.menuId == menuVO.upperId ? 'selected' : ''}>${g.menuNm}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </td>
                <th>정렬순서</th>
                <td><input type="number" name="sortOrdr" value="${empty menuVO.sortOrdr ? 100 : menuVO.sortOrdr}"/></td>
            </tr>
            <tr>
                <th>사용여부 <span class="required">*</span></th>
                <td colspan="3">
                    <select name="useAt" required>
                        <option value="Y" ${(menuVO.useAt == 'Y' or empty menuVO.useAt) ? 'selected' : ''}>Y</option>
                        <option value="N" ${menuVO.useAt == 'N' ? 'selected' : ''}>N</option>
                    </select>
                </td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/sys/menu/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

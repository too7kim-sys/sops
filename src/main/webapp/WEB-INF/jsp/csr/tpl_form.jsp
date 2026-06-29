<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="요청내용 템플릿 편집"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 요청내용 템플릿 &gt; 편집</div>
<div class="page-head">
    <div class="page-title">요청내용 템플릿 편집</div>
</div>

<form method="post" action="${ctx}/csr/tpl/save">
    <input type="hidden" name="subType" value="${tpl.subType}"/>
    <div class="panel">
        <table class="form">
            <tr>
                <th>요청 분류</th>
                <td colspan="3">
                    <span class="badge">${tpl.csrTypeNm}</span>
                    &nbsp;&gt;&nbsp;<b>${tpl.subTypeNm}</b>
                    <span class="h-meta">(코드: ${tpl.subType})</span>
                </td>
            </tr>
            <tr>
                <th>사용여부 <span class="required">*</span></th>
                <td colspan="3">
                    <select name="useAt" required>
                        <option value="Y" ${(tpl.useAt == 'Y' or empty tpl.useAt) ? 'selected' : ''}>Y</option>
                        <option value="N" ${tpl.useAt == 'N' ? 'selected' : ''}>N</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>요청내용 템플릿</th>
                <td colspan="3">
                    <textarea class="wysiwyg" name="content" rows="8" placeholder="요청 등록 시 기본으로 채워질 내용 양식을 작성하세요.">${tpl.content}</textarea>
                </td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/csr/tpl" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

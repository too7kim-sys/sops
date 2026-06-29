<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="권한관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 권한관리</div>
<div class="page-head">
    <div>
        <div class="page-title">권한관리</div>
        <div class="page-desc">역할별로 노출할 메뉴를 지정합니다. 저장 즉시 해당 역할 사용자의 메뉴에 반영됩니다.</div>
    </div>
</div>

<div class="panel">
    <div class="toolbar" style="gap:6px;">
        <c:forEach var="r" items="${roles}">
            <a href="${ctx}/sys/auth?role=${r.key}" class="btn ${r.key == selectedRole ? 'btn-primary' : 'btn-default'} btn-sm">${r.value}</a>
        </c:forEach>
        <span class="h-meta" style="margin-left:8px;">선택 역할 : <b>${roles[selectedRole]}</b> (${selectedRole})</span>
    </div>
</div>

<form method="post" action="${ctx}/sys/auth/save">
    <input type="hidden" name="role" value="${selectedRole}"/>
    <div class="panel">
        <table class="form">
            <c:forEach var="m" items="${menuList}">
                <c:choose>
                    <c:when test="${m.menuType == 'GROUP'}">
                        <tr><th colspan="2" style="background:#eef2f7;color:#1b3a6b;">${m.menuNm}</th></tr>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <th style="width:200px;font-weight:400;">
                                <label style="cursor:pointer;display:flex;align-items:center;gap:8px;">
                                    <input type="checkbox" name="menuIds" value="${m.menuId}" ${m.granted ? 'checked' : ''} style="width:auto;"/>
                                    ${m.menuNm}
                                </label>
                            </th>
                            <td><span class="h-meta">${empty m.menuUrl ? '-' : m.menuUrl}</span></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </table>
        <div class="h-meta" style="margin-top:8px;">※ 그룹은 하위 메뉴가 1개 이상 허용될 때 자동으로 노출됩니다.</div>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <button type="submit" class="btn btn-primary">권한 저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

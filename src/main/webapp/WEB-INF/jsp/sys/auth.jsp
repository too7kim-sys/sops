<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="권한관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 권한관리</div>
<div class="page-head">
    <div>
        <div class="page-title">권한관리</div>
        <div class="page-desc">역할을 등록·관리하고, 역할별로 노출할 메뉴를 지정합니다. 저장 즉시 해당 역할 사용자에게 반영됩니다.</div>
    </div>
</div>

<c:if test="${not empty roleMsg}">
    <div class="panel" style="border-left:4px solid #c0392b;background:#fdf2f2;color:#922;">${roleMsg}</div>
</c:if>

<%-- ================= 역할별 메뉴 권한 ================= --%>
<div class="panel">
    <div class="toolbar" style="gap:6px;align-items:center;">
        <c:forEach var="r" items="${roles}">
            <a href="${ctx}/sys/auth?role=${r.key}" class="btn ${r.key == selectedRole ? 'btn-primary' : 'btn-default'} btn-sm">${r.value}</a>
        </c:forEach>
        <button type="button" class="btn btn-default btn-sm" onclick="openRolePopup()" style="margin-left:8px;">⚙ 역할 관리</button>
        <c:if test="${not empty selectedRole}">
            <span class="h-meta" style="margin-left:8px;">메뉴 권한 대상 : <b>${roles[selectedRole]}</b> (${selectedRole})</span>
        </c:if>
    </div>
</div>

<script>
    function openRolePopup() {
        var w = window.open('${ctx}/sys/role/popup', 'rolePopup',
            'width=720,height=620,scrollbars=yes,resizable=yes');
        if (w) { w.focus(); }
    }
</script>

<c:choose>
<c:when test="${empty selectedRole}">
    <div class="panel"><div class="empty">사용중인 역할이 없습니다. 위에서 역할을 등록하세요.</div></div>
</c:when>
<c:otherwise>
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
</c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

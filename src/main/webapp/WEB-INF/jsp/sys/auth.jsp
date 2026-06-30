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

<%-- ================= 역할 관리(등록/수정/삭제) ================= --%>
<div class="panel">
    <h3>역할 관리</h3>
    <%-- 행마다 폼이 셀을 가로지르지 않도록 폼은 표 밖에 선언하고 input 은 form= 속성으로 연결 --%>
    <c:forEach var="r" items="${roleList}">
        <form id="roleSave_${r.roleCd}" method="post" action="${ctx}/sys/role/save"></form>
        <c:if test="${r.builtin != 'Y' and (empty r.userCnt or r.userCnt == 0)}">
        <form id="roleDel_${r.roleCd}" method="post" action="${ctx}/sys/role/delete"
              onsubmit="return confirm('역할 [${r.roleCd}] 을(를) 삭제할까요? 역할의 메뉴권한도 함께 삭제됩니다.');">
            <input type="hidden" name="roleCd" value="${r.roleCd}"/>
        </form>
        </c:if>
    </c:forEach>
    <table class="list">
        <thead>
        <tr>
            <th style="width:160px;">역할코드</th>
            <th>역할명</th>
            <th class="center" style="width:90px;">정렬</th>
            <th class="center" style="width:110px;">사용여부</th>
            <th class="center" style="width:90px;">사용자수</th>
            <th class="center" style="width:150px;">관리</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${roleList}">
            <tr>
                <td>
                    <input type="hidden" name="roleCd" value="${r.roleCd}" form="roleSave_${r.roleCd}"/>
                    <b>${r.roleCd}</b>
                    <c:if test="${r.builtin == 'Y'}"><span class="badge" style="margin-left:6px;">내장</span></c:if>
                </td>
                <td><input type="text" name="roleNm" value="${r.roleNm}" required style="width:100%;" form="roleSave_${r.roleCd}"/></td>
                <td class="center"><input type="number" name="sortNo" value="${r.sortNo}" style="width:70px;text-align:center;" form="roleSave_${r.roleCd}"/></td>
                <td class="center">
                    <select name="useAt" style="width:auto;" form="roleSave_${r.roleCd}">
                        <option value="Y" ${r.useAt == 'Y' ? 'selected' : ''}>사용</option>
                        <option value="N" ${r.useAt == 'N' ? 'selected' : ''}>미사용</option>
                    </select>
                </td>
                <td class="center">${r.userCnt}</td>
                <td class="center" style="white-space:nowrap;">
                    <button type="submit" class="btn btn-primary btn-sm" form="roleSave_${r.roleCd}">저장</button>
                    <c:if test="${r.builtin != 'Y' and (empty r.userCnt or r.userCnt == 0)}">
                        <button type="submit" class="btn btn-danger btn-sm" form="roleDel_${r.roleCd}">삭제</button>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <%-- 신규 역할 등록 --%>
    <form method="post" action="${ctx}/sys/role/save" class="appr-add-row" style="margin-top:12px;align-items:center;gap:8px;flex-wrap:wrap;">
        <input type="text" name="roleCd" placeholder="역할코드(영문 대문자, 예: AUDITOR)" required style="width:240px;"/>
        <input type="text" name="roleNm" placeholder="역할명" required style="width:200px;"/>
        <input type="number" name="sortNo" placeholder="정렬" value="50" style="width:80px;"/>
        <button type="submit" class="btn btn-primary btn-sm">＋ 역할 등록</button>
        <span class="h-meta">코드는 사용자 권한(ROLE)·메뉴권한과 매핑됩니다. 등록 후 아래에서 메뉴 권한을 지정하세요.</span>
    </form>
</div>

<%-- ================= 역할별 메뉴 권한 ================= --%>
<div class="panel">
    <div class="toolbar" style="gap:6px;">
        <c:forEach var="r" items="${roles}">
            <a href="${ctx}/sys/auth?role=${r.key}" class="btn ${r.key == selectedRole ? 'btn-primary' : 'btn-default'} btn-sm">${r.value}</a>
        </c:forEach>
        <c:if test="${not empty selectedRole}">
            <span class="h-meta" style="margin-left:8px;">메뉴 권한 대상 : <b>${roles[selectedRole]}</b> (${selectedRole})</span>
        </c:if>
    </div>
</div>

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

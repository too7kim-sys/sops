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

<%-- ================= 역할별 메뉴 권한 ================= --%>
<div class="panel">
    <div class="toolbar" style="gap:6px;align-items:center;">
        <c:forEach var="r" items="${roles}">
            <a href="${ctx}/sys/auth?role=${r.key}" class="btn ${r.key == selectedRole ? 'btn-primary' : 'btn-default'} btn-sm">${r.value}</a>
        </c:forEach>
        <button type="button" class="btn btn-default btn-sm" onclick="openRoleLayer()" style="margin-left:8px;">⚙ 역할 관리</button>
        <c:if test="${not empty selectedRole}">
            <span class="h-meta" style="margin-left:8px;">메뉴 권한 대상 : <b>${roles[selectedRole]}</b> (${selectedRole})</span>
        </c:if>
    </div>
</div>

<c:choose>
<c:when test="${empty selectedRole}">
    <div class="panel"><div class="empty">사용중인 역할이 없습니다. [역할 관리] 에서 역할을 등록하세요.</div></div>
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

<%-- ================= 역할 관리 레이어 팝업 ================= --%>
<div id="roleLayer" class="modal-overlay" style="display:none;">
    <div class="modal-box" style="width:760px;max-width:96vw;">
        <div class="modal-head">
            <b>역할 관리</b>
            <button type="button" class="modal-x" onclick="closeRoleLayer()">×</button>
        </div>
        <div class="modal-body" style="max-height:70vh;overflow:auto;">
            <c:if test="${not empty roleMsg}">
                <div style="border-left:4px solid #c0392b;background:#fdf2f2;color:#922;padding:8px 10px;margin-bottom:12px;border-radius:4px;">${roleMsg}</div>
            </c:if>

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

            <table class="list role-mng">
                <thead>
                <tr>
                    <th style="width:150px;">역할코드</th>
                    <th>역할명</th>
                    <th class="center" style="width:64px;">정렬</th>
                    <th class="center" style="width:92px;">사용여부</th>
                    <th class="center" style="width:64px;">사용자</th>
                    <th class="center" style="width:120px;">관리</th>
                </tr>
                </thead>
                <tbody>
                <c:if test="${empty roleList}"><tr><td colspan="6" class="empty">등록된 역할이 없습니다.</td></tr></c:if>
                <c:forEach var="r" items="${roleList}">
                    <tr>
                        <td style="white-space:nowrap;">
                            <input type="hidden" name="roleCd" value="${r.roleCd}" form="roleSave_${r.roleCd}"/>
                            <b>${r.roleCd}</b>
                            <c:if test="${r.builtin == 'Y'}"><span class="badge" style="margin-left:4px;">내장</span></c:if>
                        </td>
                        <td><input type="text" name="roleNm" value="${r.roleNm}" required style="width:100%;" form="roleSave_${r.roleCd}"/></td>
                        <td class="center"><input type="number" name="sortNo" value="${r.sortNo}" style="width:52px;text-align:center;" form="roleSave_${r.roleCd}"/></td>
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
            <form method="post" action="${ctx}/sys/role/save" style="margin-top:14px;padding-top:14px;border-top:1px solid #e7ebf0;display:flex;align-items:center;gap:8px;flex-wrap:wrap;">
                <input type="text" name="roleCd" placeholder="역할코드(예: AUDITOR)" required style="width:190px;"/>
                <input type="text" name="roleNm" placeholder="역할명" required style="width:160px;"/>
                <input type="number" name="sortNo" placeholder="정렬" value="50" style="width:70px;"/>
                <button type="submit" class="btn btn-primary btn-sm">＋ 역할 등록</button>
            </form>
            <div class="h-meta" style="margin-top:8px;">※ 역할코드는 사용자 권한·메뉴권한과 매핑됩니다. 메뉴 권한 지정은 닫은 뒤 권한관리 화면에서 합니다.</div>
        </div>
        <div class="modal-foot">
            <button type="button" class="btn btn-default btn-sm" onclick="closeRoleLayer()">닫기</button>
        </div>
    </div>
</div>

<script>
    function openRoleLayer()  { document.getElementById('roleLayer').style.display = 'flex'; }
    function closeRoleLayer() { document.getElementById('roleLayer').style.display = 'none'; }
    (function () {
        var layer = document.getElementById('roleLayer');
        // 배경 클릭 시 닫기 / ESC 닫기
        layer.addEventListener('click', function (e) { if (e.target === layer) { closeRoleLayer(); } });
        document.addEventListener('keydown', function (e) { if (e.key === 'Escape') { closeRoleLayer(); } });
        // 역할 처리 후 복귀(roleLayer=1)면 레이어 자동 오픈
        if ('${param.roleLayer}' === '1') { openRoleLayer(); }
    })();
</script>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

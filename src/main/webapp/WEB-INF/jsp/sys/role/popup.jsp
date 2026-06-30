<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>역할 관리</title>
    <link rel="stylesheet" href="${ctx}/css/style.css"/>
    <style>
        body { padding: 16px; background: #fff; }
        .pop-head { font-size: 16px; font-weight: 700; color: #1b3a6b; margin-bottom: 6px; }
        .pop-desc { font-size: 12px; color: #777; margin-bottom: 12px; }
        table.list td { vertical-align: middle; }
        .pop-foot { margin-top: 14px; text-align: right; }
        .msg { border-left: 4px solid #c0392b; background: #fdf2f2; color: #922; padding: 8px 10px; margin-bottom: 12px; font-size: 13px; }
    </style>
</head>
<body>
    <div class="pop-head">역할 관리</div>
    <div class="pop-desc">역할(권한 그룹)을 등록·수정·삭제합니다. 변경 후 창을 닫으면 권한관리 화면에 반영됩니다.</div>

    <c:if test="${not empty roleMsg}"><div class="msg">${roleMsg}</div></c:if>

    <%-- 행마다 폼이 셀을 가로지르지 않도록 폼은 표 밖에 선언하고 input 은 form= 속성으로 연결 --%>
    <c:forEach var="r" items="${roleList}">
        <form id="roleSave_${r.roleCd}" method="post" action="${ctx}/sys/role/save">
            <input type="hidden" name="from" value="popup"/>
        </form>
        <c:if test="${r.builtin != 'Y' and (empty r.userCnt or r.userCnt == 0)}">
        <form id="roleDel_${r.roleCd}" method="post" action="${ctx}/sys/role/delete"
              onsubmit="return confirm('역할 [${r.roleCd}] 을(를) 삭제할까요? 역할의 메뉴권한도 함께 삭제됩니다.');">
            <input type="hidden" name="from" value="popup"/>
            <input type="hidden" name="roleCd" value="${r.roleCd}"/>
        </form>
        </c:if>
    </c:forEach>

    <table class="list">
        <thead>
        <tr>
            <th style="width:130px;">역할코드</th>
            <th>역할명</th>
            <th class="center" style="width:70px;">정렬</th>
            <th class="center" style="width:96px;">사용여부</th>
            <th class="center" style="width:70px;">사용자</th>
            <th class="center" style="width:130px;">관리</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty roleList}"><tr><td colspan="6" class="empty">등록된 역할이 없습니다.</td></tr></c:if>
        <c:forEach var="r" items="${roleList}">
            <tr>
                <td>
                    <input type="hidden" name="roleCd" value="${r.roleCd}" form="roleSave_${r.roleCd}"/>
                    <b>${r.roleCd}</b>
                    <c:if test="${r.builtin == 'Y'}"><span class="badge" style="margin-left:4px;">내장</span></c:if>
                </td>
                <td><input type="text" name="roleNm" value="${r.roleNm}" required style="width:100%;" form="roleSave_${r.roleCd}"/></td>
                <td class="center"><input type="number" name="sortNo" value="${r.sortNo}" style="width:56px;text-align:center;" form="roleSave_${r.roleCd}"/></td>
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
        <input type="hidden" name="from" value="popup"/>
        <input type="text" name="roleCd" placeholder="역할코드(예: AUDITOR)" required style="width:200px;"/>
        <input type="text" name="roleNm" placeholder="역할명" required style="width:160px;"/>
        <input type="number" name="sortNo" placeholder="정렬" value="50" style="width:70px;"/>
        <button type="submit" class="btn btn-primary btn-sm">＋ 역할 등록</button>
    </form>
    <div class="pop-desc" style="margin-top:8px;">※ 역할코드는 사용자 권한·메뉴권한과 매핑됩니다. 메뉴 권한 지정은 권한관리 화면에서 합니다.</div>

    <div class="pop-foot">
        <button type="button" class="btn btn-default btn-sm" onclick="closePopup()">닫기</button>
    </div>

    <script>
        // 변경이 있었으면(저장/삭제 후 리다이렉트) 창 닫을 때 부모(권한관리) 새로고침
        var CHANGED = '${param.changed}' === '1';
        function refreshOpener() {
            if (CHANGED && window.opener && !window.opener.closed) {
                try { window.opener.location.reload(); } catch (e) {}
            }
        }
        function closePopup() { refreshOpener(); window.close(); }
        window.addEventListener('beforeunload', refreshOpener);
    </script>
</body>
</html>

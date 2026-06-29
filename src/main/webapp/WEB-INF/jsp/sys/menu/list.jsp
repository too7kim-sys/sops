<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="메뉴관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 메뉴관리</div>
<div class="page-head">
    <div>
        <div class="page-title">메뉴관리</div>
        <div class="page-desc">사이드바 메뉴 구성을 관리합니다. 역할별 노출은 <b>권한관리</b>에서 설정합니다.</div>
    </div>
    <div class="toolbar">
        <button type="button" class="btn btn-default" onclick="exportListExcel()">⬇ 엑셀</button>
        <a href="${ctx}/sys/menu/write" class="btn btn-primary">＋ 메뉴 등록</a>
    </div>
</div>

<div class="panel mb0">
    <div class="list-scroll"><table class="list">
        <thead>
        <tr>
            <th style="width:60px;" class="center">ID</th>
            <th>메뉴명</th>
            <th style="width:180px;">URL</th>
            <th style="width:100px;">키</th>
            <th style="width:80px;" class="center">유형</th>
            <th style="width:140px;">상위</th>
            <th style="width:60px;" class="center">정렬</th>
            <th style="width:60px;" class="center">사용</th>
            <th style="width:60px;" class="center"></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty menuList}"><tr><td colspan="9" class="empty">등록된 메뉴가 없습니다.</td></tr></c:if>
        <c:forEach var="m" items="${menuList}">
            <tr>
                <td class="center">${m.menuId}</td>
                <td><c:if test="${not empty m.upperId}"><span style="color:#bbb;">└ </span></c:if><a href="${ctx}/sys/menu/edit/${m.menuId}">${m.menuNm}</a></td>
                <td>${empty m.menuUrl ? '-' : m.menuUrl}</td>
                <td>${empty m.menuKey ? '-' : m.menuKey}</td>
                <td class="center"><span class="badge ${m.menuType == 'GROUP' ? 'lt-review' : 'lt-handle'}">${m.menuType == 'GROUP' ? '그룹' : '메뉴'}</span></td>
                <td>${empty m.upperNm ? '-' : m.upperNm}</td>
                <td class="center">${m.sortOrdr}</td>
                <td class="center"><span class="badge ${m.useAt == 'N' ? 'st-pending' : 'st-approved'}">${m.useAt}</span></td>
                <td class="center">
                    <form method="post" action="${ctx}/sys/menu/delete/${m.menuId}" onsubmit="return confirm('삭제할까요? (해당 메뉴의 권한도 함께 삭제)');" style="display:inline;">
                        <button type="submit" class="btn btn-danger btn-sm">×</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table></div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

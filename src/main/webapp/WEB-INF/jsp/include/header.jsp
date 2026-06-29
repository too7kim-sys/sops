<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${empty pageTitle ? 'eGov-Sop' : pageTitle} - 범정부 응용프로그램 운영관리시스템</title>
    <link rel="stylesheet" href="${ctx}/css/style.css"/>
</head>
<body>
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand" href="${ctx}/main">
            <span class="brand-logo-box">
                <img class="brand-logo" src="${ctx}/images/logo.png" alt="로고"
                     onerror="this.onerror=null;this.src='${ctx}/images/logo.svg';"/>
            </span>
            <span class="brand-name">범정부 응용프로그램 운영관리시스템</span>
        </a>
        <div class="topbar-right">
            <span class="user-info">
                <strong><sec:authentication property="principal.userNm"/></strong>님
                <span class="role-badge"><sec:authentication property="principal.user.role"/></span>
            </span>
            <form action="${ctx}/logout" method="post" class="logout-form">
                <button type="submit" class="btn btn-ghost btn-sm">로그아웃</button>
            </form>
        </div>
    </div>
</header>
<div class="layout">
    <nav class="sidebar">
        <ul class="menu">
            <%-- 메뉴/권한관리(OPS_MENU·OPS_MENU_AUTH) 기반 동적 메뉴 : 로그인 역할에 따라 노출 --%>
            <c:forEach var="m" items="${navMenus}">
                <c:choose>
                    <c:when test="${m.menuType == 'GROUP'}">
                        <li class="menu-group">${m.menuNm}</li>
                        <c:forEach var="c" items="${m.children}">
                            <li><a href="${ctx}${c.menuUrl}" class="${menu == c.menuKey ? 'active' : ''}">${c.menuNm}<c:if test="${c.menuKey == 'shared' and sharedUnreadCnt > 0}"> <span class="badge st-rejected">${sharedUnreadCnt}</span></c:if></a></li>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${ctx}${m.menuUrl}" class="${menu == m.menuKey ? 'active' : ''}">${m.menuNm}<c:if test="${m.menuKey == 'shared' and sharedUnreadCnt > 0}"> <span class="badge st-rejected">${sharedUnreadCnt}</span></c:if></a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ul>
        <div class="sidebar-foot">전자정부 표준프레임워크 기반</div>
    </nav>
    <main class="content">

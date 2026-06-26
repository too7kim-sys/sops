<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${empty pageTitle ? 'eGov-Ops' : pageTitle} - 범정부 응용프로그램 운영관리시스템</title>
    <link rel="stylesheet" href="${ctx}/css/style.css"/>
</head>
<body>
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand" href="${ctx}/main">
            <span class="brand-mark">eGov</span>
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
            <li><a href="${ctx}/main" class="${menu=='dashboard' ? 'active' : ''}">운영현황</a></li>
            <li class="menu-group">표준운영절차</li>
            <li><a href="${ctx}/csr/list"       class="${menu=='csr' ? 'active' : ''}">1. 요청관리</a></li>
            <li><a href="${ctx}/change/list"    class="${menu=='change' ? 'active' : ''}">2. 변경관리</a></li>
            <li><a href="${ctx}/release/list"   class="${menu=='release' ? 'active' : ''}">3. 배포관리</a></li>
            <li><a href="${ctx}/test/list"      class="${menu=='test' ? 'active' : ''}">4. 테스트관리</a></li>
            <li><a href="${ctx}/interface/list" class="${menu=='interface' ? 'active' : ''}">5. 연계관리</a></li>
            <li><a href="${ctx}/ci/list"        class="${menu=='ci' ? 'active' : ''}">6. 형상관리</a></li>
            <li><a href="${ctx}/event/list"     class="${menu=='event' ? 'active' : ''}">7. 운영상태관리</a></li>
            <li><a href="${ctx}/incident/list"  class="${menu=='incident' ? 'active' : ''}">8. 장애관리</a></li>
            <li><a href="${ctx}/problem/list"   class="${menu=='problem' ? 'active' : ''}">9. 문제관리</a></li>
            <li><a href="${ctx}/check/list"     class="${menu=='check' ? 'active' : ''}">· 운영점검</a></li>
            <li class="menu-group">기준정보 관리</li>
            <li><a href="${ctx}/system/list"    class="${menu=='system' ? 'active' : ''}">응용시스템</a></li>
            <sec:authorize access="hasRole('ADMIN')">
            <li><a href="${ctx}/sys/user/list"  class="${menu=='user' ? 'active' : ''}">사용자관리</a></li>
            <li><a href="${ctx}/sys/code/list"  class="${menu=='code' ? 'active' : ''}">공통코드</a></li>
            </sec:authorize>
        </ul>
        <div class="sidebar-foot">전자정부 표준프레임워크 기반</div>
    </nav>
    <main class="content">

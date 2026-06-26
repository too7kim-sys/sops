<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>로그인 - 범정부 응용프로그램 운영관리시스템</title>
    <link rel="stylesheet" href="${ctx}/css/style.css"/>
</head>
<body>
<div class="login-wrap">
    <div class="login-box">
        <h1>범정부 응용프로그램<br/>운영관리시스템</h1>
        <p class="sub">전자정부 표준프레임워크 · 표준운영절차</p>

        <c:if test="${param.error != null}">
            <div class="login-err">아이디 또는 비밀번호가 올바르지 않습니다.</div>
        </c:if>
        <c:if test="${param.logout != null}">
            <div class="login-info">정상적으로 로그아웃되었습니다.</div>
        </c:if>

        <form action="${ctx}/login" method="post">
            <div class="field">
                <label for="userId">아이디</label>
                <input type="text" id="userId" name="userId" autofocus required/>
            </div>
            <div class="field">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" required/>
            </div>
            <button type="submit" class="btn btn-primary">로그인</button>
        </form>

        <div class="login-help">
            데모 계정 : admin / admin123!<br/>
            (운영자) oper01 / oper123!
        </div>
    </div>
</div>
</body>
</html>

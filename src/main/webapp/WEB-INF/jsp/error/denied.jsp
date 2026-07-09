<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="접근 권한 없음"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="panel" style="text-align:center; padding:60px 30px;">
    <div style="font-size:46px; color:#c0392b; margin-bottom:14px;">⛔</div>
    <h2 style="color:#1b3a6b; margin-bottom:10px;">접근 권한이 없습니다</h2>
    <p style="color:#667; line-height:1.8;">
        해당 업무의 <b>요청자·처리자·검토자·승인자·심의자</b> 또는 <b>공유 대상자</b>만 열람·처리할 수 있습니다.<br/>
        접근이 필요하면 운영관리자에게 공유를 요청하세요. (운영관리자는 전체 열람 가능)
    </p>
    <div class="toolbar" style="justify-content:center; margin-top:22px;">
        <%-- 이전 페이지 이동은 브라우저 히스토리로만 처리(서버 리다이렉트·Referer 미사용) → 오픈리다이렉트/XSS 방지 --%>
        <button type="button" class="btn btn-default" onclick="if(history.length>1){history.back();}else{location.href='${ctx}/main';}">＜ 이전 페이지로</button>
        <a href="${ctx}/main" class="btn btn-primary">운영현황으로</a>
        <a href="${ctx}/appr/shared" class="btn btn-default">공유함</a>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

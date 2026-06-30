<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>사용자 검색</title>
    <link rel="stylesheet" href="${ctx}/css/style.css"/>
    <style>
        body { padding: 16px; background: #fff; }
        .pop-head { font-size: 16px; font-weight: 700; color: #1b3a6b; margin-bottom: 12px; }
        .pop-search { display: flex; gap: 8px; margin-bottom: 12px; }
        .pop-search input[type=text] { flex: 1; }
        table.list td { vertical-align: middle; }
    </style>
</head>
<body>
    <div class="pop-head">사용자 검색</div>

    <form method="get" action="${ctx}/sys/user/popup" class="pop-search">
        <input type="hidden" name="prefix" value="${prefix}"/>
        <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}"
               placeholder="성명 또는 ID 입력 후 Enter" autofocus/>
        <button type="submit" class="btn btn-primary btn-sm">검색</button>
    </form>

    <table class="list">
        <thead>
        <tr>
            <th style="width:120px;">ID</th>
            <th>성명</th>
            <th style="width:90px;">직급</th>
            <th style="width:130px;">부서</th>
            <th style="width:70px;"></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty userList}"><tr><td colspan="5" class="empty">검색 결과가 없습니다.</td></tr></c:if>
        <c:forEach var="u" items="${userList}">
            <tr>
                <td>${u.userId}</td>
                <td>${u.userNm}</td>
                <td>${empty u.positn ? '-' : u.positn}</td>
                <td>${empty u.deptNm ? '-' : u.deptNm}</td>
                <td>
                    <button type="button" class="btn btn-primary btn-sm"
                            data-id="${u.userId}" data-nm="${u.userNm}" onclick="pick(this)">선택</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <script>
        function pick(el) {
            if (window.opener && !window.opener.closed && window.opener.applyUser) {
                window.opener.applyUser('${prefix}', el.getAttribute('data-id'), el.getAttribute('data-nm'));
            }
            window.close();
        }
    </script>
</body>
</html>

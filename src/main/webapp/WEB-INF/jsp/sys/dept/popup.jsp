<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>부서 검색</title>
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
    <div class="pop-head">부서 검색</div>

    <form method="get" action="${ctx}/sys/dept/popup" class="pop-search">
        <input type="hidden" name="prefix" value="${prefix}"/>
        <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}"
               placeholder="부서명 또는 코드 입력 후 Enter" autofocus/>
        <button type="submit" class="btn btn-primary btn-sm">검색</button>
    </form>

    <table class="list">
        <thead>
        <tr>
            <th style="width:90px;">코드</th>
            <th>부서명</th>
            <th style="width:130px;">상위부서</th>
            <th style="width:70px;"></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty deptList}"><tr><td colspan="4" class="empty">검색 결과가 없습니다.</td></tr></c:if>
        <c:forEach var="d" items="${deptList}">
            <tr>
                <td>${empty d.deptCd ? '-' : d.deptCd}</td>
                <td>${d.deptNm}</td>
                <td>${empty d.upperNm ? '-' : d.upperNm}</td>
                <td>
                    <button type="button" class="btn btn-primary btn-sm"
                            data-id="${d.deptId}" data-cd="${d.deptCd}" data-nm="${d.deptNm}"
                            onclick="pick(this)">선택</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <script>
        function pick(el) {
            if (window.opener && !window.opener.closed && window.opener.applyDept) {
                window.opener.applyDept('${prefix}',
                    el.getAttribute('data-id'), el.getAttribute('data-cd'), el.getAttribute('data-nm'));
            }
            window.close();
        }
    </script>
</body>
</html>

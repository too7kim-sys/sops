<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="부서관리"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 부서관리</div>
<div class="page-head">
    <div>
        <div class="page-title">부서관리</div>
        <div class="page-desc">운영 조직(부서) 마스터 관리 — 결재선/공유 대상 지정에 사용</div>
    </div>
    <div class="toolbar">
        <a href="${ctx}/sys/dept/write" class="btn btn-primary">＋ 부서 등록</a>
    </div>
</div>

<form method="get" action="${ctx}/sys/dept/list" class="searchbar">
    <select name="searchStatus">
        <option value="">전체 사용여부</option>
        <option value="Y" ${searchVO.searchStatus == 'Y' ? 'selected' : ''}>사용</option>
        <option value="N" ${searchVO.searchStatus == 'N' ? 'selected' : ''}>미사용</option>
    </select>
    <input type="text" name="searchKeyword" value="${searchVO.searchKeyword}" placeholder="부서명/코드 검색"/>
    <button type="submit" class="btn btn-default">검색</button>
</form>

<div class="panel mb0">
    <p style="margin-bottom:10px;color:#777;font-size:13px;">총 <b>${totalCnt}</b>개</p>
    <table class="list">
        <thead>
        <tr>
            <th class="center" style="width:90px;">부서코드</th>
            <th>부서명</th>
            <th style="width:140px;">상위부서</th>
            <th class="center" style="width:110px;">부서장</th>
            <th class="center" style="width:70px;">인원</th>
            <th class="center" style="width:60px;">정렬</th>
            <th class="center" style="width:70px;">사용</th>
            <th class="center" style="width:120px;">관리</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="d" items="${deptList}">
            <tr>
                <td class="center">${empty d.deptCd ? '-' : d.deptCd}</td>
                <td><a href="${ctx}/sys/dept/edit/${d.deptId}">${d.deptNm}</a></td>
                <td>${empty d.upperNm ? '-' : d.upperNm}</td>
                <td class="center">${empty d.mngrNm ? '-' : d.mngrNm}</td>
                <td class="center">${d.userCnt}</td>
                <td class="center">${d.sortOrdr}</td>
                <td class="center"><span class="badge ${d.useAt == 'Y' ? 'st-approved' : 'st-closed'}">${d.useAt == 'Y' ? '사용' : '미사용'}</span></td>
                <td class="center">
                    <a href="${ctx}/sys/dept/edit/${d.deptId}" class="btn btn-default btn-sm">수정</a>
                    <form action="${ctx}/sys/dept/delete/${d.deptId}" method="post" style="display:inline;"
                          onsubmit="return confirm('삭제하시겠습니까?');">
                        <button type="submit" class="btn btn-danger btn-sm">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty deptList}"><tr><td colspan="8" class="empty">등록된 부서가 없습니다.</td></tr></c:if>
        </tbody>
    </table>
    <jsp:include page="/WEB-INF/jsp/include/paging.jsp"><jsp:param name="baseUrl" value="/sys/dept/list"/></jsp:include>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

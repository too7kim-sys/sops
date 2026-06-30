<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="요청내용 템플릿"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">기준정보 관리 &gt; 요청내용 템플릿</div>
<div class="page-head">
    <div>
        <div class="page-title">요청내용 템플릿</div>
        <div class="page-desc">요청 소분류별로 요청내용 기본 양식을 관리합니다. 요청 등록 시 소분류 선택에 따라 자동 표시됩니다.</div>
    </div>
</div>

<div class="panel">
    <table class="list">
        <thead>
        <tr>
            <th style="width:110px;">대분류</th>
            <th style="width:170px;">소분류</th>
            <th>요청내용 템플릿</th>
            <th class="center" style="width:100px;">완료요구<br/>소요일</th>
            <th class="center" style="width:70px;">사용</th>
            <th class="center" style="width:80px;"></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty tplList}"><tr><td colspan="6" class="empty">등록된 소분류가 없습니다.</td></tr></c:if>
        <c:forEach var="t" items="${tplList}">
            <tr>
                <td><span class="badge">${t.csrTypeNm}</span></td>
                <td>${t.subTypeNm}</td>
                <td>
                    <c:choose>
                        <c:when test="${empty t.content}"><span class="h-meta">— 미등록 —</span></c:when>
                        <c:otherwise><div class="rte-view">${t.content}</div></c:otherwise>
                    </c:choose>
                </td>
                <td class="center"><c:choose><c:when test="${empty t.leadDays}">-</c:when><c:otherwise>${t.leadDays} 근무일</c:otherwise></c:choose></td>
                <td class="center">
                    <span class="badge ${t.useAt == 'N' ? 'st-pending' : 'st-approved'}">${t.useAt == 'N' ? 'N' : 'Y'}</span>
                </td>
                <td class="center">
                    <a href="${ctx}/csr/tpl/edit?subType=${t.subType}" class="btn btn-default btn-sm">편집</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="h-meta" style="margin-top:8px;">※ 소분류 코드 자체(추가/명칭)는 <b>공통코드</b> 관리(CSR_SUBTYPE)에서 관리합니다.</div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

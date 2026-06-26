<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="연계 요청"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 연계관리 &gt; 요청</div>
<div class="page-head">
    <div class="page-title">연계 요청</div>
</div>

<form method="post" action="${ctx}/interface/insert">
    <div class="panel">
        <h3>연계 정보</h3>
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == interface.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>연계 유형 <span class="required">*</span></th>
                <td>
                    <select name="ifType" required>
                        <option value="">선택</option>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == interface.ifType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${interface.title}" required/></td>
            </tr>
            <tr>
                <th>상대 시스템</th>
                <td><input type="text" name="partnerSys" value="${interface.partnerSys}" placeholder="연계 상대 시스템"/></td>
                <th>예정일</th>
                <td><input type="date" name="planDt" value="${interface.planDt}"/></td>
            </tr>
            <tr>
                <th>연계 데이터</th>
                <td colspan="3"><textarea name="dataDesc" rows="5">${interface.dataDesc}</textarea></td>
            </tr>
        </table>
    </div>
    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/interface/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

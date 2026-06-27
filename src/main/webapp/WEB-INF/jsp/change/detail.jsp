<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="변경 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 변경관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">변경 상세 <span style="color:#888;font-weight:400;">(CHG-${change.chgId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/change/edit/${change.chgId}" class="btn btn-default">수정</a>
        <form action="${ctx}/change/delete/${change.chgId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>진행 단계 <span class="badge st-${fn:toLowerCase(change.status)}" style="margin-left:6px;">${change.statusNm}</span></h3>
    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
        <jsp:param name="type" value="CHANGE"/>
        <jsp:param name="status" value="${change.status}"/>
        <jsp:param name="statusNm" value="${change.statusNm}"/>
        <jsp:param name="mode" value="full"/>
    </jsp:include>
</div>

<div class="grid-2">
    <div class="panel">
        <h3>변경 정보</h3>
        <table class="form">
            <tr><th>대상 시스템</th><td>${change.sysNm}</td></tr>
            <tr><th>제목</th><td>${change.title}</td></tr>
            <tr><th>유형 / 상태</th><td>
                <span class="badge">${change.chgTypeNm}</span>
                &nbsp;
                <span class="badge st-${fn:toLowerCase(change.status)}">${change.statusNm}</span>
            </td></tr>
            <tr><th>요청자 / 요청일시</th><td>${empty change.reqId ? '-' : change.reqId} / ${empty change.reqDt ? '-' : change.reqDt}</td></tr>
            <tr><th>적용 예정일</th><td>${empty change.planDt ? '-' : change.planDt}</td></tr>
            <tr><th>적용 일시</th><td>${empty change.applyDt ? '-' : change.applyDt}</td></tr>
            <tr><th>변경 사유</th><td><div class="rte-view">${empty change.reason ? '-' : change.reason}</div></td></tr>
            <tr><th>변경 내용</th><td><div class="rte-view">${empty change.content ? '-' : change.content}</div></td></tr>
            <tr><th>심의자 / 심의일시</th><td>${empty change.apprId ? '-' : change.apprId} / ${empty change.apprDt ? '-' : change.apprDt}</td></tr>
            <tr><th>심의 의견</th><td><div class="rte-view">${empty change.apprOpinion ? '-' : change.apprOpinion}</div></td></tr>
        </table>
    </div>

    <c:if test="${change.status != 'COMPLETED' and change.status != 'REJECTED'}">
    <div>
        <%-- 심의/승인 --%>
        <div class="panel">
            <h3>심의/승인</h3>
            <form method="post" action="${ctx}/change/approve">
                <input type="hidden" name="chgId" value="${change.chgId}"/>
                <table class="form">
                    <tr><th>심의 결과 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <option value="REVIEWING">검토</option>
                                <option value="APPROVED">승인</option>
                                <option value="REJECTED">반려</option>
                            </select>
                        </td></tr>
                    <tr><th>심의 의견</th><td><textarea class="wysiwyg" name="apprOpinion" rows="3">${change.apprOpinion}</textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;">
                    <button type="submit" class="btn btn-success">심의 등록</button>
                </div>
            </form>
        </div>

        <%-- 적용 --%>
        <div class="panel">
            <h3>적용 처리</h3>
            <form method="post" action="${ctx}/change/apply">
                <input type="hidden" name="chgId" value="${change.chgId}"/>
                <table class="form">
                    <tr><th>적용 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <option value="APPLIED">적용</option>
                                <option value="COMPLETED">완료</option>
                            </select>
                        </td></tr>
                </table>
                <div class="right" style="margin-top:12px;">
                    <button type="submit" class="btn btn-primary">적용 등록</button>
                </div>
            </form>
        </div>
    </div>
    </c:if>
</div>

<%-- CAB(변경자문위원회) 심의이력 --%>
<div class="panel">
    <h3>CAB 심의이력</h3>
    <table class="list">
        <thead>
        <tr>
            <th style="width:120px;">심의결과</th>
            <th>의견</th>
            <th style="width:140px;">심의위원</th>
            <th style="width:150px;">심의일시</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty change.cabList}">
            <tr><td colspan="4" class="empty">등록된 CAB 심의이력이 없습니다.</td></tr>
        </c:if>
        <c:forEach var="cab" items="${change.cabList}">
            <tr>
                <td><span class="badge st-${fn:toLowerCase(cab.decision)}">${empty cab.decisionNm ? cab.decision : cab.decisionNm}</span></td>
                <td><div class="rte-view">${empty cab.opinion ? '-' : cab.opinion}</div></td>
                <td>${empty cab.reviewer ? '-' : cab.reviewer}</td>
                <td>${empty cab.cabDt ? '-' : cab.cabDt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <form method="post" action="${ctx}/change/cab" style="margin-top:12px;">
        <input type="hidden" name="chgId" value="${change.chgId}"/>
        <table class="form">
            <tr><th>심의 결과 <span class="required">*</span></th>
                <td>
                    <select name="decision" required>
                        <option value="">선택</option>
                        <c:forEach var="d" items="${cabDecisionList}">
                            <option value="${d.codeId}">${d.codeNm}</option>
                        </c:forEach>
                    </select>
                </td></tr>
            <tr><th>심의 의견</th><td><textarea class="wysiwyg" name="opinion" rows="3"></textarea></td></tr>
            <tr><th>심의위원</th><td><input type="text" name="reviewer" placeholder="미입력 시 로그인 사용자"/></td></tr>
        </table>
        <div class="right" style="margin-top:12px;">
            <button type="submit" class="btn btn-success">CAB 심의 등록</button>
        </div>
    </form>
</div>

<%-- 이행후검토(PIR) --%>
<c:if test="${change.status == 'APPLIED' or change.status == 'COMPLETED'}">
<div class="panel">
    <h3>이행후검토(PIR)</h3>
    <table class="form">
        <tr><th>검토 일시</th><td>${empty change.pirDt ? '-' : change.pirDt}</td></tr>
        <tr><th>검토 내용</th><td><div class="rte-view">${empty change.pirContent ? '-' : change.pirContent}</div></td></tr>
    </table>
    <form method="post" action="${ctx}/change/pir" style="margin-top:12px;">
        <input type="hidden" name="chgId" value="${change.chgId}"/>
        <table class="form">
            <tr><th>이행후검토 내용 <span class="required">*</span></th>
                <td><textarea class="wysiwyg" name="pirContent" rows="4">${change.pirContent}</textarea></td></tr>
        </table>
        <div class="right" style="margin-top:12px;">
            <button type="submit" class="btn btn-primary">PIR 기록</button>
        </div>
    </form>
</div>
</c:if>

<div class="toolbar"><a href="${ctx}/change/list" class="btn btn-default">＜ 목록</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="CHANGE"/>
    <c:param name="bizId" value="${change.chgId}"/>
    <c:param name="returnUrl" value="/change/detail/${change.chgId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

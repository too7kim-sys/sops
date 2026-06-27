<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  요청관리 진행 단계 스테퍼 (재사용 프래그먼트).
  param.status : 현재 상태 코드, param.mode : full(상세) | mini(목록)
  단계: 요청 → 접수 → 분류완료 → 처리중 → 처리완료 → 종료 (REJECTED 는 반려 표기)
--%>
<c:set var="stgCodes" value="REQUESTED,RECEIVED,CLASSIFIED,IN_PROGRESS,PROCESSED,CLOSED"/>
<c:set var="stgNames" value="요청,접수,분류완료,처리중,처리완료,종료"/>
<c:set var="curStatus" value="${param.status}"/>
<c:set var="stgMode" value="${empty param.mode ? 'full' : param.mode}"/>
<c:set var="codeArr" value="${fn:split(stgCodes, ',')}"/>
<c:set var="nameArr" value="${fn:split(stgNames, ',')}"/>
<c:set var="curIdx" value="-1"/>
<c:forEach var="cdv" items="${codeArr}" varStatus="i">
    <c:if test="${cdv == curStatus}"><c:set var="curIdx" value="${i.index}"/></c:if>
</c:forEach>
<c:choose>
    <c:when test="${curStatus == 'REJECTED'}">
        <div class="stepper stepper-${stgMode} is-rejected">
            <span class="badge st-rejected">반려</span>
            <c:if test="${stgMode == 'full'}"><span class="rej-note">표준 처리절차 중단(반려)</span></c:if>
        </div>
    </c:when>
    <c:otherwise>
        <div class="stepper stepper-${stgMode}">
            <c:forEach var="nm" items="${nameArr}" varStatus="i">
                <c:set var="cls" value="${i.index < curIdx ? 'done' : (i.index == curIdx ? 'current' : 'todo')}"/>
                <div class="step ${cls}">
                    <span class="dot">${i.index + 1}</span>
                    <span class="lbl">${nm}</span>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

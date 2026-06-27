<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  표준운영절차 진행 단계 스테퍼 (범용 재사용 프래그먼트).
  param.type    : 업무 구분 (CSR/CHANGE/RELEASE/INCIDENT/PROBLEM/TEST/INTERFACE/CI/EVENT)
  param.status  : 현재 상태 코드
  param.statusNm: 현재 상태 명 (표준경로 외 상태 표기용)
  param.mode    : full(상세) | mini(목록)
  - 표준경로에 없는 상태(반려/롤백/실패/장애전환 등)는 단절 배지로 표기한다.
--%>
<c:set var="stgType" value="${param.type}"/>
<c:choose>
    <c:when test="${stgType == 'CSR'}">
        <c:set var="codes" value="REQUESTED,RECEIVED,CLASSIFIED,IN_PROGRESS,PROCESSED,CLOSED"/>
        <c:set var="names" value="요청,접수,분류완료,처리중,처리완료,종료"/>
    </c:when>
    <c:when test="${stgType == 'CHANGE'}">
        <c:set var="codes" value="REQUESTED,REVIEWING,APPROVED,APPLIED,COMPLETED"/>
        <c:set var="names" value="요청,검토,승인,적용,완료"/>
    </c:when>
    <c:when test="${stgType == 'RELEASE'}">
        <c:set var="codes" value="PLANNED,APPROVED,DEPLOYING,DEPLOYED,VERIFYING,STABILIZING"/>
        <c:set var="names" value="배포계획,배포승인,배포중,배포완료,배포검증,안정화"/>
    </c:when>
    <c:when test="${stgType == 'INCIDENT'}">
        <c:set var="codes" value="RECEIVED,ANALYZING,ACTING,RESOLVED,CLOSED"/>
        <c:set var="names" value="접수,원인분석,조치중,조치완료,종결"/>
    </c:when>
    <c:when test="${stgType == 'PROBLEM'}">
        <c:set var="codes" value="REGISTERED,ANALYZING,IDENTIFIED,RESOLVING,RESOLVED,CLOSED"/>
        <c:set var="names" value="등록,분석중,원인규명,해결중,해결완료,종료"/>
    </c:when>
    <c:when test="${stgType == 'TEST'}">
        <c:set var="codes" value="PLANNED,TESTING,ANALYZED,CLOSED"/>
        <c:set var="names" value="계획,수행중,분석완료,종료"/>
    </c:when>
    <c:when test="${stgType == 'INTERFACE'}">
        <c:set var="codes" value="REQUESTED,REVIEWING,PLANNING,WORKING,TESTING,COMPLETED"/>
        <c:set var="names" value="요청,검토,계획수립,작업중,연계테스트,완료"/>
    </c:when>
    <c:when test="${stgType == 'CI'}">
        <c:set var="codes" value="IDENTIFIED,BASELINED,CHECKED_OUT,CHECKED_IN"/>
        <c:set var="names" value="식별,베이스라인,체크아웃,체크인"/>
    </c:when>
    <c:when test="${stgType == 'EVENT'}">
        <c:set var="codes" value="DETECTED,ANALYZING,ACTING,HANDLED,CLOSED"/>
        <c:set var="names" value="감지,분석중,조치중,조치완료,종료"/>
    </c:when>
</c:choose>
<c:set var="curStatus" value="${param.status}"/>
<c:set var="stgMode" value="${empty param.mode ? 'full' : param.mode}"/>
<c:set var="codeArr" value="${fn:split(codes, ',')}"/>
<c:set var="nameArr" value="${fn:split(names, ',')}"/>
<c:set var="curIdx" value="-1"/>
<c:forEach var="cdv" items="${codeArr}" varStatus="i">
    <c:if test="${cdv == curStatus}"><c:set var="curIdx" value="${i.index}"/></c:if>
</c:forEach>
<c:choose>
    <c:when test="${empty codes}">
        <span class="badge st-${fn:toLowerCase(curStatus)}">${empty param.statusNm ? curStatus : param.statusNm}</span>
    </c:when>
    <c:when test="${curIdx == -1}">
        <div class="stepper stepper-${stgMode} is-off">
            <span class="badge st-${fn:toLowerCase(curStatus)}">${empty param.statusNm ? curStatus : param.statusNm}</span>
            <c:if test="${stgMode == 'full'}"><span class="rej-note">표준 처리절차 외 상태</span></c:if>
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

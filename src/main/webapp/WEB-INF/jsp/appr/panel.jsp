<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- 결재선(검토/승인/처리자 라인) · 병렬 처리 · 공유 공통 패널 --%>
<%-- 기본 결재선·공유가 전혀 없으면(기본설정 미구성) 상세에서 패널 자체를 숨김 --%>
<c:if test="${not empty lineList or not empty shares}">
<div class="panel appr-panel">
    <h3>결재선 / 공유
        <span class="badge ov-${fn:toLowerCase(overallKey)}" style="margin-left:8px;">${overallNm}</span>
        <c:if test="${isManager}">
            <span style="float:right;font-weight:400;">
                <c:if test="${hasTemplate and empty lineList and empty shares}">
                <form method="post" action="${ctx}/appr/applyTemplate" style="display:inline;"
                      onsubmit="return confirm('이 업무의 기본 결재선/공유를 적용할까요?');">
                    <input type="hidden" name="bizType" value="${bizType}"/>
                    <input type="hidden" name="bizId" value="${bizId}"/>
                    <input type="hidden" name="returnUrl" value="${returnUrl}"/>
                    <button type="submit" class="btn btn-default btn-sm">기본 결재선/공유 적용</button>
                </form>
                </c:if>
            </span>
        </c:if>
    </h3>

    <div class="statbar" style="margin-bottom:10px;">
        <span class="chip">검토 <b>${reviewDone}/${reviewTotal}</b></span>
        <span class="chip">승인 <b>${approveDone}/${approveTotal}</b></span>
        <c:if test="${not hideHandle}"><span class="chip">처리 <b>${handleDone}/${handleTotal}</b></span></c:if>
        <c:if test="${myPending > 0}"><span class="chip" style="background:#fde9e9;color:#c0392b;">내 처리대기 <b>${myPending}</b></span></c:if>
    </div>

    <table class="list">
        <thead>
        <tr>
            <th style="width:64px;">단계</th>
            <th style="width:70px;">유형</th>
            <th style="width:150px;">대상자</th>
            <th style="width:90px;">상태</th>
            <th>의견 / 처리</th>
            <th style="width:130px;">처리일시</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty lineList}">
            <tr><td colspan="6" class="empty">지정된 결재선이 없습니다.</td></tr>
        </c:if>
        <c:set var="prevStep" value="-1"/>
        <c:forEach var="ln" items="${lineList}">
            <%-- 처리 버튼 권한: 본인 담당 라인(차례)만 노출, 운영관리자는 대리처리 가능 --%>
            <%-- 단계 게이트: 현재 진행 단계의 결재선만 처리(같은 단계는 병렬, 이전 단계 미완료면 대기) --%>
            <c:set var="atStep" value="${empty currentStep or ln.stepNo == currentStep}"/>
            <c:set var="canAct" value="${(ln.assigneeId == loginId or isAdmin) and ln.status == 'PENDING' and atStep}"/>
            <tr<c:if test="${ln.stepNo ne prevStep and prevStep ne -1}"> class="step-sep"</c:if>>
                <td>
                    <c:choose>
                        <c:when test="${ln.stepNo ne prevStep}">${ln.stepNo}단계</c:when>
                        <c:otherwise><span style="color:#aaa;">〃</span></c:otherwise>
                    </c:choose>
                </td>
                <td><span class="badge lt-${fn:toLowerCase(ln.lineType)}">${ln.lineTypeNm}</span></td>
                <td>${uf:nm(userNameMap, ln.assigneeId)}
                    <c:if test="${not empty ln.assigneeDept}"><div class="h-meta">${ln.assigneeDept}</div></c:if>
                </td>
                <td><span class="badge st-${fn:toLowerCase(ln.status)}">${empty ln.statusNm ? ln.status : ln.statusNm}</span></td>
                <td>
                    <c:if test="${not empty ln.opinion}"><div class="rte-view" style="margin-bottom:6px;">${ln.opinion}</div></c:if>
                    <c:if test="${canAct}">
                        <form method="post" action="${ctx}/appr/act" class="act-form">
                            <input type="hidden" name="apprId" value="${ln.apprId}"/>
                            <input type="hidden" name="returnUrl" value="${returnUrl}"/>
                            <textarea name="opinion" rows="2" placeholder="의견(선택)" style="width:100%;margin-bottom:6px;"></textarea>
                            <c:choose>
                                <c:when test="${ln.lineType == 'REVIEW'}">
                                    <button type="submit" name="action" value="REVIEW" class="btn btn-success btn-sm">검토완료</button>
                                    <button type="submit" name="action" value="REJECT" class="btn btn-danger btn-sm">반려</button>
                                </c:when>
                                <c:when test="${ln.lineType == 'APPROVE'}">
                                    <button type="submit" name="action" value="APPROVE" class="btn btn-success btn-sm">승인</button>
                                    <button type="submit" name="action" value="REJECT" class="btn btn-danger btn-sm">반려</button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" name="action" value="DONE" class="btn btn-primary btn-sm">처리완료</button>
                                    <button type="submit" name="action" value="REJECT" class="btn btn-danger btn-sm">반려</button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                    </c:if>
                    <c:if test="${ln.status == 'PENDING' and not atStep and (ln.assigneeId == loginId or isAdmin)}">
                        <span class="h-meta">이전 단계 진행 중 — 대기</span>
                    </c:if>
                </td>
                <td>${empty ln.actDt ? '-' : ln.actDt}</td>
            </tr>
            <c:set var="prevStep" value="${ln.stepNo}"/>
        </c:forEach>
        </tbody>
    </table>

    <%-- ================= 공유 ================= --%>
    <h3 style="margin-top:18px;">공유 <span class="badge">${fn:length(shares)}</span></h3>
    <table class="list">
        <thead>
        <tr>
            <th style="width:150px;">대상자</th>
            <th>메모</th>
            <th style="width:70px;">열람</th>
            <th style="width:160px;">공유자 / 일시</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty shares}"><tr><td colspan="4" class="empty">공유 내역이 없습니다.</td></tr></c:if>
        <c:forEach var="sh" items="${shares}">
            <tr>
                <td>${uf:nm(userNameMap, sh.userId)}
                    <c:if test="${not empty sh.userDept}"><div class="h-meta">${sh.userDept}</div></c:if>
                </td>
                <td>${empty sh.shareMemo ? '-' : sh.shareMemo}</td>
                <td><span class="badge ${sh.readAt == 'Y' ? 'st-approved' : 'st-pending'}">${sh.readAt == 'Y' ? '열람' : '미열람'}</span></td>
                <td>${uf:nm(userNameMap, sh.sharedBy)}<div class="h-meta">${sh.sharedDt}</div></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="h-meta" style="margin-top:8px;">※ 결재선/공유는 <b>결재 기본설정</b>에 따라 자동 구성됩니다. 변경하려면
        <c:choose><c:when test="${isAdmin}">상단 메뉴의 <b>[결재 기본설정]</b> 에서 수정하세요.</c:when>
        <c:otherwise>운영관리자에게 문의하세요.</c:otherwise></c:choose>
    </div>
</div>
</c:if>

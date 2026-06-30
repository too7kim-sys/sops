<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="pageTitle" value="배포 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 배포관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">배포 상세 <span style="color:#888;font-weight:400;">(REL-${release.relId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/release/list" class="btn btn-default">목록</a>
        <a href="${ctx}/release/edit/${release.relId}" class="btn btn-default">수정</a>
        <form action="${ctx}/release/delete/${release.relId}" method="post"
              onsubmit="return confirm('삭제하시겠습니까?');" style="display:inline;">
            <button type="submit" class="btn btn-danger">삭제</button>
        </form>
    </div>
</div>

<div class="panel">
    <h3>진행 단계 <span class="badge st-${fn:toLowerCase(release.status)}" style="margin-left:6px;">${release.statusNm}</span></h3>
    <jsp:include page="/WEB-INF/jsp/include/stage.jsp">
        <jsp:param name="type" value="RELEASE"/>
        <jsp:param name="status" value="${release.status}"/>
        <jsp:param name="statusNm" value="${release.statusNm}"/>
        <jsp:param name="mode" value="full"/>
    </jsp:include>
</div>

<div class="detail-stack">
    <div class="panel">
        <h3>배포 정보</h3>
        <table class="form">
            <tr>
                <th>대상 시스템</th><td>${release.sysNm}</td>
                <th>버전</th><td>${release.ver}</td>
            </tr>
            <tr><th>제목</th><td colspan="3">${release.title}</td></tr>
            <tr>
                <th>상태</th><td><span class="badge st-${fn:toLowerCase(release.status)}">${release.statusNm}</span></td>
                <th>연계 변경ID</th><td>${empty release.chgId ? '-' : release.chgId}</td>
            </tr>
            <tr>
                <th>배포 예정일</th><td>${empty release.planDt ? '-' : release.planDt}</td>
                <th>배포 일시</th><td>${empty release.deployDt ? '-' : release.deployDt}</td>
            </tr>
            <tr>
                <th>소요일(근무일)</th>
                <td colspan="3">
                    <c:set var="elapsedWd" value="${uf:wdays(release.planDt, release.deployDt)}"/>
                    <c:choose><c:when test="${empty elapsedWd}">-</c:when>
                    <c:otherwise><b>${elapsedWd}</b>일<c:if test="${uf:ongoing(release.deployDt)}"> <span class="h-meta">(진행중 · 오늘 기준)</span></c:if></c:otherwise></c:choose>
                </td>
            </tr>
            <tr><th>처리자</th><td colspan="3">${empty release.chargerId ? '-' : uf:nm(userNameMap, release.chargerId)}</td></tr>
            <tr><th>배포 내용</th><td colspan="3"><div class="rte-view">${release.content}</div></td></tr>
            <tr><th>배포 결과</th><td colspan="3"><div class="rte-view">${empty release.result ? '-' : release.result}</div></td></tr>
        </table>
    </div>

    <div>
        <%-- 배포 처리 (상태전이) --%>
        <c:if test="${release.status != 'DEPLOYED' and release.status != 'ROLLBACK'}">
        <div class="panel">
            <h3>배포 처리</h3>
            <form method="post" action="${ctx}/release/process">
                <input type="hidden" name="relId" value="${release.relId}"/>
                <table class="form">
                    <tr><th>처리 상태 <span class="required">*</span></th>
                        <td>
                            <select name="status" required>
                                <c:forEach var="cd" items="${statusList}">
                                    <option value="${cd.codeId}" ${cd.codeId == release.status ? 'selected' : ''}>${cd.codeNm}</option>
                                </c:forEach>
                            </select>
                        </td></tr>
                    <tr><th>배포 결과</th><td><textarea class="wysiwyg" name="result" rows="4">${release.result}</textarea></td></tr>
                </table>
                <div class="right" style="margin-top:12px;">
                    <button type="submit" class="btn btn-success">처리 등록</button>
                </div>
            </form>
        </div>
        </c:if>
    </div>
</div>

<%-- Git 자동배포 --%>
<div class="panel">
    <h3>Git 자동배포</h3>
    <c:if test="${deployMsg != null}">
        <c:set var="ok" value="${deployMsgType == 'info'}"/>
        <div style="${ok ? 'background:#eef3fb;color:#2e5da8;padding:9px 12px;border-radius:6px;margin-bottom:12px;' : 'background:#fbe0e0;color:#c0392b;padding:9px 12px;border-radius:6px;margin-bottom:12px;'}">${deployMsg}</div>
    </c:if>
    <c:if test="${system == null or system.gitUrl == null or system.gitUrl == ''}">
        <div class="empty">
            대상 시스템에 Git 저장소가 설정되지 않았습니다.
            <a href="${ctx}/system/edit/${release.sysId}">[응용시스템 Git 설정]</a>
        </div>
    </c:if>
    <c:if test="${system != null and system.gitUrl != null and system.gitUrl != ''}">
        <table class="form">
            <tr><th>Git 저장소</th><td colspan="3">${system.gitUrl}</td></tr>
            <tr>
                <th>기본 브랜치</th><td>${empty system.gitBranch ? '-' : system.gitBranch}</td>
                <th>배포 경로</th><td>${empty system.deployPath ? '(기본 작업영역)' : system.deployPath}</td>
            </tr>
            <tr><th>최근 배포 ref / 커밋</th>
                <td colspan="3">
                    <span>${empty release.deployRef ? '-' : release.deployRef}</span>
                    <c:if test="${release.deployCommit != null}"> / </c:if>
                    <c:if test="${release.deployCommit != null}"><code>${fn:substring(release.deployCommit,0,12)}</code></c:if>
                </td>
            </tr>
        </table>

        <form method="post" action="${ctx}/release/deploy" style="margin-top:12px;"
              onsubmit="return confirm('Git 체크아웃 및 배포 스크립트를 실행합니다. 진행하시겠습니까?');">
            <input type="hidden" name="relId" value="${release.relId}"/>
            <table class="form">
                <tr>
                    <th>배포 ref</th>
                    <td><input type="text" name="ref" value="${release.ver}" placeholder="태그/브랜치/커밋 (미입력 시 버전 또는 기본 브랜치)"/></td>
                    <th>실행 구분</th>
                    <td>
                        <select name="deployType">
                            <option value="DEPLOY">배포(DEPLOY)</option>
                            <option value="ROLLBACK">롤백(ROLLBACK)</option>
                        </select>
                    </td>
                </tr>
            </table>
            <div class="right" style="margin-top:12px;">
                <button type="submit" class="btn btn-primary">Git 배포 실행</button>
            </div>
        </form>

        <h3 style="margin-top:18px;">배포 실행 이력</h3>
        <table class="list">
            <thead>
            <tr>
                <th class="center" style="width:60px;">구분</th>
                <th class="center" style="width:90px;">결과</th>
                <th style="width:120px;">ref</th>
                <th style="width:110px;">커밋</th>
                <th>로그</th>
                <th class="center" style="width:90px;">실행자</th>
                <th class="center" style="width:150px;">실행일시</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="d" items="${deployHisList}">
                <tr>
                    <td class="center">${d.deployType}</td>
                    <td class="center"><span class="badge ${d.result == 'SUCCESS' ? 'sla-ok' : (d.result == 'RUNNING' ? 'sla-warn' : 'sla-viol')}">${d.result}</span></td>
                    <td>${empty d.deployRef ? '-' : d.deployRef}</td>
                    <td><code><c:choose><c:when test="${d.commitHash != null}">${fn:substring(d.commitHash,0,10)}</c:when><c:otherwise>-</c:otherwise></c:choose></code></td>
                    <td><pre style="white-space:pre-wrap;margin:0;font-size:12px;max-height:160px;overflow:auto;">${d.log}</pre></td>
                    <td class="center">${d.execBy}</td>
                    <td class="center">${d.execDt}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty deployHisList}"><tr><td colspan="7" class="empty">배포 실행 이력이 없습니다.</td></tr></c:if>
            </tbody>
        </table>
    </c:if>
</div>

<%-- 배포 항목 --%>
<div class="panel">
    <h3>배포 항목</h3>
    <table class="list">
        <thead>
        <tr>
            <th class="center" style="width:50px;">#</th>
            <th>항목명</th>
            <th>설명</th>
            <th class="center" style="width:120px;">결과</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="it" items="${release.itemList}" varStatus="stat">
            <tr>
                <td class="center">${stat.count}</td>
                <td>${it.itemNm}</td>
                <td style="white-space:pre-line;">${empty it.itemDesc ? '-' : it.itemDesc}</td>
                <td class="center"><span class="badge st-${fn:toLowerCase(it.itemResult)}">${empty it.itemResultNm ? it.itemResult : it.itemResultNm}</span></td>
            </tr>
        </c:forEach>
        <c:if test="${empty release.itemList}"><tr><td colspan="4" class="empty">배포 항목이 없습니다.</td></tr></c:if>
        </tbody>
    </table>

    <form method="post" action="${ctx}/release/item" style="margin-top:12px;">
        <input type="hidden" name="relId" value="${release.relId}"/>
        <table class="form">
            <tr>
                <th>항목명 <span class="required">*</span></th>
                <td><input type="text" name="itemNm" required/></td>
                <th>결과 <span class="required">*</span></th>
                <td>
                    <select name="itemResult" required>
                        <option value="">선택</option>
                        <c:forEach var="cd" items="${itemResultList}">
                            <option value="${cd.codeId}">${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>설명</th>
                <td colspan="3"><textarea name="itemDesc" rows="3"></textarea></td>
            </tr>
        </table>
        <div class="right" style="margin-top:12px;">
            <button type="submit" class="btn btn-success">항목 등록</button>
        </div>
    </form>
</div>

<div class="toolbar" style="justify-content:flex-end;"><a href="${ctx}/release/list" class="btn btn-default">목록 ＞</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="RELEASE"/>
    <c:param name="bizId" value="${release.relId}"/>
    <c:param name="returnUrl" value="/release/detail/${release.relId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

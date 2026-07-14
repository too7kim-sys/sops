<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="uf" uri="http://egovframework.ops/userfn" %>
<c:set var="pageTitle" value="변경 상세"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 변경관리 &gt; 상세</div>
<div class="page-head">
    <div class="page-title">변경 상세 <span style="color:#888;font-weight:400;">(CHG-${change.chgId})</span></div>
    <div class="toolbar">
        <a href="${ctx}/change/list" class="btn btn-default">목록</a>
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

<div class="detail-stack">
    <div class="panel">
        <h3>변경 정보</h3>
        <table class="form">
            <tr>
                <th>대상 시스템</th><td>${change.sysNm}</td>
                <th>유형 / 상태</th><td>
                    <span class="badge">${change.chgTypeNm}</span>&nbsp;
                    <span class="badge st-${fn:toLowerCase(change.status)}">${change.statusNm}</span>
                </td>
            </tr>
            <tr>
                <th>처리유형</th><td colspan="3">${empty change.procTypeNm ? '-' : change.procTypeNm}</td>
            </tr>
            <tr><th>제목</th><td colspan="3">${change.title}</td></tr>
            <tr>
                <th>요청자</th><td>${empty change.reqId ? '-' : uf:nm(userNameMap, change.reqId)}</td>
                <th>요청일시</th><td>${empty change.reqDt ? '-' : change.reqDt}</td>
            </tr>
            <tr>
                <th>적용 예정일</th><td>${empty change.planDt ? '-' : change.planDt}</td>
                <th>적용 일시</th><td>${empty change.applyDt ? '-' : change.applyDt}</td>
            </tr>
            <tr>
                <th>소요일(근무일)</th>
                <td colspan="3">
                    <c:set var="elapsedWd" value="${uf:wdays(change.reqDt, change.applyDt)}"/>
                    <c:choose><c:when test="${empty elapsedWd}">-</c:when>
                    <c:otherwise><b>${elapsedWd}</b>일<c:if test="${uf:ongoing(change.applyDt)}"> <span class="h-meta">(진행중 · 오늘 기준)</span></c:if></c:otherwise></c:choose>
                </td>
            </tr>
            <tr>
                <th>심의자</th><td>${empty change.apprId ? '-' : uf:nm(userNameMap, change.apprId)}</td>
                <th>심의일시</th><td>${empty change.apprDt ? '-' : change.apprDt}</td>
            </tr>
            <tr><th>변경 사유</th><td colspan="3"><div class="rte-view">${empty change.reason ? '-' : change.reason}</div></td></tr>
            <tr><th>변경 내용</th><td colspan="3"><div class="rte-view">${empty change.content ? '-' : change.content}</div></td></tr>
            <tr><th>심의 의견</th><td colspan="3"><div class="rte-view">${empty change.apprOpinion ? '-' : change.apprOpinion}</div></td></tr>
            <c:if test="${not empty change.procResult}">
            <tr><th>처리 결과</th><td colspan="3"><div class="rte-view">${change.procResult}</div></td></tr>
            </c:if>
        </table>
    </div>

    <c:if test="${not empty change.linkedId}">
        <c:set var="linkTgt" value="${transferTargets[change.linkedType]}"/>
        <div class="panel">
            <div class="h-meta">
                <span class="badge st-approved">이관됨</span>
                <a href="${ctx}${empty linkTgt ? '#' : linkTgt.urlPrefix}${change.linkedId}">${empty linkTgt ? change.linkedType : linkTgt.label} #${change.linkedId} 보기</a>
            </div>
        </div>
    </c:if>
</div>

<%-- 변경 처리 — 처리자(결재선 HANDLE 담당자)만 · 검토·승인 완료 후 · 완료 시 배포요청/장애관리로 이관 --%>
<c:if test="${change.status != 'COMPLETED' and change.status != 'REJECTED'}">
<div class="panel">
    <h3>변경 처리</h3>
    <c:if test="${not canProcess}">
        <div class="h-meta">변경 처리는 <b>처리자</b>(결재선 처리 담당자)만 가능합니다.</div>
    </c:if>
    <c:if test="${canProcess and not apprComplete}">
        <div class="h-meta"><b>검토·승인</b>이 모두 완료되어야 변경 처리를 진행할 수 있습니다. (결재선 진행 상황을 확인하세요)</div>
    </c:if>
    <c:if test="${canProcess and apprComplete}">
    <form method="post" action="${ctx}/change/apply">
        <input type="hidden" name="chgId" value="${change.chgId}"/>
        <table class="form">
            <tr><th>처리유형 <span class="required">*</span></th>
                <td>
                    <select name="procType" required>
                        <c:forEach var="cd" items="${procTypeList}">
                            <option value="${cd.codeId}" ${cd.codeId == change.procType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                    <span class="h-meta">일반변경 · 단순변경 · 긴급변경</span>
                </td></tr>
            <tr><th>처리 결과 <span class="required">*</span></th>
                <td><textarea class="wysiwyg" name="procResult" rows="4" required>${change.procResult}</textarea></td></tr>
            <tr><th>적용 예정일</th>
                <td><input type="date" name="planDt" value="${change.planDt}"/></td></tr>
            <tr><th>적용 일시</th>
                <td>
                    <input type="time" name="applyTime" value="${fn:substringAfter(change.applyDt, ' ')}"/>
                    <span class="h-meta">시간만 선택 — 날짜는 적용 예정일 기준(미지정 시 처리일), 미입력 시 처리 시각으로 기록됩니다.</span>
                </td></tr>
            <tr><th>적용 시 이관</th>
                <td>
                    <select name="transferTo" style="width:auto;">
                        <option value="">이관 안함</option>
                        <c:forEach var="t" items="${transferTargets}">
                            <option value="${t.key}">${t.value.label}</option>
                        </c:forEach>
                    </select>
                    <span class="h-meta">적용 등록 시 선택한 후속 업무(배포요청/장애관리)로 이관 생성됩니다.</span>
                </td></tr>
        </table>
        <div class="right" style="margin-top:12px;">
            <button type="submit" name="status" value="APPLIED" class="btn btn-primary">적용 등록</button>
            <button type="submit" name="status" value="COMPLETED" class="btn btn-success">완료 처리</button>
        </div>
    </form>
    </c:if>
</div>
</c:if>

<%-- CAB(변경자문위원회) 심의 — 검토자(결재선 REVIEW 담당자)만 수행 --%>
<div class="panel">
    <%-- CAB 심의는 중요도 1등급 시스템만 수행 · 심의이력이 있으면(등록 완료) 폼 숨김 --%>
    <c:set var="cabFormShown" value="${cabRequired and empty change.cabList and change.status != 'COMPLETED' and change.status != 'REJECTED'}"/>
    <c:if test="${not cabRequired}">
        <div class="h-meta"><b>CAB 심의 대상</b>이 아니어서(중요도 3등급 이하 또는 검토자 미지정) <b>CAB 심의 없이 자동 검토·승인</b> 처리됩니다.</div>
    </c:if>
    <c:if test="${cabFormShown}">
    <h3>CAB 심의</h3>
    <c:if test="${not canReview}">
        <div class="h-meta">CAB 심의는 <b>검토자</b>(결재선 검토 담당자)만 수행할 수 있습니다.</div>
    </c:if>
    <c:if test="${canReview}">
    <form method="post" action="${ctx}/change/cab">
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
            <tr><th>심의위원</th>
                <td>
                    <%-- 제출값(콤마구분 ID) : JS 가 칩 선택에 맞춰 갱신 --%>
                    <input type="hidden" name="reviewer" id="cabReviewerVal"/>
                    <div class="sysfind" style="cursor:default;">
                        <div class="sysfind-box" id="cabMemberBox" style="cursor:default;">
                            <span class="sysfind-tags" id="cabMemberTags"></span>
                        </div>
                    </div>
                    <div style="margin-top:6px;">
                        <button type="button" class="btn btn-default btn-sm" onclick="cabOpenUserSearch()">＋ 위원 검색/추가</button>
                        <span class="h-meta">요청자·심의자·검토/승인/처리자가 기본 포함됩니다. 칩의 ×로 제외할 수 있습니다.</span>
                    </div>
                </td></tr>
        </table>
        <div class="right" style="margin-top:12px;">
            <button type="submit" class="btn btn-success">CAB 심의 등록</button>
        </div>
    </form>
    </c:if>
    </c:if>

    <h3<c:if test="${cabFormShown}"> style="margin-top:20px;"</c:if>>CAB 심의이력</h3>
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
                <td>${empty cab.reviewer ? '-' : uf:nms(userNameMap, cab.reviewer)}</td>
                <td>${empty cab.cabDt ? '-' : cab.cabDt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${cabFormShown}">
<%-- 심의위원 사용자 검색 팝업 --%>
<div id="cabUserModal" class="modal-overlay" style="display:none;">
    <div class="modal-box">
        <div class="modal-head">
            <b>심의위원 검색/추가</b>
            <button type="button" class="modal-x" onclick="cabCloseUserSearch()">×</button>
        </div>
        <div class="modal-body">
            <input type="text" id="cabUserSearch" class="sysfind-search" style="width:100%;box-sizing:border-box;margin-bottom:8px;" placeholder="성명·ID·부서 검색…" autocomplete="off"/>
            <div id="cabUserResults" class="sysfind-results" style="position:static;display:block;max-height:320px;"></div>
        </div>
    </div>
</div>

<script>
(function () {
    var tagsEl = document.getElementById('cabMemberTags');
    var valEl  = document.getElementById('cabReviewerVal');
    if (!tagsEl || !valEl) return;

    // 후보 사용자 : {id, nm, search}
    var CANDS = [
        <c:forEach var="u" items="${userCandidates}" varStatus="vs">{id:'${u.userId}', nm:'${fn:escapeXml(uf:nm(userNameMap, u.userId))}', search:'${fn:escapeXml(fn:toLowerCase(u.userId))} ${fn:escapeXml(fn:toLowerCase(u.userNm))} ${fn:escapeXml(fn:toLowerCase(u.deptNm))}'}<c:if test="${!vs.last}">,</c:if></c:forEach>
    ];
    var NAME = {};
    CANDS.forEach(function (c) { NAME[c.id] = c.nm || c.id; });

    // 선택된 위원(기본값 : 요청자/심의자/결재선 대상자)
    var selected = [
        <c:forEach var="m" items="${cabMembers}" varStatus="vs">'${m}'<c:if test="${!vs.last}">,</c:if></c:forEach>
    ];

    function nmeOf(id) { return NAME[id] || id; }

    function render() {
        tagsEl.innerHTML = '';
        selected.forEach(function (id) {
            var chip = document.createElement('span');
            chip.className = 'sys-chip';
            chip.appendChild(document.createTextNode(nmeOf(id)));
            var x = document.createElement('button');
            x.type = 'button'; x.className = 'sys-chip-x'; x.textContent = '×'; x.title = '제외';
            x.addEventListener('click', function () {
                selected = selected.filter(function (s) { return s !== id; });
                render();
            });
            chip.appendChild(x);
            tagsEl.appendChild(chip);
        });
        if (!selected.length) {
            var none = document.createElement('span');
            none.className = 'h-meta'; none.textContent = '심의위원을 추가하세요.';
            tagsEl.appendChild(none);
        }
        valEl.value = selected.join(',');
    }

    var modal   = document.getElementById('cabUserModal');
    var search  = document.getElementById('cabUserSearch');
    var results = document.getElementById('cabUserResults');

    function renderResults() {
        var q = (search.value || '').toLowerCase().trim();
        var matches = CANDS.filter(function (c) {
            return selected.indexOf(c.id) < 0 && (!q || c.search.indexOf(q) >= 0);
        });
        results.innerHTML = '';
        if (!matches.length) {
            var n = document.createElement('div');
            n.className = 'sysfind-none';
            n.textContent = q ? '검색 결과가 없습니다.' : '추가할 사용자가 없습니다.';
            results.appendChild(n);
            return;
        }
        matches.slice(0, 100).forEach(function (c) {
            var d = document.createElement('div');
            d.className = 'sysfind-item';
            d.textContent = c.nm + ' [' + c.id + ']';
            d.addEventListener('click', function () {
                selected.push(c.id);
                render(); renderResults();
            });
            results.appendChild(d);
        });
    }

    window.cabOpenUserSearch = function () {
        modal.style.display = 'flex';
        search.value = '';
        renderResults();
        search.focus();
    };
    window.cabCloseUserSearch = function () { modal.style.display = 'none'; };

    search.addEventListener('input', renderResults);
    modal.addEventListener('click', function (e) { if (e.target === modal) { cabCloseUserSearch(); } });
    document.addEventListener('keydown', function (e) { if (e.key === 'Escape') { cabCloseUserSearch(); } });

    render();
})();
</script>
</c:if>

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

<div class="toolbar" style="justify-content:flex-end;"><a href="${ctx}/change/list" class="btn btn-default">목록 ＞</a></div>

<%-- 결재선(검토/승인/처리자) · 병렬 처리 · 공유 --%>
<c:import url="/appr/panel" charEncoding="UTF-8">
    <c:param name="bizType" value="CHANGE"/>
    <c:param name="bizId" value="${change.chgId}"/>
    <c:param name="returnUrl" value="/change/detail/${change.chgId}"/>
</c:import>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- 결재선(검토/승인/처리자 라인) · 병렬 처리 · 공유 공통 패널 --%>
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
                <c:if test="${isAdmin}">
                    <a href="${ctx}/appr/template?bizType=${bizType}" class="btn btn-ghost btn-sm" style="color:#1b3a6b;border-color:#cbd5e2;">기본 설정 관리</a>
                </c:if>
            </span>
        </c:if>
    </h3>

    <div class="statbar" style="margin-bottom:10px;">
        <span class="chip">검토 <b>${reviewDone}/${reviewTotal}</b></span>
        <span class="chip">승인 <b>${approveDone}/${approveTotal}</b></span>
        <span class="chip">처리 <b>${handleDone}/${handleTotal}</b></span>
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
            <c:if test="${isManager}"><th style="width:50px;"></th></c:if>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty lineList}">
            <tr><td colspan="${isManager ? 7 : 6}" class="empty">지정된 결재선이 없습니다.</td></tr>
        </c:if>
        <c:set var="prevStep" value="-1"/>
        <c:forEach var="ln" items="${lineList}">
            <c:set var="canAct" value="${(ln.assigneeId == loginId or isManager) and ln.status == 'PENDING'}"/>
            <tr<c:if test="${ln.stepNo ne prevStep and prevStep ne -1}"> class="step-sep"</c:if>>
                <td>
                    <c:choose>
                        <c:when test="${ln.stepNo ne prevStep}">${ln.stepNo}단계</c:when>
                        <c:otherwise><span style="color:#aaa;">〃</span></c:otherwise>
                    </c:choose>
                </td>
                <td><span class="badge lt-${fn:toLowerCase(ln.lineType)}">${ln.lineTypeNm}</span></td>
                <td>${empty ln.assigneeNm ? ln.assigneeId : ln.assigneeNm}
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
                </td>
                <td>${empty ln.actDt ? '-' : ln.actDt}</td>
                <c:if test="${isManager}">
                    <td>
                        <form method="post" action="${ctx}/appr/line/delete" onsubmit="return confirm('이 결재선을 삭제할까요?');">
                            <input type="hidden" name="apprId" value="${ln.apprId}"/>
                            <input type="hidden" name="bizType" value="${bizType}"/>
                            <input type="hidden" name="bizId" value="${bizId}"/>
                            <input type="hidden" name="returnUrl" value="${returnUrl}"/>
                            <button type="submit" class="btn btn-danger btn-sm">×</button>
                        </form>
                    </td>
                </c:if>
            </tr>
            <c:set var="prevStep" value="${ln.stepNo}"/>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${isManager}">
    <form method="post" action="${ctx}/appr/line/add" class="appr-add" data-target-form>
        <input type="hidden" name="bizType" value="${bizType}"/>
        <input type="hidden" name="bizId" value="${bizId}"/>
        <input type="hidden" name="returnUrl" value="${returnUrl}"/>
        <div class="appr-add-row">
            <label>유형
                <select name="lineType" required>
                    <c:forEach var="lty" items="${lineTypeList}">
                        <option value="${lty.codeId}">${lty.codeNm}</option>
                    </c:forEach>
                </select>
            </label>
            <label>단계
                <input type="number" name="stepNo" value="1" min="1" style="width:60px;" title="같은 단계 = 병렬 처리"/>
            </label>
            <label>대상 유형
                <select name="targetType" class="target-type">
                    <c:forEach var="tt" items="${targetTypeList}">
                        <option value="${tt.codeId}">${tt.codeNm}</option>
                    </c:forEach>
                </select>
            </label>
            <label class="tgt-user">대상자(복수 = 병렬)
                <select name="assigneeId" multiple size="3" style="min-width:200px;">
                    <c:forEach var="u" items="${candidates}">
                        <option value="${u.userId}">${u.userNm} (${u.userId}<c:if test="${not empty u.deptNm}">·${u.deptNm}</c:if>)</option>
                    </c:forEach>
                </select>
            </label>
            <label class="tgt-dept" style="display:none;">부서
                <span class="dept-search" style="max-width:240px;">
                    <input type="text" name="targetValue" id="lineDept_nm" class="dept-search-disp" data-prefix="lineDept"
                           placeholder="부서 검색" autocomplete="off" readonly onclick="openDeptPopup('lineDept')"/>
                    <button type="button" class="dept-search-btn" onclick="openDeptPopup('lineDept')"><svg viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="7" cy="7" r="4.5"></circle><line x1="11" y1="11" x2="14.5" y2="14.5"></line></svg>검색</button>
                </span>
            </label>
            <button type="submit" class="btn btn-primary btn-sm">결재선 추가</button>
        </div>
        <div class="h-meta">※ 같은 <b>단계</b>는 <b>병렬</b> 처리. 대상 유형을 <b>부서/요청자/전체</b>로 지정하면 해당 사용자들로 자동 전개됩니다.</div>
    </form>
    </c:if>

    <%-- ================= 공유 ================= --%>
    <h3 style="margin-top:18px;">공유 <span class="badge">${fn:length(shares)}</span></h3>
    <table class="list">
        <thead>
        <tr>
            <th style="width:150px;">대상자</th>
            <th>메모</th>
            <th style="width:70px;">열람</th>
            <th style="width:160px;">공유자 / 일시</th>
            <th style="width:50px;"></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty shares}"><tr><td colspan="5" class="empty">공유 내역이 없습니다.</td></tr></c:if>
        <c:forEach var="sh" items="${shares}">
            <tr>
                <td>${empty sh.userNm ? sh.userId : sh.userNm}
                    <c:if test="${not empty sh.userDept}"><div class="h-meta">${sh.userDept}</div></c:if>
                </td>
                <td>${empty sh.shareMemo ? '-' : sh.shareMemo}</td>
                <td><span class="badge ${sh.readAt == 'Y' ? 'st-approved' : 'st-pending'}">${sh.readAt == 'Y' ? '열람' : '미열람'}</span></td>
                <td>${empty sh.sharedByNm ? sh.sharedBy : sh.sharedByNm}<div class="h-meta">${sh.sharedDt}</div></td>
                <td>
                    <c:if test="${isManager or sh.sharedBy == loginId}">
                    <form method="post" action="${ctx}/appr/share/delete" onsubmit="return confirm('공유를 취소할까요?');">
                        <input type="hidden" name="shareId" value="${sh.shareId}"/>
                        <input type="hidden" name="bizType" value="${bizType}"/>
                        <input type="hidden" name="bizId" value="${bizId}"/>
                        <input type="hidden" name="returnUrl" value="${returnUrl}"/>
                        <button type="submit" class="btn btn-danger btn-sm">×</button>
                    </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <form method="post" action="${ctx}/appr/share/add" class="appr-add" data-target-form>
        <input type="hidden" name="bizType" value="${bizType}"/>
        <input type="hidden" name="bizId" value="${bizId}"/>
        <input type="hidden" name="returnUrl" value="${returnUrl}"/>
        <div class="appr-add-row">
            <label>대상 유형
                <select name="targetType" class="target-type">
                    <c:forEach var="tt" items="${targetTypeList}">
                        <option value="${tt.codeId}">${tt.codeNm}</option>
                    </c:forEach>
                </select>
            </label>
            <label class="tgt-user">공유 대상자(복수 선택)
                <select name="userId" multiple size="3" style="min-width:200px;">
                    <c:forEach var="u" items="${candidates}">
                        <option value="${u.userId}">${u.userNm} (${u.userId}<c:if test="${not empty u.deptNm}">·${u.deptNm}</c:if>)</option>
                    </c:forEach>
                </select>
            </label>
            <label class="tgt-dept" style="display:none;">부서
                <span class="dept-search" style="max-width:240px;">
                    <input type="text" name="targetValue" id="shareDept_nm" class="dept-search-disp" data-prefix="shareDept"
                           placeholder="부서 검색" autocomplete="off" readonly onclick="openDeptPopup('shareDept')"/>
                    <button type="button" class="dept-search-btn" onclick="openDeptPopup('shareDept')"><svg viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="7" cy="7" r="4.5"></circle><line x1="11" y1="11" x2="14.5" y2="14.5"></line></svg>검색</button>
                </span>
            </label>
            <label style="flex:1;">메모
                <input type="text" name="shareMemo" placeholder="공유 메모(선택)" style="width:100%;"/>
            </label>
            <button type="submit" class="btn btn-primary btn-sm">공유</button>
        </div>
    </form>
</div>

<script>
/* 대상 유형(사용자/부서/요청자/전체)에 따라 입력 표시 전환 — jQuery 비의존(vanilla) */
(function () {
    function sync(form) {
        var t = form.querySelector('.target-type');
        if (!t) return;
        var v = t.value;
        var u = form.querySelector('.tgt-user'), d = form.querySelector('.tgt-dept');
        if (u) {
            u.style.display = (v === 'USER') ? '' : 'none';
            var us = u.querySelector('select'); if (us) us.disabled = (v !== 'USER');
        }
        if (d) {
            d.style.display = (v === 'DEPT') ? '' : 'none';
            var dv = d.querySelector('[name=targetValue]'); if (dv) dv.disabled = (v !== 'DEPT');
        }
    }
    var forms = document.querySelectorAll('form[data-target-form]');
    for (var i = 0; i < forms.length; i++) {
        (function (f) {
            var t = f.querySelector('.target-type');
            if (t) t.addEventListener('change', function () { sync(f); });
            sync(f);
        })(forms[i]);
    }
})();
</script>

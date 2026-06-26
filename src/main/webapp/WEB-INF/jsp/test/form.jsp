<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="테스트 등록"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 테스트관리 &gt; 등록</div>
<div class="page-head">
    <div class="page-title">테스트 등록</div>
    <div class="page-desc">테스트 정보와 테스트케이스를 함께 등록합니다.</div>
</div>

<form method="post" action="${ctx}/test/insert">
    <div class="panel">
        <h3>테스트 정보</h3>
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == test.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>연계 변경 ID</th>
                <td><input type="number" name="chgId" value="${test.chgId}" placeholder="선택"/></td>
            </tr>
            <tr>
                <th>제목 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="title" value="${test.title}" required/></td>
            </tr>
            <tr>
                <th>테스트 유형 <span class="required">*</span></th>
                <td>
                    <select name="testType" required>
                        <option value="">선택</option>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == test.testType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>테스트 환경 <span class="required">*</span></th>
                <td>
                    <select name="testEnv" required>
                        <option value="">선택</option>
                        <c:forEach var="cd" items="${envList}">
                            <option value="${cd.codeId}" ${cd.codeId == test.testEnv ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>예정일</th>
                <td><input type="date" name="planDt" value="${test.planDt}"/></td>
                <th>테스터</th>
                <td><input type="text" name="testerId" value="${test.testerId}" placeholder="미입력 시 로그인 사용자"/></td>
            </tr>
        </table>
    </div>

    <div class="panel">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;">
            <h3 style="margin:0;">테스트 케이스</h3>
            <button type="button" class="btn btn-default" onclick="addCaseRow()">＋ 케이스 추가</button>
        </div>
        <table class="list" id="caseTable">
            <thead>
            <tr>
                <th style="width:40px;" class="center">#</th>
                <th>케이스명</th>
                <th>기대 결과</th>
                <th style="width:110px;" class="center">결과</th>
                <th>비고</th>
                <th style="width:70px;" class="center">삭제</th>
            </tr>
            </thead>
            <tbody id="caseBody">
            </tbody>
        </table>
    </div>

    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/test/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<script>
    var caseIdx = 0;

    function addCaseRow(nm, expected, result, remark) {
        var idx = caseIdx++;
        var resultOptions =
            '<option value="NA"' + (result === 'PASS' || result === 'FAIL' ? '' : ' selected') + '>해당없음</option>' +
            '<option value="PASS"' + (result === 'PASS' ? ' selected' : '') + '>성공</option>' +
            '<option value="FAIL"' + (result === 'FAIL' ? ' selected' : '') + '>실패</option>';
        var tr = document.createElement('tr');
        tr.innerHTML =
            '<td class="center idx-cell"></td>' +
            '<td><input type="text" name="caseList[' + idx + '].caseNm" value="' + (nm || '') + '" placeholder="테스트 케이스명"/></td>' +
            '<td><input type="text" name="caseList[' + idx + '].expected" value="' + (expected || '') + '" placeholder="기대 결과"/></td>' +
            '<td class="center"><select name="caseList[' + idx + '].caseResult">' + resultOptions + '</select></td>' +
            '<td><input type="text" name="caseList[' + idx + '].remark" value="' + (remark || '') + '" placeholder="비고"/></td>' +
            '<td class="center"><button type="button" class="btn btn-danger btn-sm" onclick="removeCaseRow(this)">삭제</button></td>';
        document.getElementById('caseBody').appendChild(tr);
        renumber();
    }

    function removeCaseRow(btn) {
        var tr = btn.closest('tr');
        tr.parentNode.removeChild(tr);
        renumber();
    }

    function renumber() {
        var rows = document.querySelectorAll('#caseBody tr');
        for (var i = 0; i < rows.length; i++) {
            rows[i].querySelector('.idx-cell').textContent = (i + 1);
        }
    }

    // 기본 테스트케이스 행 3개 미리 제공
    document.addEventListener('DOMContentLoaded', function () {
        addCaseRow();
        addCaseRow();
        addCaseRow();
    });
</script>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

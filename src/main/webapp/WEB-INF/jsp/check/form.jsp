<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="운영점검 등록"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="breadcrumb">표준운영절차 &gt; 운영점검 &gt; 등록</div>
<div class="page-head">
    <div class="page-title">운영점검 등록</div>
    <div class="page-desc">점검헤더와 점검항목을 함께 등록합니다. 항목 중 하나라도 '이상'이면 종합결과는 자동으로 '이상'으로 산정됩니다.</div>
</div>

<form method="post" action="${ctx}/check/insert">
    <div class="panel">
        <h3>점검 정보</h3>
        <table class="form">
            <tr>
                <th>대상 시스템 <span class="required">*</span></th>
                <td>
                    <select name="sysId" required>
                        <option value="">선택</option>
                        <c:forEach var="s" items="${systemList}">
                            <option value="${s.sysId}" ${s.sysId == check.sysId ? 'selected' : ''}>${s.sysNm}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>점검 유형 <span class="required">*</span></th>
                <td>
                    <select name="chkType" required>
                        <option value="">선택</option>
                        <c:forEach var="cd" items="${typeList}">
                            <option value="${cd.codeId}" ${cd.codeId == check.chkType ? 'selected' : ''}>${cd.codeNm}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>점검 일자 <span class="required">*</span></th>
                <td><input type="date" name="chkDt" value="${check.chkDt}" required/></td>
                <th>점검자</th>
                <td><select name="chkrId"><option value="">선택</option><c:forEach var="__u" items="${userNameMap}"><option value="${__u.key}" ${__u.key == check.chkrId ? 'selected' : ''}>${__u.value}</option></c:forEach></select></td>
            </tr>
            <tr>
                <th>비고</th>
                <td colspan="3"><textarea class="wysiwyg" name="remark" rows="3">${check.remark}</textarea></td>
            </tr>
        </table>
    </div>

    <div class="panel">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;">
            <h3 style="margin:0;">점검 항목</h3>
            <button type="button" class="btn btn-default" onclick="addItemRow()">＋ 항목 추가</button>
        </div>
        <table class="list" id="itemTable">
            <thead>
            <tr>
                <th style="width:40px;" class="center">#</th>
                <th>항목명</th>
                <th style="width:140px;" class="center">결과</th>
                <th>비고</th>
                <th style="width:70px;" class="center">삭제</th>
            </tr>
            </thead>
            <tbody id="itemBody">
            </tbody>
        </table>
    </div>

    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/check/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<script>
    var itemIdx = 0;

    function addItemRow(nm, result, remark) {
        var idx = itemIdx++;
        var tr = document.createElement('tr');
        var resultOptions =
            '<option value="NORMAL"' + (result === 'ABNORMAL' ? '' : ' selected') + '>정상</option>' +
            '<option value="ABNORMAL"' + (result === 'ABNORMAL' ? ' selected' : '') + '>이상</option>';
        tr.innerHTML =
            '<td class="center idx-cell"></td>' +
            '<td><input type="text" name="itemList[' + idx + '].itemNm" value="' + (nm || '') + '" placeholder="점검 항목명"/></td>' +
            '<td class="center"><select name="itemList[' + idx + '].itemResult">' + resultOptions + '</select></td>' +
            '<td><input type="text" name="itemList[' + idx + '].itemRemark" value="' + (remark || '') + '" placeholder="비고"/></td>' +
            '<td class="center"><button type="button" class="btn btn-danger btn-sm" onclick="removeItemRow(this)">삭제</button></td>';
        document.getElementById('itemBody').appendChild(tr);
        renumber();
    }

    function removeItemRow(btn) {
        var tr = btn.closest('tr');
        tr.parentNode.removeChild(tr);
        renumber();
    }

    function renumber() {
        var rows = document.querySelectorAll('#itemBody tr');
        for (var i = 0; i < rows.length; i++) {
            rows[i].querySelector('.idx-cell').textContent = (i + 1);
        }
    }

    // 기본 점검항목 행 3개 미리 제공
    document.addEventListener('DOMContentLoaded', function () {
        addItemRow();
        addItemRow();
        addItemRow();
    });
</script>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

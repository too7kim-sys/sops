<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="응용시스템 등록/수정"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isNew" value="${system.sysId == null or system.sysId == ''}"/>

<div class="breadcrumb">기준정보 관리 &gt; 응용시스템 &gt; ${isNew ? '등록' : '수정'}</div>
<div class="page-head">
    <div class="page-title">${isNew ? '응용시스템 등록' : '응용시스템 수정'}</div>
</div>

<form method="post" action="${ctx}${isNew ? '/system/insert' : '/system/update'}">
    <div class="panel">
        <table class="form">
            <tr>
                <th>시스템ID <span class="required">*</span></th>
                <td>
                    <c:choose>
                        <c:when test="${isNew}">
                            <input type="text" name="sysId" value="${system.sysId}" required/>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="sysId" value="${system.sysId}"/>
                            <span>${system.sysId}</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <th>중요도등급 <span class="required">*</span></th>
                <td>
                    <select name="grad" required>
                        <option value="1" ${system.grad == '1' ? 'selected' : ''}>1등급(상)</option>
                        <option value="2" ${system.grad == '2' ? 'selected' : ''}>2등급(중)</option>
                        <option value="3" ${system.grad == '3' ? 'selected' : ''}>3등급(하)</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>시스템명 <span class="required">*</span></th>
                <td colspan="3"><input type="text" name="sysNm" value="${system.sysNm}" required/></td>
            </tr>
            <tr>
                <th>운영담당자</th>
                <td><input type="text" name="mngrNm" value="${system.mngrNm}"/></td>
                <th>운영부서</th>
                <td><input type="text" name="mngrDept" value="${system.mngrDept}"/></td>
            </tr>
            <tr>
                <th>사용여부</th>
                <td colspan="3">
                    <select name="useAt">
                        <option value="Y" ${(system.useAt == 'Y' or system.useAt == null or system.useAt == '') ? 'selected' : ''}>사용</option>
                        <option value="N" ${system.useAt == 'N' ? 'selected' : ''}>미사용</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>시스템설명</th>
                <td colspan="3"><textarea class="wysiwyg" name="sysDesc" rows="3">${system.sysDesc}</textarea></td>
            </tr>
        </table>
    </div>

    <div class="panel">
        <h3>Git 자동배포 설정</h3>
        <table class="form">
            <tr>
                <th>Git 저장소 URL</th>
                <td colspan="3"><input type="text" name="gitUrl" value="${system.gitUrl}"
                        placeholder="예) https://git.example.go.kr/app.git 또는 file:///repo/app"/></td>
            </tr>
            <tr>
                <th>기본 브랜치</th>
                <td><input type="text" name="gitBranch" value="${system.gitBranch}" placeholder="main / master"/></td>
                <th>배포 경로</th>
                <td><input type="text" name="deployPath" value="${system.deployPath}" placeholder="체크아웃 대상 디렉터리(미입력 시 기본 작업영역)"/></td>
            </tr>
            <tr>
                <th>배포 스크립트</th>
                <td colspan="3"><textarea name="deployScript" rows="3"
                        placeholder="체크아웃 후 실행할 셸 스크립트 (환경변수: $SYS_ID $VER $REF $DEPLOY_PATH $DEPLOY_TYPE)">${system.deployScript}</textarea></td>
            </tr>
        </table>
    </div>

    <div class="toolbar right" style="justify-content:flex-end;">
        <a href="${ctx}/system/list" class="btn btn-default">목록</a>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/include/editor.jsp"/>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>

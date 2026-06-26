<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- 무료 웹에디터(Summernote Lite, MIT) : textarea.wysiwyg 자동 적용. 로컬 호스팅(망분리 대응) --%>
<link rel="stylesheet" href="${ctx}/css/editor/summernote-lite.min.css"/>
<script src="${ctx}/js/editor/jquery.min.js"></script>
<script src="${ctx}/js/editor/summernote-lite.min.js"></script>
<script src="${ctx}/js/editor/summernote-ko-KR.min.js"></script>
<script>
jQuery(function ($) {
    $('textarea.wysiwyg').each(function () {
        var $ta = $(this);
        $ta.summernote({
            lang: 'ko-KR',
            height: 200,
            placeholder: $ta.attr('placeholder') || '',
            disableDragAndDrop: true,
            toolbar: [
                ['style',   ['style']],
                ['font',    ['bold', 'underline', 'italic', 'strikethrough', 'clear']],
                ['fontsize',['fontsize']],
                ['color',   ['color']],
                ['para',    ['ul', 'ol', 'paragraph']],
                ['table',   ['table']],
                ['insert',  ['link']],
                ['view',    ['codeview', 'fullscreen', 'help']]
            ]
        });
    });
});
</script>

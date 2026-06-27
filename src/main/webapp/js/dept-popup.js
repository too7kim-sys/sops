/* 부서 팝업 검색 공통 스크립트 (jQuery 비의존) */
(function () {
    function ctx() { return window.CTX || ''; }

    /** 부서 검색 팝업 열기. prefix 는 대상 필드 식별자(P_nm / P_val). 표시필드 입력값을 검색어로 전달 */
    window.openDeptPopup = function (prefix) {
        var disp = document.getElementById(prefix + '_nm');
        var kw = (disp && disp.value) ? disp.value : '';
        var url = ctx() + '/sys/dept/popup?prefix=' + encodeURIComponent(prefix)
            + '&searchKeyword=' + encodeURIComponent(kw);
        var win = window.open(url, 'deptPopup_' + prefix,
            'width=620,height=560,scrollbars=yes,resizable=yes');
        if (win) { win.focus(); }
    };

    /** id 바인딩 필드 초기화(최상위/미지정) */
    window.clearDept = function (prefix) {
        var disp = document.getElementById(prefix + '_nm');
        var val = document.getElementById(prefix + '_val');
        if (disp) { disp.value = ''; }
        if (val) { val.value = ''; }
    };

    /** 팝업에서 선택 시 호출(opener) — 표시필드/값필드 세팅 */
    window.applyDept = function (prefix, deptId, deptCd, deptNm) {
        var disp = document.getElementById(prefix + '_nm');
        var val = document.getElementById(prefix + '_val');
        if (disp) { disp.value = deptNm; }
        if (val) {
            var bind = val.getAttribute('data-dept-bind') || 'nm';
            val.value = (bind === 'id') ? deptId : (bind === 'cd' ? deptCd : deptNm);
        }
    };

    /** 표시필드 enter/click 시 팝업 (인라인 핸들러 대체용) */
    document.addEventListener('keydown', function (e) {
        var t = e.target;
        if (t && t.classList && t.classList.contains('dept-search-disp') && e.key === 'Enter') {
            e.preventDefault();
            var p = t.getAttribute('data-prefix');
            if (p) { window.openDeptPopup(p); }
        }
    });
})();

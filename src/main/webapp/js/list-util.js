/* 목록 공통 유틸 : 엑셀(CSV, UTF-8 BOM) 다운로드 */
(function () {
    'use strict';

    function cellText(el) {
        // data-export 가 있으면 그 값을 우선(스테퍼 등 복합 셀의 깔끔한 내보내기)
        var ex = el.getAttribute && el.getAttribute('data-export');
        if (ex != null) { return ex.replace(/\s+/g, ' ').trim(); }
        return (el.innerText || el.textContent || '').replace(/\s+/g, ' ').trim();
    }

    function csvCell(v) {
        v = (v == null ? '' : String(v));
        return /[",\n\r]/.test(v) ? '"' + v.replace(/"/g, '""') + '"' : v;
    }

    function stamp() {
        var d = new Date();
        function p(n) { return ('0' + n).slice(-2); }
        return '' + d.getFullYear() + p(d.getMonth() + 1) + p(d.getDate())
            + '_' + p(d.getHours()) + p(d.getMinutes());
    }

    /**
     * 현재 화면의 목록 테이블(table.list)을 엑셀(CSV)로 다운로드.
     * - 헤더가 비어 있는 열(작업 버튼 등)은 제외
     * - "데이터 없음" 행은 제외
     * @param {string} [name] 파일명(미지정 시 .page-title 사용)
     */
    window.exportListExcel = function (name) {
        var table = document.querySelector('.list-scroll table.list') || document.querySelector('table.list');
        if (!table) { alert('내보낼 목록이 없습니다.'); return; }

        var title = name;
        if (!title) {
            var pt = document.querySelector('.page-title');
            title = pt ? cellText(pt).replace(/[\\/:*?"<>|]/g, '').split('(')[0].trim() : '목록';
        }

        var ths = Array.prototype.slice.call(table.querySelectorAll('thead th'));
        var skip = {};
        var header = [];
        ths.forEach(function (th, i) {
            var t = cellText(th);
            if (!t) { skip[i] = true; } else { header.push(t); }
        });

        var rows = [header];
        Array.prototype.slice.call(table.querySelectorAll('tbody tr')).forEach(function (tr) {
            if (tr.querySelector('td.empty')) { return; }
            var tds = Array.prototype.slice.call(tr.children).filter(function (c) { return c.tagName === 'TD'; });
            var row = [];
            tds.forEach(function (td, i) {
                if (skip[i]) { return; }
                row.push(cellText(td));
            });
            if (row.length) { rows.push(row); }
        });

        if (rows.length <= 1) { alert('내보낼 데이터가 없습니다.'); return; }

        var csv = rows.map(function (r) { return r.map(csvCell).join(','); }).join('\r\n');
        var blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
        var url = URL.createObjectURL(blob);
        var a = document.createElement('a');
        a.href = url;
        a.download = title + '_' + stamp() + '.csv';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        setTimeout(function () { URL.revokeObjectURL(url); }, 1000);
    };
})();

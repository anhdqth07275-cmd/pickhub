/* =====================================================================
   PickHub - login.js (dung chung 4 trang dang nhap)
   1) Nut mat an / hien mat khau   2) Hieu ung "hai nua tach ra" truoc khi gui form
   note code nhu nay nhe ban: form van gui DUNG nhu cu (cung action, method, name cac o). Chi cho hieu ung chay ~0.4s roi goi form.submit() goc.
   ===================================================================== */
(function () {
    'use strict';
    var giam = window.matchMedia && matchMedia('(prefers-reduced-motion: reduce)').matches;

    // ---- 1) mat an / hien mat khau: chi doi type cua o, KHONG doi name nen backend nhan van dung
    document.querySelectorAll('[data-pl-eye]').forEach(function (nut) {
        var o = document.getElementById(nut.getAttribute('data-pl-eye'));
        if (!o) return;
        nut.addEventListener('click', function () {
            var hien = o.type === 'password';
            o.type = hien ? 'text' : 'password';
            nut.setAttribute('aria-pressed', hien ? 'true' : 'false');
            nut.setAttribute('aria-label', hien ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
            o.focus();
        });
    });

    // ---- 2) hai nua tach ra roi moi gui form
    var shell = document.querySelector('.pl-shell');
    var form = document.querySelector('form.pl-card');
    if (!form || !shell) return;
    var daGui = false;

    form.addEventListener('submit', function (e) {
        if (daGui) return;
        // Neu thieu o bat buoc thi de trinh duyet bao loi nhu cu, khong chay hieu ung
        if (form.checkValidity && !form.checkValidity()) return;
        if (giam) return;
        e.preventDefault();
        daGui = true;
        var nut = form.querySelector('.pl-btn');
        if (nut) nut.classList.add('is-busy');
        shell.classList.add('pl-leaving');
        setTimeout(function () { form.submit(); }, 420);   // form.submit() KHONG goi lai su kien submit nen khong bi lap
    });

    // Quay lai bang nut Back (bfcache): bo trang thai dang tach
    window.addEventListener('pageshow', function (e) {
        if (!e.persisted) return;
        daGui = false;
        shell.classList.remove('pl-leaving');
        var nut = form.querySelector('.pl-btn');
        if (nut) nut.classList.remove('is-busy');
    });
})();

/* =====================================================================
   PickHub - theme.js (dung chung cho admin, trang ban hang, POS)
   Lam 4 viec, TAT CA chi o phia trinh duyet, khong dung toi backend / form / link:
   1) Cong tac sang/toi (nho bang localStorage)  2) Sidebar admin tu thu gon / mo khi re chuot
   3) Dem so o dashboard                         4) Mo nhoe nhanh khi chuyen trang admin
   note code nhu nay nhe ban: cac nut them vao la CHEN THEM, khong xoa / doi cho khoi nao co san.
   ===================================================================== */
(function () {
    'use strict';
    var root = document.documentElement;
    var reduce = window.matchMedia && matchMedia('(prefers-reduced-motion: reduce)').matches;

    function safeGet(k) { try { return localStorage.getItem(k); } catch (e) { return null; } }
    function safeSet(k, v) { try { localStorage.setItem(k, v); } catch (e) { /* che do rieng tu: bo qua */ } }

    // ---------------------------------------------------------------- 1) SANG / TOI
    var SUN = '<svg class="i-sun" viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>';
    var MOON = '<svg class="i-moon" viewBox="0 0 24 24"><path d="M20.5 14.2A8.5 8.5 0 0 1 9.8 3.5a8.5 8.5 0 1 0 10.7 10.7z"/></svg>';

    function laToi() { return root.getAttribute('data-theme') === 'dark'; }

    function capNhatCongTac() {
        var toi = laToi();
        document.querySelectorAll('.ph-switch').forEach(function (b) { b.setAttribute('aria-checked', toi ? 'true' : 'false'); });
    }

    function datCheDo(che, luu) {
        if (!reduce) {                                   // bat chuyen mau muot ~0.4s roi tat de khong lam cham trang
            root.classList.add('ph-theme-anim');
            setTimeout(function () { root.classList.remove('ph-theme-anim'); }, 450);
        }
        root.setAttribute('data-theme', che);
        if (luu) safeSet('ph-theme', che);
        capNhatCongTac();
    }

    function taoCongTac() {
        var b = document.createElement('button');
        b.type = 'button';
        b.className = 'ph-switch';
        b.setAttribute('role', 'switch');
        b.setAttribute('aria-label', 'Chế độ tối');
        b.title = 'Chế độ sáng / tối';
        b.innerHTML = '<span class="ph-switch-knob">' + SUN + MOON + '</span>';
        b.addEventListener('click', function () { datCheDo(laToi() ? 'light' : 'dark', true); });
        return b;
    }

    // Neu nguoi dung chua tu chon thi theo cai dat sang/toi cua he dieu hanh
    if (window.matchMedia && !safeGet('ph-theme')) {
        matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function (e) { datCheDo(e.matches ? 'dark' : 'light', false); });
    }

    function gan() {
        var sidebar = document.querySelector('.sidebar');
        var topKhach = document.querySelector('header.top .right');       // trang ban hang (trang-chu-khach)
        var topFront = document.querySelector('header.top > nav');        // trang tai khoan (trang-chu-frontend)
        var posPhai = document.querySelector('.pos-top .phai');           // POS
        var khac = document.querySelectorAll('[data-ph-switch]');         // cho de san (trang dang nhap)

        if (sidebar) {
            // Admin: them 1 hang "Che do toi" ngay TREN khoi tai khoan o day sidebar
            var hang = document.createElement('div');
            hang.className = 'ph-tools';
            hang.innerHTML = '<div class="ph-tool-row"><span class="ph-tool-label">Chế độ tối</span></div>';
            hang.firstChild.appendChild(taoCongTac());
            var userBox = sidebar.querySelector('.user-box');
            if (userBox) sidebar.insertBefore(hang, userBox); else sidebar.appendChild(hang);
            ganNhanMenu(sidebar);
        }
        if (topKhach) topKhach.insertBefore(taoCongTac(), topKhach.firstChild);
        if (topFront && !topKhach) topFront.appendChild(taoCongTac());   // header khach cung co <nav>, tranh gan 2 lan
        if (posPhai) posPhai.insertBefore(taoCongTac(), posPhai.firstChild);
        khac.forEach(function (el) { el.appendChild(taoCongTac()); });
        capNhatCongTac();
    }

    // ---------------------------------------------------------------- 2) SIDEBAR TU THU GON (admin)
    // note code nhu nay nhe ban: viec thu gon / mo rong khi re chuot do CSS lam (xem admin-glass.css).
    // JS chi boc chu cua tung muc menu vao <span class="ph-lbl"> de CSS lam mo / hien chu muot hon. Link, href, noi dung menu khong doi.
    function ganNhanMenu(sidebar) {
        sidebar.querySelectorAll('.nav-item, .nav-group summary').forEach(function (a) {
            Array.prototype.slice.call(a.childNodes).forEach(function (n) {
                if (n.nodeType === 3 && n.textContent.trim()) {
                    var s = document.createElement('span');
                    s.className = 'ph-lbl';
                    s.textContent = n.textContent.trim();
                    a.replaceChild(s, n);
                }
            });
        });
    }

    // ---------------------------------------------------------------- 3) DEM SO (dashboard / the thong ke)
    // Chi dem voi o chua DUNG 1 so (vd "12", "4.156.500 ₫", "98%"), cac o khac giu nguyen.
    function demSo() {
        if (reduce) return;
        var ds = document.querySelectorAll('.stat strong, .tile b, .mini-stats .box b, .thong-ke .o b');
        ds.forEach(function (el) {
            if (el.children.length) return;
            var goc = el.textContent;
            var m = goc.match(/^(\D*?)(\d{1,3}(?:[.,]\d{3})+|\d+)(\D*)$/);
            if (!m) return;
            var coPhanNghin = /[.,]\d{3}/.test(m[2]);
            var dau = coPhanNghin ? m[2].charAt(m[2].search(/[.,]/)) : '';
            var dich = parseInt(m[2].replace(/[.,]/g, ''), 10);
            if (!isFinite(dich) || dich < 2) return;
            var t0 = null, dai = 900;
            function dinhDang(n) {
                var s = String(Math.round(n));
                return coPhanNghin ? s.replace(/\B(?=(\d{3})+(?!\d))/g, dau) : s;
            }
            function buoc(t) {
                if (t0 === null) t0 = t;
                var p = Math.min(1, (t - t0) / dai);
                var e = 1 - Math.pow(2, -10 * p);               // ease-out mu: len nhanh, ve dich em
                el.textContent = m[1] + dinhDang(dich * e) + m[3];
                if (p < 1) requestAnimationFrame(buoc); else el.textContent = goc;   // ket thuc: tra ve DUNG chuoi goc
            }
            requestAnimationFrame(buoc);
        });
    }

    // ---------------------------------------------------------------- 4) CHUYEN TRANG ADMIN: mo nhoe nhanh roi moi di theo link
    function ganChuyenTrang() {
        var vung = document.querySelector('.sidebar ~ .main') || document.querySelector('header.top ~ .wrap');   // admin: .main, trang ban hang: .wrap
        if (!vung || reduce) return;
        document.addEventListener('click', function (e) {
            if (e.defaultPrevented || e.button !== 0 || e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;
            var a = e.target.closest && e.target.closest('a[href]');
            if (!a || a.target === '_blank' || a.hasAttribute('download')) return;
            var href = a.getAttribute('href');
            if (!href || href.charAt(0) === '#' || /^(javascript:|mailto:|tel:)/i.test(href)) return;
            if (a.origin !== location.origin) return;
            if (a.pathname === location.pathname && a.search === location.search && a.hash) return;
            e.preventDefault();
            vung.classList.add('ph-leave');
            setTimeout(function () { location.href = a.href; }, 190);
        });
        // Quay lai bang nut Back (bfcache) thi bo trang thai dang mo nhoe
        window.addEventListener('pageshow', function () { vung.classList.remove('ph-leave'); });
    }

    function chay() { gan(); demSo(); ganChuyenTrang(); }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', chay); else chay();
})();

/* ============================================================================
 * SELECT-SEARCH: MOI o <select> (dropdown) trong he thong deu co THANH TIM KIEM + goi y theo tu khoa.
 *
 * note code nhu nay nhe ban:
 *  - Khong can sua tung <select>: script bat su kien mousedown / keydown cua TAT CA <select> (event delegation),
 *    nen dropdown tao ra sau nay (kieu JS them vao trang) cung tu co tim kiem.
 *  - <select> goc VAN NGUYEN trong trang (giu layout, required, name, onchange...). Script chi chan popup mac dinh cua
 *    trinh duyet roi mo khung tim kiem rieng; chon xong thi gan select.selectedIndex va ban su kien "change" nhu binh thuong.
 *  - Muon TAT cho 1 o nao do: them thuoc tinh data-no-search vao <select>. <select multiple> / <select size="n"> duoc bo qua.
 *
 * Cach loc (khong phan biet hoa thuong va DAU tieng Viet, vd go "vot" ra "Vợt"):
 *   1. tu khoa nam o DAU cum tu          -> len dau danh sach
 *   2. tu khoa nam o dau 1 tu trong cum  -> tiep theo
 *   3. tu khoa nam o giua                -> cuoi cung
 * Nhieu tu thi moi tu deu phai co mat. Chi ve toi da 100 dong de nhe may khi du lieu rat lon.
 * ========================================================================== */
(function () {
    'use strict';
    if (window.__selectSearch) return;            // tranh nap script 2 lan
    window.__selectSearch = true;

    var TOI_DA = 100;                             // so dong ve toi da moi lan (du lieu lon van muot)
    var khung = null, oTim = null, danhSach = null, ghiChu = null;   // khung noi bat (tao 1 lan, dung chung cho moi dropdown)
    var dangMo = null;                            // <select> dang duoc mo
    var tatCa = [];                               // moi <option> cua select dang mo (da chuan hoa de so khop nhanh)
    var dangHien = [];                            // cac dong dang hien sau khi loc
    var viTri = -1;                               // dong dang sang (phim mui ten)

    // ---------- tien ich ----------
    // bo dau tieng Viet + hoa thuong: "Vợt Carbon" -> "vot carbon"
    function chuanHoa(s) {
        return String(s || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').replace(/Đ/g, 'd').toLowerCase();
    }
    function duocPhep(s) {
        return s && s.tagName === 'SELECT' && !s.disabled && !s.multiple && !(s.size > 1) && !s.hasAttribute('data-no-search') && !(khung && khung.contains(s));
    }

    // ---------- tao khung (1 lan) ----------
    function taoKhung() {
        if (khung) return;
        khung = document.createElement('div');
        khung.className = 'ss-khung';
        khung.setAttribute('role', 'listbox');
        khung.innerHTML = '<input type="text" class="ss-tim" placeholder="Gõ để tìm..." autocomplete="off" spellcheck="false">' +
            '<ul class="ss-ds"></ul><div class="ss-ghi-chu"></div>';
        document.body.appendChild(khung);
        oTim = khung.querySelector('.ss-tim');
        danhSach = khung.querySelector('.ss-ds');
        ghiChu = khung.querySelector('.ss-ghi-chu');

        oTim.addEventListener('input', loc);
        oTim.addEventListener('keydown', phimTrongKhung);
        // mousedown (khong dung click) de chon truoc khi o tim mat focus
        danhSach.addEventListener('mousedown', function (e) {
            e.preventDefault();
            var li = e.target.closest('.ss-it');
            if (li) chon(dangHien[parseInt(li.dataset.i, 10)]);
        });
        danhSach.addEventListener('mousemove', function (e) {
            var li = e.target.closest('.ss-it');
            if (li) sang(parseInt(li.dataset.i, 10), false);
        });
    }

    // ---------- mo / dong ----------
    function mo(sel, kyTuDau) {
        taoKhung();
        if (dangMo && dangMo !== sel) dangMo.classList.remove('ss-dang-mo');
        dangMo = sel;
        sel.classList.add('ss-dang-mo');

        // doc danh sach option moi lan mo -> luon thay doi duoc du lieu moi (option them bang JS)
        tatCa = [];
        var nhom = '';
        for (var i = 0; i < sel.options.length; i++) {
            var op = sel.options[i];
            var cha = op.parentNode;
            tatCa.push({
                index: i, value: op.value, text: op.text,
                bo: chuanHoa(op.text), khoa: op.disabled,
                nhom: cha && cha.tagName === 'OPTGROUP' ? cha.label : '',
                chon: op.selected
            });
        }
        oTim.value = kyTuDau || '';
        khung.classList.add('mo');
        datViTri();
        loc();
        oTim.focus();
        // dang mo ma khong go gi: cuon toi dong dang chon cho de thay
        if (!kyTuDau) {
            for (var k = 0; k < dangHien.length; k++) if (dangHien[k].chon) { sang(k, true); break; }
        }
    }
    function dong(traFocus) {
        if (!khung || !khung.classList.contains('mo')) return;
        khung.classList.remove('mo');
        if (dangMo) {
            dangMo.classList.remove('ss-dang-mo');
            if (traFocus) dangMo.focus();
        }
        dangMo = null;
    }
    // dat khung ngay duoi o select; thieu cho phia duoi thi mo len tren
    function datViTri() {
        if (!dangMo || !khung) return;
        var r = dangMo.getBoundingClientRect();
        var rong = Math.max(r.width, 240);
        var trai = Math.min(r.left, Math.max(8, window.innerWidth - rong - 8));
        khung.style.width = rong + 'px';
        khung.style.left = Math.max(8, trai) + 'px';
        var phiaDuoi = window.innerHeight - r.bottom;
        if (phiaDuoi < 300 && r.top > phiaDuoi) {
            khung.style.top = 'auto';
            khung.style.bottom = (window.innerHeight - r.top + 4) + 'px';
        } else {
            khung.style.bottom = 'auto';
            khung.style.top = (r.bottom + 4) + 'px';
        }
    }

    // ---------- loc + ve ----------
    function loc() {
        var tu = chuanHoa(oTim.value).trim();
        var cacTu = tu ? tu.split(/\s+/) : [];
        var kq = [];
        for (var i = 0; i < tatCa.length; i++) {
            var m = tatCa[i];
            var hang = 0;
            if (cacTu.length) {
                var du = true;
                for (var t = 0; t < cacTu.length; t++) if (m.bo.indexOf(cacTu[t]) < 0) { du = false; break; }
                if (!du) continue;
                if (m.bo.indexOf(cacTu[0]) === 0) hang = 0;                  // bat dau bang tu khoa
                else if (m.bo.indexOf(' ' + cacTu[0]) >= 0) hang = 1;       // dau 1 tu trong cum
                else hang = 2;                                               // nam o giua
            }
            kq.push({m: m, hang: hang, thuTu: i});
        }
        // sap xep on dinh: hang truoc, trong cung hang giu thu tu goc
        kq.sort(function (a, b) { return a.hang - b.hang || a.thuTu - b.thuTu; });
        dangHien = kq.slice(0, TOI_DA).map(function (x) { return x.m; });

        var h = '', nhomTruoc = null;
        dangHien.forEach(function (m, i) {
            // tieu de nhom (optgroup) chi hien khi khong dang tim kiem de danh sach gon
            if (!cacTu.length && m.nhom && m.nhom !== nhomTruoc) { h += '<li class="ss-nhom">' + esc(m.nhom) + '</li>'; }
            nhomTruoc = m.nhom;
            h += '<li class="ss-it' + (m.chon ? ' chon' : '') + (m.khoa ? ' khoa' : '') + '" role="option" data-i="' + i + '">' + (esc(m.text) || '&nbsp;') + '</li>';
        });
        if (!h) h = '<li class="ss-trong">Không tìm thấy kết quả</li>';
        danhSach.innerHTML = h;
        ghiChu.textContent = kq.length > TOI_DA ? 'Còn ' + (kq.length - TOI_DA) + ' kết quả nữa, gõ thêm để thu hẹp' : (cacTu.length ? kq.length + ' kết quả' : '');
        ghiChu.style.display = ghiChu.textContent ? 'block' : 'none';
        viTri = -1;
        if (cacTu.length && dangHien.length) sang(0, false);      // dang tim: tu dong sang dong dau tien de bam Enter la chon
    }
    function esc(s) { return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) { return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c]; }); }

    // to sang dong thu i (cuon vao tam nhin neu can)
    function sang(i, cuon) {
        var cacDong = danhSach.querySelectorAll('.ss-it');
        if (viTri >= 0 && cacDong[viTri]) cacDong[viTri].classList.remove('sang');
        viTri = i;
        if (cacDong[i]) {
            cacDong[i].classList.add('sang');
            if (cuon) {
                var li = cacDong[i], ds = danhSach;
                if (li.offsetTop < ds.scrollTop) ds.scrollTop = li.offsetTop;
                else if (li.offsetTop + li.offsetHeight > ds.scrollTop + ds.clientHeight) ds.scrollTop = li.offsetTop + li.offsetHeight - ds.clientHeight;
            }
        }
    }

    // ---------- chon 1 dong ----------
    function chon(m) {
        if (!m || m.khoa || !dangMo) return;
        var sel = dangMo;
        var doi = sel.selectedIndex !== m.index;
        sel.selectedIndex = m.index;
        dong(true);
        // note code nhu nay nhe ban: ban su kien nhu nguoi dung chon that -> onchange / addEventListener('change') / form tu submit van chay
        if (doi) {
            sel.dispatchEvent(new Event('input', {bubbles: true}));
            sel.dispatchEvent(new Event('change', {bubbles: true}));
        }
    }

    // ---------- ban phim ----------
    function phimTrongKhung(e) {
        if (e.key === 'ArrowDown') { e.preventDefault(); if (dangHien.length) sang(Math.min(dangHien.length - 1, viTri + 1), true); }
        else if (e.key === 'ArrowUp') { e.preventDefault(); if (dangHien.length) sang(Math.max(0, viTri - 1), true); }
        else if (e.key === 'Enter') { e.preventDefault(); if (viTri >= 0) chon(dangHien[viTri]); }
        else if (e.key === 'Escape') { e.preventDefault(); e.stopPropagation(); dong(true); }
        else if (e.key === 'Tab') { dong(false); }
    }

    // ---------- bat su kien toan trang (capture = chay truoc moi xu ly khac) ----------
    document.addEventListener('mousedown', function (e) {
        var sel = e.target.closest ? e.target.closest('select') : null;
        if (duocPhep(sel)) {
            e.preventDefault();                   // chan popup mac dinh cua trinh duyet
            sel.focus();
            if (dangMo === sel && khung.classList.contains('mo')) dong(false); else mo(sel);
        } else if (khung && khung.classList.contains('mo') && !khung.contains(e.target)) {
            dong(false);                          // bam ra ngoai thi dong
        }
    }, true);

    // select dang focus (khong mo): mui ten / Enter / Space mo khung; go chu cai thi mo khung va dien san chu do
    document.addEventListener('keydown', function (e) {
        var sel = e.target;
        if (!duocPhep(sel) || e.ctrlKey || e.metaKey || e.altKey) return;
        if (e.key === 'ArrowDown' || e.key === 'ArrowUp' || e.key === 'Enter' || e.key === ' ') {
            e.preventDefault(); mo(sel);
        } else if (e.key.length === 1) {
            e.preventDefault(); mo(sel, e.key);
        }
    }, true);

    window.addEventListener('resize', datViTri);
    window.addEventListener('scroll', function (e) {
        if (khung && khung.contains(e.target)) return;      // cuon ben trong danh sach thi giu nguyen
        datViTri();
    }, true);
})();

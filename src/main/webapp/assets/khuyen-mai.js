/* =====================================================================
 * KHUYEN MAI - POPUP THEM / SUA: tim kiem + tich chon san pham + tinh gia sau giam + tong ket
 *
 * note code nhu nay nhe ban:
 *  - Moi dong san pham la <tr class="km-dong" data-id data-gia data-tim> (ve boi dong-san-pham.jspf).
 *  - Dong CHUA tich: cac o nhap bi disabled nen KHONG gui len server. Tich vao thi mo khoa + tu dien "muc giam mac dinh".
 *  - Go o "Tim ma hoac ten san pham": loc NGAY cac dong dang co (khong phan biet hoa thuong / dau),
 *    dong thoi hoi server them san pham khop (KhuyenMaiTimSanPhamServlet) roi them vao bang.
 *  - Dong da tich nhung bi loc an van nam trong form nen van duoc luu.
 * ===================================================================== */
(function () {
    'use strict';
    var form = document.getElementById('km-form');
    if (!form) return;                                       // trang khong co popup thi thoi
    var nen = document.getElementById('km-nen');
    document.body.classList.add('km-khoa-cuon');

    // ---------- dong popup ----------
    var daSua = false;                                       // nguoi dung da go gi chua -> hoi truoc khi dong de khoi mat du lieu
    form.addEventListener('input', function () { daSua = true; });
    function dongPopup() {
        if (daSua && !window.confirm('Đóng cửa sổ? Dữ liệu chưa lưu sẽ mất.')) return;
        window.location.href = nen.getAttribute('data-dong');
    }
    // X va Huy la link co san; chan lai de hoi truoc khi thoat
    Array.prototype.forEach.call(form.querySelectorAll('.km-x, .km-nut a'), function (a) {
        a.addEventListener('click', function (e) { e.preventDefault(); dongPopup(); });
    });
    // Esc dong popup (neu khung tim kiem cua dropdown dang mo thi de khung do xu ly Esc truoc)
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && !document.querySelector('.ss-khung.mo')) dongPopup();
    });

    function baoLoi(noiDung) {
        var o = document.getElementById('km-loi');
        o.textContent = noiDung;
        o.hidden = false;
        form.querySelector('.km-than').scrollTop = 0;       // cuon len dau de thay thong bao
    }

    var tbody = document.getElementById('km-tbody');
    if (!tbody) return;                                      // chuong trinh da ket thuc: popup khong co bang san pham

    var oTim = document.getElementById('km-tim');
    var chonTatCa = document.getElementById('km-chon-tat-ca');
    var chiDaChon = document.getElementById('km-chi-da-chon');
    var elDem = document.getElementById('km-dem');
    var elTong = document.getElementById('km-tong');
    var ghiChu = document.getElementById('km-ghi-chu');
    var loaiMd = document.getElementById('km-loai-md');
    var giaTriMd = document.getElementById('km-gia-tri-md');
    var urlTim = form.getAttribute('data-tim');

    // ---------- tien ich ----------
    // bo dau tieng Viet + chu thuong: "Vợt" -> "vot"
    function chuanHoa(s) {
        return String(s || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[đĐ]/g, 'd').toLowerCase();
    }
    // note code nhu nay nhe ban: dinh dang tien GIONG ph:tien ben JSP (4.890.000đ) de so tren man hinh khong lech
    function tien(n) { return String(Math.round(n)).replace(/\B(?=(\d{3})+(?!\d))/g, '.') + 'đ'; }
    function so(v) { var n = parseFloat(String(v == null ? '' : v).replace(',', '.')); return isNaN(n) ? 0 : n; }
    // note code nhu nay nhe ban: cong thuc GIONG HET KhuyenMaiService.tinhGia ben Java
    // PHAN_TRAM: gia x (100 - %) / 100 lam tron; SO_TIEN: gia - so tien; ket qua luon nam trong [0, gia goc]
    function tinhGia(gia, loai, giaTri) {
        var g = loai === 'PHAN_TRAM' ? Math.round(gia * (100 - giaTri) / 100) : gia - giaTri;
        if (g < 0) g = 0;
        if (g > gia) g = gia;
        return Math.round(g);
    }
    function cacDong() { return tbody.querySelectorAll('tr.km-dong'); }
    function oChon(tr) { return tr.querySelector('input[name="chon"]'); }
    function loaiCua(tr) { var r = tr.querySelector('input[type="radio"]:checked'); return r ? r.value : 'PHAN_TRAM'; }
    function tenCua(tr) { var b = tr.querySelector('.km-c-sp b'); return b ? b.textContent : ''; }

    // ---------- cap nhat 1 dong / tong ket ----------
    function capNhatDong(tr) {
        var chon = oChon(tr).checked;
        tr.classList.toggle('chon', chon);
        // o nhap chi duoc GUI LEN khi dong duoc tich
        Array.prototype.forEach.call(tr.querySelectorAll('input:not([name="chon"])'), function (i) { i.disabled = !chon; });
        var giaTri = so(tr.querySelector('.km-giatri').value);
        tr.querySelector('.km-sau').textContent = chon && giaTri > 0 ? tien(tinhGia(+tr.getAttribute('data-gia'), loaiCua(tr), giaTri)) : '—';
    }
    function capNhatTong() {
        var n = 0, goc = 0, sau = 0;
        Array.prototype.forEach.call(cacDong(), function (tr) {
            if (!oChon(tr).checked) return;
            var gia = +tr.getAttribute('data-gia'), giaTri = so(tr.querySelector('.km-giatri').value);
            n++;
            goc += gia;
            sau += giaTri > 0 ? tinhGia(gia, loaiCua(tr), giaTri) : gia;
        });
        elDem.textContent = n;
        elTong.innerHTML = 'Đã chọn: <b>' + n + '</b> sản phẩm · Tổng giá gốc: <b>' + tien(goc) + '</b> · Tổng sau giảm: <b>' + tien(sau) + '</b>';
    }
    // o "chon tat ca" tick khi MOI dong dang hien deu da tich
    function capNhatChonTatCa() {
        var hien = 0, daTich = 0;
        Array.prototype.forEach.call(cacDong(), function (tr) {
            if (tr.hidden) return;
            hien++;
            if (oChon(tr).checked) daTich++;
        });
        chonTatCa.checked = hien > 0 && hien === daTich;
    }

    // tich chon 1 dong chua co muc giam -> tu dien muc giam mac dinh o tren
    function dienMacDinh(tr) {
        var o = tr.querySelector('.km-giatri');
        if (o.value === '' && so(giaTriMd.value) > 0) {
            o.value = giaTriMd.value;
            var r = tr.querySelector('input[type="radio"][value="' + loaiMd.value + '"]');
            if (r) r.checked = true;
        }
    }

    // ---------- loc theo tu khoa + "chi hien da chon" ----------
    function loc() {
        var tu = chuanHoa(oTim.value).trim().split(/\s+/).filter(Boolean);
        var chiChon = chiDaChon.checked;
        var thay = 0;
        Array.prototype.forEach.call(cacDong(), function (tr) {
            if (tr._tim === undefined) tr._tim = chuanHoa(tr.getAttribute('data-tim'));    // chuan hoa 1 lan roi nho lai
            var khop = tu.every(function (t) { return tr._tim.indexOf(t) >= 0; });         // moi tu khoa deu phai co mat
            var hien = khop && (!chiChon || oChon(tr).checked);
            tr.hidden = !hien;
            if (hien) thay++;
        });
        ghiChu.textContent = thay === 0 ? 'Không tìm thấy sản phẩm phù hợp.'
            : tu.length ? 'Tìm thấy ' + thay + ' sản phẩm.' : 'Đang hiển thị ' + thay + ' sản phẩm. Gõ vào ô tìm kiếm để tìm thêm.';
        capNhatChonTatCa();
    }

    // ---------- hoi server them san pham khop tu khoa ----------
    var lanTim = 0, hen = null;
    function hoiServer() {
        var tuKhoa = oTim.value.trim();
        var lan = ++lanTim;
        if (!tuKhoa) return;                                 // khong go gi thi khong can hoi
        fetch(urlTim + '?tuKhoa=' + encodeURIComponent(tuKhoa), { credentials: 'same-origin' })
            .then(function (r) { return r.ok ? r.text() : ''; })
            .then(function (html) {
                if (lan !== lanTim) return;                  // da co lan go moi hon -> bo ket qua cu
                var t = document.createElement('tbody');
                t.innerHTML = html;
                Array.prototype.forEach.call(t.querySelectorAll('tr.km-dong'), function (tr) {
                    // san pham da co trong bang (vd da tich) thi khong them lai
                    if (!tbody.querySelector('tr[data-id="' + tr.getAttribute('data-id') + '"]')) {
                        tbody.appendChild(tr);
                        capNhatDong(tr);
                    }
                });
                loc();
            })
            .catch(function () { /* mat mang: van loc duoc tren cac dong dang co */ });
    }
    oTim.addEventListener('input', function () {
        loc();                                               // loc ngay tren cac dong dang co
        clearTimeout(hen);
        hen = setTimeout(hoiServer, 300);                    // go xong 0.3 giay moi hoi server (do gui qua nhieu yeu cau)
    });
    oTim.addEventListener('keydown', function (e) { if (e.key === 'Enter') e.preventDefault(); });   // Enter o o tim kiem khong duoc luu form

    // ---------- su kien trong bang ----------
    tbody.addEventListener('change', function (e) {
        var tr = e.target.closest('tr.km-dong');
        if (!tr) return;
        if (e.target.name === 'chon') {
            if (e.target.checked) dienMacDinh(tr);
            capNhatDong(tr);
            if (chiDaChon.checked) loc();                    // dang "chi hien da chon" ma bo tich thi dong do bien mat
        } else {
            capNhatDong(tr);                                 // doi loai giam (radio)
        }
        capNhatTong();
        capNhatChonTatCa();
    });
    tbody.addEventListener('input', function (e) {
        var tr = e.target.closest('tr.km-dong');
        if (!tr) return;
        capNhatDong(tr);
        capNhatTong();
    });

    chiDaChon.addEventListener('change', loc);
    // chon / bo chon TAT CA cac dong dang hien (dang loc thi chi anh huong ket qua loc)
    chonTatCa.addEventListener('change', function () {
        Array.prototype.forEach.call(cacDong(), function (tr) {
            if (tr.hidden) return;
            var o = oChon(tr);
            if (o.checked === chonTatCa.checked) return;
            o.checked = chonTatCa.checked;
            if (o.checked) dienMacDinh(tr);
            capNhatDong(tr);
        });
        capNhatTong();
        if (chiDaChon.checked) loc();
    });

    // ---------- "Ap dung cho san pham da chon" ----------
    function apDungMacDinh() {
        if (!(so(giaTriMd.value) > 0)) { baoLoi('Hãy nhập giá trị giảm mặc định lớn hơn 0'); giaTriMd.focus(); return; }
        var n = 0;
        Array.prototype.forEach.call(cacDong(), function (tr) {
            if (!oChon(tr).checked) return;
            tr.querySelector('.km-giatri').value = giaTriMd.value;
            var r = tr.querySelector('input[type="radio"][value="' + loaiMd.value + '"]');
            if (r) r.checked = true;
            capNhatDong(tr);
            n++;
        });
        if (!n) baoLoi('Chưa tích chọn sản phẩm nào để áp dụng mức giảm');
        capNhatTong();
    }
    document.getElementById('km-ap-dung').addEventListener('click', apDungMacDinh);
    giaTriMd.addEventListener('keydown', function (e) { if (e.key === 'Enter') { e.preventDefault(); apDungMacDinh(); } });

    // ---------- kiem tra truoc khi luu (server van kiem tra lai day du) ----------
    form.addEventListener('submit', function (e) {
        var dem = 0, dongs = cacDong();
        for (var i = 0; i < dongs.length; i++) {
            var tr = dongs[i];
            if (!oChon(tr).checked) continue;
            dem++;
            var o = tr.querySelector('.km-giatri'), v = so(o.value), loai = loaiCua(tr), nhan = '"' + tenCua(tr) + '": ';
            var loi = '';
            if (!(v > 0)) loi = 'hãy nhập mức giảm lớn hơn 0';
            else if (loai === 'PHAN_TRAM' && v > 100) loi = 'phần trăm giảm tối đa là 100';
            else if (loai === 'SO_TIEN' && v >= +tr.getAttribute('data-gia')) loi = 'số tiền giảm phải nhỏ hơn giá gốc';
            if (loi) {
                e.preventDefault();
                tr.hidden = false;                           // dong loi dang bi loc an thi hien ra de nguoi dung thay
                baoLoi(nhan + loi);
                o.focus();
                return;
            }
        }
        if (!dem) { e.preventDefault(); baoLoi('Vui lòng tích chọn ít nhất 1 sản phẩm áp dụng khuyến mãi'); }
    });

    // ---------- khoi tao ----------
    Array.prototype.forEach.call(cacDong(), capNhatDong);
    capNhatTong();
    loc();
})();

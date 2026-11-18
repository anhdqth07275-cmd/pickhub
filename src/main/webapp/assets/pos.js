/* ============================================================================
 * PickHub POS - logic man hinh ban hang (JavaScript thuan, khong thu vien).
 * Trang thai nhieu hoa don (tab) luu o localStorage nen F5 / mat dien van con.
 * Gia gui len server KHONG duoc tin: server tinh lai gia + kiem tra ton khi thanh toan.
 * ========================================================================== */
(function () {
    'use strict';
    var KHOA_LUU = 'pickhub_pos_bills_v1';
    var bills = [];
    var dangChon = 0;
    var trangHienTai = 1;
    var hen = null;

    // ---------- tien ich ----------
    function $(id) { return document.getElementById(id); }
    function tien(n) { return Math.round(Number(n) || 0).toLocaleString('vi-VN'); }
    function so(chuoi) { var s = String(chuoi || '').replace(/[^\d]/g, ''); return s ? parseInt(s, 10) : 0; }
    function esc(s) { return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) { return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c]; }); }
    function anhUrl(duongDan) { return !duongDan ? CTX + '/assets/no-image.png' : (duongDan.indexOf('http') === 0 ? duongDan : CTX + duongDan); }
    function toast(noiDung, loi) {
        var t = $('toast'); t.textContent = noiDung; t.className = loi ? 'loi' : ''; t.style.display = 'block';
        clearTimeout(t._h); t._h = setTimeout(function () { t.style.display = 'none'; }, 3200);
    }
    function maYeuCauMoi() { return 'Y' + Date.now() + Math.random().toString(36).slice(2, 8); }
    function hoaDonMoi(stt) {
        return {ten: 'Hóa đơn ' + stt, dong: [], maGiamGia: '', giamGia: 0, phuongThuc: 'TIEN_MAT', tienKhachDua: 0, tenKhach: '', sdt: '', ghiChu: '', maYeuCau: maYeuCauMoi()};
    }
    function luu() { try { localStorage.setItem(KHOA_LUU, JSON.stringify({bills: bills, dangChon: dangChon})); } catch (e) { } }
    function nap() {
        try {
            var d = JSON.parse(localStorage.getItem(KHOA_LUU) || 'null');
            if (d && d.bills && d.bills.length) { bills = d.bills; dangChon = Math.min(d.dangChon || 0, bills.length - 1); }
        } catch (e) { }
        if (!bills.length) { bills = [hoaDonMoi(1)]; dangChon = 0; }
    }
    function hd() { return bills[dangChon]; }
    function tamTinh(b) { var t = 0; b.dong.forEach(function (d) { t += d.giaBan * d.sl; }); return t; }
    function tongTien(b) { return Math.max(0, tamTinh(b) - (b.giamGia || 0)); }

    // ---------- goi API ----------
    function goi(url, tuyChon) {
        return fetch(url, tuyChon).then(function (r) {
            if (r.status === 401) { toast('Hết phiên đăng nhập, đang chuyển về trang đăng nhập...', true); setTimeout(function () { location.href = CTX + '/dang-nhap'; }, 1200); throw new Error('401'); }
            return r.json();
        });
    }

    // ---------- bo loc + luoi san pham ----------
    var truongLoc = ['danhMuc', 'thuongHieu', 'loai', 'mau', 'khoiLuong', 'chatLieu', 'chuVi', 'giaTu', 'giaDen', 'ton', 'sapXep'];
    function taoQuery(trang) {
        var p = new URLSearchParams();
        var tk = $('o-tim').value.trim(); if (tk) p.set('tuKhoa', tk);
        truongLoc.forEach(function (k) { var v = $('loc-' + k).value.trim(); if (v) p.set(k, v); });
        if ($('loc-km').checked) p.set('km', '1');
        p.set('trang', trang || 1);
        return p.toString();
    }
    function taiSanPham(trang) {
        trangHienTai = trang || 1;
        $('luoi').innerHTML = '<div class="rong">Đang tải...</div>';
        goi(CTX + '/pos/api/san-pham?' + taoQuery(trangHienTai)).then(function (kq) {
            if (!kq.ok) { $('luoi').innerHTML = '<div class="rong">' + esc(kq.thongBao) + '</div>'; return; }
            veLuoi(kq);
        }).catch(function (e) { if (e.message !== '401') { $('luoi').innerHTML = '<div class="rong">Không tải được danh sách sản phẩm</div>'; } });
    }
    function veLuoi(kq) {
        $('so-ket-qua').textContent = kq.tongSo + ' mặt hàng';
        if (!kq.ds.length) { $('luoi').innerHTML = '<div class="rong">Không có mặt hàng phù hợp bộ lọc.<br>Thử bỏ bớt điều kiện lọc.</div>'; $('phan-trang').innerHTML = ''; return; }
        var h = '';
        kq.ds.forEach(function (m) {
            var het = m.ton <= 0 || m.trangThai !== 'DANG_BAN';
            var it = !het && m.ton <= m.nguong;
            var km = m.giaBan < m.giaGoc ? '<span class="km">-' + Math.round((m.giaGoc - m.giaBan) * 100 / m.giaGoc) + '%</span>' : '';
            h += '<div class="the' + (het ? ' het' : '') + '" data-id="' + m.id + '">' + km +
                '<span class="ton' + (het ? ' het' : (it ? ' it' : '')) + '">' + (het ? 'Hết hàng' : 'Còn ' + m.ton) + '</span>' +
                '<div class="anh"><img loading="lazy" src="' + esc(anhUrl(m.anh)) + '" alt="" onerror="this.onerror=null;this.src=\'' + CTX + '/assets/no-image.png\'"></div>' +
                '<div class="tt"><div class="ten">' + esc(m.ten) + '</div><div class="mo-ta">' + esc(m.moTa || m.hieu) + '</div>' +
                '<div class="sku">' + esc(m.sku) + '</div>' +
                '<div class="gia">' + tien(m.giaBan) + ' đ' + (km ? '<s>' + tien(m.giaGoc) + '</s>' : '') + '</div></div></div>';
        });
        $('luoi').innerHTML = h;
        $('luoi').scrollTop = 0;
        $('luoi')._ds = kq.ds;
        // phan trang
        var pt = '';
        if (kq.tongTrang > 1) {
            var tu = Math.max(1, kq.trang - 3), den = Math.min(kq.tongTrang, kq.trang + 3);
            if (tu > 1) pt += '<button data-t="1">1</button>' + (tu > 2 ? '<span>…</span>' : '');
            for (var i = tu; i <= den; i++) pt += '<button data-t="' + i + '"' + (i === kq.trang ? ' class="on"' : '') + '>' + i + '</button>';
            if (den < kq.tongTrang) pt += (den < kq.tongTrang - 1 ? '<span>…</span>' : '') + '<button data-t="' + kq.tongTrang + '">' + kq.tongTrang + '</button>';
        }
        $('phan-trang').innerHTML = pt;
    }
    function locLai() { clearTimeout(hen); hen = setTimeout(function () { taiSanPham(1); }, 300); }
    function xoaLoc() {
        $('o-tim').value = '';
        truongLoc.forEach(function (k) { var e = $('loc-' + k); if (k === 'ton') e.value = 'CON_HANG'; else if (k === 'sapXep') e.value = 'ten'; else e.value = ''; });
        $('loc-km').checked = false;
        document.querySelectorAll('.chips-nhanh button').forEach(function (x, i) { x.classList.toggle('on', i === 0); });
        taiSanPham(1);
    }

    // ---------- hoa don ----------
    function themMatHang(m, sl) {
        if (m.ton <= 0 || (m.trangThai && m.trangThai !== 'DANG_BAN')) { toast('Mặt hàng đã hết hàng', true); return; }
        var b = hd();
        var dong = null;
        b.dong.forEach(function (d) { if (d.id === m.id) dong = d; });
        var moi = (dong ? dong.sl : 0) + (sl || 1);
        if (moi > m.ton) { toast('"' + m.ten + '" chỉ còn ' + m.ton + ' cái', true); return; }
        if (dong) { dong.sl = moi; dong.giaBan = m.giaBan; dong.giaGoc = m.giaGoc; dong.ton = m.ton; }
        else { b.dong.push({id: m.id, sku: m.sku, ten: m.ten, moTa: m.moTa, giaBan: m.giaBan, giaGoc: m.giaGoc, ton: m.ton, sl: sl || 1}); }
        veHoaDon(); kiemTraPhieuSau();
    }
    function doiSL(id, sl) {
        var b = hd();
        for (var i = 0; i < b.dong.length; i++) {
            if (b.dong[i].id === id) {
                if (sl <= 0) { b.dong.splice(i, 1); }
                else if (sl > b.dong[i].ton) { toast('Chỉ còn ' + b.dong[i].ton + ' cái', true); b.dong[i].sl = b.dong[i].ton; }
                else { b.dong[i].sl = sl; }
                break;
            }
        }
        veHoaDon(); kiemTraPhieuSau();
    }
    function veTabs() {
        var h = '';
        bills.forEach(function (b, i) {
            var n = 0; b.dong.forEach(function (d) { n += d.sl; });
            h += '<div class="tab' + (i === dangChon ? ' on' : '') + '" data-i="' + i + '">' + esc(b.ten) + (n ? ' <span class="dem">' + n + '</span>' : '') +
                (bills.length > 1 ? ' <span class="dong" data-x="' + i + '" title="Đóng hóa đơn">✕</span>' : '') + '</div>';
        });
        h += '<div class="them" id="them-hd" title="Hóa đơn mới (F4)">+</div>';
        $('tabs').innerHTML = h;
    }
    function veHoaDon() {
        var b = hd();
        veTabs();
        if (!b.dong.length) { $('dong-hd').innerHTML = '<div class="trong">Chưa có mặt hàng.<br>Bấm vào sản phẩm hoặc quét mã hàng để thêm.</div>'; }
        else {
            var h = '';
            b.dong.forEach(function (d) {
                h += '<div class="mh"><div><div class="ten">' + esc(d.ten) + '</div><div class="phu">' + esc(d.moTa) + ' · ' + esc(d.sku) + '</div></div>' +
                    '<div class="tien">' + tien(d.giaBan * d.sl) + '</div>' +
                    '<div class="dieu-khien"><div class="stepper"><button data-g="' + d.id + '">−</button><input type="text" inputmode="numeric" value="' + d.sl + '" data-s="' + d.id + '"><button data-t="' + d.id + '">+</button></div>' +
                    '<span class="don-gia">× ' + tien(d.giaBan) + ' đ</span><button class="xoa" data-d="' + d.id + '">Xóa</button></div></div>';
            });
            $('dong-hd').innerHTML = h;
        }
        // phieu giam gia
        $('o-phieu').value = b.maGiamGia || '';
        $('phieu-ok').style.display = b.giamGia ? 'flex' : 'none';
        $('phieu-ok-ten').textContent = b.giamGia ? 'Đã áp mã ' + b.maGiamGia : '';
        $('t-tamtinh').textContent = tien(tamTinh(b)) + ' đ';
        $('t-giam').textContent = '- ' + tien(b.giamGia || 0) + ' đ';
        $('t-tong').textContent = tien(tongTien(b)) + ' đ';
        // thanh toan
        var pts = document.querySelectorAll('.pt-chon button');
        for (var i = 0; i < pts.length; i++) { pts[i].classList.toggle('on', pts[i].dataset.pt === b.phuongThuc); }
        var tm = b.phuongThuc === 'TIEN_MAT';
        $('khu-tien-mat').style.display = tm ? 'block' : 'none';
        $('o-dua').value = b.tienKhachDua ? tien(b.tienKhachDua) : '';
        $('o-khach').value = b.tenKhach; $('o-sdt').value = b.sdt; $('o-ghichu').value = b.ghiChu;
        veTienThua();
        $('btn-tra').disabled = !b.dong.length;
        veNhanh();
        luu();
    }
    function veTienThua() {
        var b = hd(), t = tongTien(b), e = $('t-thua');
        if (b.phuongThuc !== 'TIEN_MAT') return;
        var thua = (b.tienKhachDua || 0) - t;
        if (!b.tienKhachDua) { e.textContent = '—'; e.className = 'tien-thua'; }
        else if (thua < 0) { e.textContent = 'Thiếu ' + tien(-thua) + ' đ'; e.className = 'tien-thua thieu'; }
        else { e.textContent = tien(thua) + ' đ'; e.className = 'tien-thua du'; }
    }
    function veNhanh() {
        var t = tongTien(hd()), goiY = [], h = '';
        if (t > 0) {
            goiY.push(t);
            [10000, 50000, 100000, 200000, 500000].forEach(function (m) { var v = Math.ceil(t / m) * m; if (goiY.indexOf(v) < 0 && goiY.length < 5) goiY.push(v); });
            goiY.forEach(function (v) { h += '<button data-v="' + v + '">' + tien(v) + '</button>'; });
        }
        $('nhanh-tien').innerHTML = h;
    }

    // ---------- ma giam gia (xem truoc) ----------
    var henPhieu = null;
    function kiemTraPhieuSau() { clearTimeout(henPhieu); henPhieu = setTimeout(kiemTraPhieu, 250); }
    function kiemTraPhieu(thongBaoLoi) {
        var b = hd();
        if (!b.maGiamGia) { b.giamGia = 0; capNhatTong(); return; }
        goi(CTX + '/pos/api/phieu-giam-gia?ma=' + encodeURIComponent(b.maGiamGia) + '&tamTinh=' + tamTinh(b)).then(function (kq) {
            if (kq.ok) { b.giamGia = kq.soTienGiam; }
            else { toast(kq.thongBao, true); b.maGiamGia = ''; b.giamGia = 0; $('o-phieu').value = ''; }
            veHoaDon();
        }).catch(function () { });
    }
    function capNhatTong() { veHoaDon(); }

    // ---------- thanh toan ----------
    function thanhToan() {
        var b = hd();
        if (!b.dong.length) { toast('Hóa đơn chưa có mặt hàng', true); return; }
        var tong = tongTien(b);
        if (b.phuongThuc === 'TIEN_MAT' && (b.tienKhachDua || 0) < tong) { toast('Tiền khách đưa chưa đủ', true); $('o-dua').focus(); return; }
        var nut = $('btn-tra'); nut.disabled = true; nut.textContent = 'Đang xử lý...';
        var p = new URLSearchParams();
        p.set('dong', b.dong.map(function (d) { return d.id + ':' + d.sl; }).join(','));
        p.set('maGiamGia', b.maGiamGia || ''); p.set('phuongThuc', b.phuongThuc);
        p.set('tienKhachDua', b.phuongThuc === 'TIEN_MAT' ? b.tienKhachDua : tong);
        p.set('tenKhach', b.tenKhach); p.set('sdtKhach', b.sdt); p.set('ghiChu', b.ghiChu);
        p.set('maYeuCau', b.maYeuCau);       // chong gui trung hoa don neu bam 2 lan
        goi(CTX + '/pos/api/thanh-toan', {method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'}, body: p.toString()})
            .then(function (kq) {
                nut.textContent = 'THANH TOÁN (F9)'; nut.disabled = false;
                if (!kq.ok) { toast(kq.thongBao, true); taiSanPham(trangHienTai); return; }
                hienThanhCong(kq);
                // xoa hoa don vua thanh toan, giu lai cac hoa don khac
                if (bills.length > 1) { bills.splice(dangChon, 1); dangChon = Math.max(0, dangChon - 1); }
                else { bills = [hoaDonMoi(1)]; dangChon = 0; }
                veHoaDon();
                taiSanPham(trangHienTai);        // cap nhat lai ton kho tren luoi
                capNhatCa(kq.tongTien);
            }).catch(function (e) { nut.textContent = 'THANH TOÁN (F9)'; nut.disabled = false; if (e.message !== '401') toast('Không kết nối được máy chủ, hóa đơn CHƯA được tạo', true); });
    }
    function hienThanhCong(kq) {
        $('tc-ma').textContent = kq.maTraCuu;
        $('tc-tong').textContent = tien(kq.tongTien) + ' đ';
        $('tc-thua').textContent = tien(kq.tienThua) + ' đ';
        $('tc-thua-dong').style.display = kq.tienThua > 0 ? 'block' : 'none';
        var linkIn = CTX + '/pos/in-hoa-don?id=' + kq.maHoaDon + '&tuDong=1';
        $('tc-in').onclick = function () { window.open(linkIn, '_blank', 'width=420,height=720'); };
        $('lop-tc').classList.add('mo');
        $('tc-in').focus();
    }
    function capNhatCa(them) {
        var e = $('ca-so'), d = $('ca-tien');
        e.textContent = (parseInt(e.textContent, 10) || 0) + 1;
        d.textContent = tien(so(d.textContent) + them);
    }

    // ---------- gan su kien ----------
    function ganSuKien() {
        $('luoi').addEventListener('click', function (e) {
            var the = e.target.closest('.the'); if (!the) return;
            var id = parseInt(the.dataset.id, 10), ds = $('luoi')._ds || [];
            for (var i = 0; i < ds.length; i++) { if (ds[i].id === id) { themMatHang(ds[i], 1); break; } }
        });
        $('phan-trang').addEventListener('click', function (e) { if (e.target.dataset.t) taiSanPham(parseInt(e.target.dataset.t, 10)); });
        $('o-tim').addEventListener('input', locLai);
        $('o-tim').addEventListener('keydown', function (e) {
            if (e.key !== 'Enter') return;
            e.preventDefault();
            var tu = this.value.trim(); if (!tu) return;
            if (/\s/.test(tu)) { taiSanPham(1); return; }
            // 1 tu lien: co the la MA HANG (quet ma vach) -> them thang vao hoa don
            goi(CTX + '/pos/api/san-pham?sku=' + encodeURIComponent(tu)).then(function (kq) {
                if (kq.ok) { themMatHang(kq.item, 1); $('o-tim').value = ''; taiSanPham(1); }
                else { taiSanPham(1); }
            }).catch(function () { });
        });
        truongLoc.forEach(function (k) { $('loc-' + k).addEventListener(k === 'giaTu' || k === 'giaDen' ? 'input' : 'change', locLai); });
        $('loc-km').addEventListener('change', locLai);
        $('btn-xoa-loc').onclick = xoaLoc;
        $('btn-loc').onclick = function () { $('bo-loc').classList.toggle('mo'); };
        document.querySelectorAll('.chips-nhanh button').forEach(function (b) {
            b.onclick = function () { $('loc-loai').value = b.dataset.loai; document.querySelectorAll('.chips-nhanh button').forEach(function (x) { x.classList.toggle('on', x === b); }); taiSanPham(1); };
        });

        $('tabs').addEventListener('click', function (e) {
            if (e.target.dataset.x !== undefined) {
                var i = parseInt(e.target.dataset.x, 10);
                if (bills[i].dong.length && !confirm('Đóng hóa đơn "' + bills[i].ten + '" đang có hàng?')) return;
                bills.splice(i, 1); dangChon = Math.min(dangChon, bills.length - 1); veHoaDon(); return;
            }
            if (e.target.id === 'them-hd') { hoaDonMoiTab(); return; }
            var tab = e.target.closest('.tab'); if (tab) { dangChon = parseInt(tab.dataset.i, 10); veHoaDon(); }
        });
        $('dong-hd').addEventListener('click', function (e) {
            var d = e.target.dataset;
            var b = hd();
            function dong(id) { for (var i = 0; i < b.dong.length; i++) if (b.dong[i].id === id) return b.dong[i]; return null; }
            if (d.g) { var x = dong(parseInt(d.g, 10)); if (x) doiSL(x.id, x.sl - 1); }
            if (d.t) { var y = dong(parseInt(d.t, 10)); if (y) doiSL(y.id, y.sl + 1); }
            if (d.d) { doiSL(parseInt(d.d, 10), 0); }
        });
        $('dong-hd').addEventListener('change', function (e) { if (e.target.dataset.s) doiSL(parseInt(e.target.dataset.s, 10), so(e.target.value)); });

        $('btn-phieu').onclick = function () { var b = hd(); b.maGiamGia = $('o-phieu').value.trim().toUpperCase(); if (!b.maGiamGia) { b.giamGia = 0; veHoaDon(); return; } kiemTraPhieu(); };
        $('o-phieu').addEventListener('keydown', function (e) { if (e.key === 'Enter') { e.preventDefault(); $('btn-phieu').click(); } });
        $('btn-bo-phieu').onclick = function () { var b = hd(); b.maGiamGia = ''; b.giamGia = 0; veHoaDon(); };

        document.querySelector('.pt-chon').addEventListener('click', function (e) {
            if (!e.target.dataset.pt) return;
            hd().phuongThuc = e.target.dataset.pt; veHoaDon();
        });
        $('o-dua').addEventListener('input', function () { hd().tienKhachDua = so(this.value); this.value = hd().tienKhachDua ? tien(hd().tienKhachDua) : ''; veTienThua(); luu(); });
        $('nhanh-tien').addEventListener('click', function (e) { if (e.target.dataset.v) { hd().tienKhachDua = parseInt(e.target.dataset.v, 10); veHoaDon(); } });
        $('o-khach').addEventListener('input', function () { hd().tenKhach = this.value; luu(); });
        $('o-sdt').addEventListener('input', function () { hd().sdt = this.value; luu(); });
        $('o-ghichu').addEventListener('input', function () { hd().ghiChu = this.value; luu(); });
        $('btn-tra').onclick = thanhToan;
        $('btn-xoa-hd').onclick = function () {
            if (hd().dong.length && !confirm('Xóa toàn bộ mặt hàng trong hóa đơn này?')) return;
            var ten = hd().ten; bills[dangChon] = hoaDonMoi(1); bills[dangChon].ten = ten; veHoaDon();
        };
        $('tc-moi').onclick = function () { $('lop-tc').classList.remove('mo'); $('o-tim').focus(); };

        document.addEventListener('keydown', function (e) {
            if (e.key === 'F2') { e.preventDefault(); $('o-tim').focus(); $('o-tim').select(); }
            else if (e.key === 'F4') { e.preventDefault(); hoaDonMoiTab(); }
            else if (e.key === 'F9') { e.preventDefault(); if (!$('lop-tc').classList.contains('mo')) thanhToan(); }
            else if (e.key === 'Escape') { $('lop-tc').classList.remove('mo'); }
        });
        // dong ho
        function chay() { var n = new Date(); $('dong-ho').textContent = n.toLocaleTimeString('vi-VN', {hour: '2-digit', minute: '2-digit'}) + ' · ' + n.toLocaleDateString('vi-VN'); }
        chay(); setInterval(chay, 30000);
    }
    function hoaDonMoiTab() {
        if (bills.length >= 8) { toast('Tối đa 8 hóa đơn cùng lúc', true); return; }
        var stt = 1; bills.forEach(function (b) { var m = /(\d+)$/.exec(b.ten); if (m && parseInt(m[1], 10) >= stt) stt = parseInt(m[1], 10) + 1; });
        bills.push(hoaDonMoi(stt)); dangChon = bills.length - 1; veHoaDon();
    }

    window.addEventListener('DOMContentLoaded', function () {
        nap(); ganSuKien(); veHoaDon(); taiSanPham(1); $('o-tim').focus();
        // luc mo trang: kiem tra lai ma giam gia cua hoa don dang chon (neu co)
        if (hd().maGiamGia) kiemTraPhieu();
    });
})();

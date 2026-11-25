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
        p.set('nhom', '1');      // note code nhu nay nhe ban: nhom=1 -> server tra theo SAN PHAM (moi san pham 1 the kem cac bien the), khong phai tung bien the
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
        $('so-ket-qua').textContent = kq.tongSo + ' sản phẩm';
        if (!kq.ds.length) { $('luoi').innerHTML = '<div class="rong">Không có sản phẩm phù hợp bộ lọc.<br>Thử bỏ bớt điều kiện lọc.</div>'; $('phan-trang').innerHTML = ''; $('luoi')._ds = []; return; }
        var h = '';
        kq.ds.forEach(function (sp) {
            // note code nhu nay nhe ban: moi SAN PHAM la 1 the thoang chi co ANH + TEN + GIA. Cac bien the (mau, khoi luong...) nam trong sp.bt
            // va chi hien khi bam vao the (popup chon bien the). So ton tren the do capNhatTonTrenGiao() ve (xem ben duoi).
            var giam = sp.giaGoc > sp.giaTu ? Math.round((sp.giaGoc - sp.giaTu) * 100 / sp.giaGoc) : 0;
            var km = giam > 0 ? '<span class="km">-' + giam + '%</span>' : (sp.km ? '<span class="km">KM</span>' : '');
            h += '<div class="the" data-sp="' + sp.id + '">' + km + '<span class="ton"></span>' +
                '<div class="anh"><img loading="lazy" src="' + esc(anhUrl(sp.anh)) + '" alt="" onerror="this.onerror=null;this.src=\'' + CTX + '/assets/no-image.png\'"></div>' +
                '<div class="tt"><div class="ten">' + esc(sp.ten) + '</div>' +
                '<div class="gia">' + (sp.giaTu !== sp.giaDen ? '<small>Từ</small>' : '') + tien(sp.giaTu) + ' đ' + (giam > 0 ? '<s>' + tien(sp.giaGoc) + '</s>' : '') + '</div>' +
                (sp.bt.length > 1 ? '<div class="bt-so">' + sp.bt.length + ' phân loại</div>' : '') + '</div></div>';
        });
        $('luoi').innerHTML = h;
        $('luoi').scrollTop = 0;
        $('luoi')._ds = kq.ds;
        capNhatTonTrenGiao();
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
    // tra true neu them duoc, false neu bi chan (het hang / het so con lai)
    function themMatHang(m, sl) {
        if (m.ton <= 0 || (m.trangThai && m.trangThai !== 'DANG_BAN')) { toast('Mặt hàng đã hết hàng', true); return false; }
        var can = sl || 1;
        // note code nhu nay nhe ban: kiem tra theo SO CON LAI = ton - so luong dang nam trong TAT CA hoa don cho (khong chi hoa don dang chon)
        var con = conLai(m);
        if (can > con) {
            toast(con <= 0 ? '"' + m.ten + '" đã được đưa hết vào các hóa đơn' : '"' + m.ten + '" chỉ còn ' + con + ' cái', true);
            return false;
        }
        var b = hd();
        var dong = null;
        b.dong.forEach(function (d) { if (d.id === m.id) dong = d; });
        if (dong) { dong.sl += can; dong.giaBan = m.giaBan; dong.giaGoc = m.giaGoc; dong.ton = m.ton; }
        else { b.dong.push({id: m.id, sku: m.sku, ten: m.ten, moTa: m.moTa, giaBan: m.giaBan, giaGoc: m.giaGoc, ton: m.ton, sl: can}); }
        veHoaDon(); kiemTraPhieuSau();      // veHoaDon -> capNhatTonTrenGiao: so ton tren the / popup giam ngay
        return true;
    }
    function doiSL(id, sl) {
        var b = hd();
        for (var i = 0; i < b.dong.length; i++) {
            if (b.dong[i].id === id) {
                if (sl <= 0) { b.dong.splice(i, 1); }
                else {
                    // so toi da duoc phep = ton - so luong dang nam o CAC HOA DON KHAC (phan o hoa don nay chinh la dong dang sua)
                    var toiDa = b.dong[i].ton - (daGiu(id) - b.dong[i].sl);
                    if (sl > toiDa) { toast('Chỉ còn ' + toiDa + ' cái (đã trừ phần ở các hóa đơn chờ khác)', true); b.dong[i].sl = Math.max(1, toiDa); }
                    else { b.dong[i].sl = sl; }
                }
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
        capNhatTonTrenGiao();      // moi lan hoa don doi (them / bot / xoa dong / dong tab...) thi so ton hien tren man hinh cung doi
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

    // ---------- popup xem va chon voucher ----------
    // note code nhu nay nhe ban: luong chay = bam "Voucher" -> moVoucher() goi API lay danh sach -> veVoucher() ve the
    // -> bam "Chon" -> chonVoucher(ma) gan ma vao hoa don dang chon roi goi kiemTraPhieu() (ham co san) de tinh so tien giam
    var dsVoucher = [];          // danh sach voucher vua lay tu server (giu lai de loc theo o tim kiem ma khong goi lai API)

    function moVoucher() {
        var tt = tamTinh(hd());
        $('lop-voucher').classList.add('mo');
        $('vc-tim').value = '';
        $('vc-ds').innerHTML = '<div class="rong">Đang tải voucher...</div>';
        $('vc-ghi-chu').textContent = tt > 0
            ? 'Tạm tính hiện tại: ' + tien(tt) + ' đ — voucher đủ điều kiện được xếp lên đầu.'
            : 'Hóa đơn chưa có mặt hàng. Bạn vẫn chọn được mã, số tiền giảm sẽ được tính khi thêm hàng.';
        goi(CTX + '/pos/api/phieu-giam-gia/danh-sach?tamTinh=' + tt).then(function (kq) {
            if (!kq.ok) { $('vc-ds').innerHTML = '<div class="rong">' + esc(kq.thongBao) + '</div>'; return; }
            dsVoucher = kq.ds;
            veVoucher();
        }).catch(function () { $('vc-ds').innerHTML = '<div class="rong">Không tải được danh sách voucher</div>'; });
    }

    function dongVoucher() { $('lop-voucher').classList.remove('mo'); }

    // "Giam 10% (toi da 300.000 d)" hoac "Giam 50.000 d"
    function mucGiamChu(v) {
        if (v.loai === 'PHAN_TRAM') return 'Giảm ' + v.giaTri + '%' + (v.giamToiDa ? ' (tối đa ' + tien(v.giamToiDa) + ' đ)' : '');
        return 'Giảm ' + tien(v.giaTri) + ' đ';
    }
    // chu to ben trai the: "10%" hoac "50K"
    function mucGiamNgan(v) {
        if (v.loai === 'PHAN_TRAM') return v.giaTri + '%';
        return v.giaTri % 1000 === 0 ? (v.giaTri / 1000) + 'K' : tien(v.giaTri);
    }

    function veVoucher() {
        var tu = $('vc-tim').value.trim().toLowerCase();
        var dangAp = hd().maGiamGia;
        var h = '';
        dsVoucher.forEach(function (v) {
            if (tu && (v.ma + ' ' + v.ten).toLowerCase().indexOf(tu) < 0) return;
            var apDung = v.ma === dangAp;
            // note code nhu nay nhe ban: 3 dong thong tin co ban theo yeu cau = muc giam, dieu kien ap dung, mo ta chi tiet
            var moTa = mucGiamChu(v) + (v.donToiThieu > 0 ? ' cho hóa đơn từ ' + tien(v.donToiThieu) + ' đ.' : ' cho mọi hóa đơn.');
            h += '<div class="vc' + (v.duDieuKien ? '' : ' khoa') + (apDung ? ' dang-ap' : '') + '">' +
                '<div class="vc-muc"><div>' + esc(mucGiamNgan(v)) + '<small>' + (v.loai === 'PHAN_TRAM' ? 'GIẢM %' : 'GIẢM TIỀN') + '</small></div></div>' +
                '<div class="vc-than">' +
                    '<div><span class="vc-ten">' + esc(v.ten) + '</span><span class="vc-ma">' + esc(v.ma) + '</span></div>' +
                    '<div class="vc-mo-ta">' + esc(moTa) + '</div>' +
                    '<ul class="vc-chi-tiet">' +
                        '<li>Điều kiện: ' + (v.donToiThieu > 0 ? 'hóa đơn từ ' + tien(v.donToiThieu) + ' đ' : 'không yêu cầu giá trị tối thiểu') + '</li>' +
                        '<li>Hạn dùng: đến ' + esc(v.hanDung) + '</li>' +
                        '<li>Mỗi khách tối đa ' + (v.moiNguoi == null ? '?' : v.moiNguoi) + ' lần' +
                            (v.conLuot == null ? '' : ' · còn ' + v.conLuot + ' lượt') + '</li>' +
                    '</ul>' +
                    (v.duDieuKien
                        ? (v.soTienGiam > 0 ? '<div class="vc-tiet-kiem">Giảm ước tính hóa đơn này: ' + tien(v.soTienGiam) + ' đ</div>' : '')
                        : '<div class="vc-thieu">Mua thêm ' + tien(v.canThem) + ' đ để dùng được mã này</div>') +
                '</div>' +
                '<div class="vc-chon">' +
                    (apDung ? '<button class="btn" type="button" disabled>Đang áp dụng</button>'
                        : (v.duDieuKien ? '<button class="btn lime" type="button" data-ma="' + esc(v.ma) + '">Chọn</button>'
                            : '<button class="btn line" type="button" disabled>Chưa đủ ĐK</button>')) +
                '</div></div>';
        });
        $('vc-ds').innerHTML = h || '<div class="rong">' + (dsVoucher.length ? 'Không có voucher khớp từ khóa' : 'Hiện chưa có voucher nào đang dùng được') + '</div>';
    }

    function chonVoucher(ma) {
        var b = hd();
        b.maGiamGia = ma;
        $('o-phieu').value = ma;
        dongVoucher();
        if (tamTinh(b) <= 0) toast('Đã chọn mã ' + ma + ' — tiền giảm sẽ tính khi hóa đơn có hàng');
        kiemTraPhieu();     // goi lai API cu de kiem tra that su va cap nhat so tien giam tren hoa don
    }

    // =====================================================================
    // TON KHO TRUC QUAN (yeu cau: bam chon san pham la so ton tren man hinh GIAM NGAY)
    // note code nhu nay nhe ban: so ton HIEN tren man hinh = ton lay tu server - so luong dang nam trong TAT CA hoa don cho (moi tab).
    //   - bam chon san pham -> so tren the san pham + trong popup giam ngay; xoa dong / giam so luong / dong tab -> tang lai
    //   - day chi la hien thi tren man hinh, KHONG ghi vao DB. Ton that chi bi tru luc THANH TOAN (BanHangTaiQuayServiceImpl).
    // =====================================================================
    // bo dau tieng Viet + chu thuong: "Vợt" -> "vot" (dung cho o loc va goi y)
    function chuanHoa(s) { return String(s || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[đĐ]/g, 'd').toLowerCase(); }
    // tong so luong cua bien the `id` dang nam trong moi hoa don cho
    function daGiu(id) { var n = 0; bills.forEach(function (b) { b.dong.forEach(function (d) { if (d.id === id) n += d.sl; }); }); return n; }
    function conLai(m) { return m.ton - daGiu(m.id); }
    function timSanPham(id) { var ds = $('luoi')._ds || []; for (var i = 0; i < ds.length; i++) { if (ds[i].id === id) return ds[i]; } return null; }
    function nguongSanPham(sp) { var n = 0; sp.bt.forEach(function (m) { if (m.nguong > n) n = m.nguong; }); return n; }
    // tong so con lai cua cac bien the con ban duoc cua san pham
    function tonSanPham(sp) {
        var n = 0;
        sp.bt.forEach(function (m) { if (m.trangThai === 'DANG_BAN' && m.ton > 0) n += Math.max(0, conLai(m)); });
        return n;
    }
    // ve lai so ton tren tat ca the san pham (va popup bien the dang mo) - goi sau MOI lan hoa don thay doi
    function capNhatTonTrenGiao() {
        Array.prototype.forEach.call($('luoi').querySelectorAll('.the'), function (the) {
            var sp = timSanPham(parseInt(the.dataset.sp, 10));
            if (!sp) return;
            var con = tonSanPham(sp);
            var hetThat = sp.ton <= 0;                       // server bao het hang
            var het = con <= 0;                              // het hang that, hoac da dua het vao cac hoa don
            var it = !het && con <= nguongSanPham(sp);
            the.classList.toggle('het', het);
            var e = the.querySelector('.ton');
            e.className = 'ton' + (het ? ' het' : (it ? ' it' : ''));
            e.textContent = hetThat ? 'Hết hàng' : (het ? 'Đã chọn hết' : 'Còn ' + con);
        });
        if (spDangMo) capNhatBienThe();
    }

    // =====================================================================
    // THE SAN PHAM -> POPUP CHON BIEN THE
    // =====================================================================
    var spDangMo = null;                                     // san pham dang mo trong popup (null = popup dong)

    function chonSanPham(sp) {
        var coBan = sp.bt.some(function (m) { return m.trangThai === 'DANG_BAN' && m.ton > 0; });
        if (!coBan) { toast('Sản phẩm đã hết hàng', true); return; }
        // note code nhu nay nhe ban: san pham chi co 1 phan loai thi khong can chon -> them thang nhu truoc; nhieu phan loai thi mo popup
        if (sp.bt.length === 1) { themMatHang(sp.bt[0], 1); return; }
        moPopupBienThe(sp);
    }
    function moPopupBienThe(sp) {
        spDangMo = sp;
        $('bt-ten').textContent = sp.ten;
        $('bt-hieu').textContent = sp.hieu || '';
        var anh = $('bt-anh');
        anh.onerror = function () { anh.onerror = null; anh.src = CTX + '/assets/no-image.png'; };
        anh.src = anhUrl(sp.anh);
        $('bt-gia').innerHTML = (sp.giaTu !== sp.giaDen ? 'Từ ' : '') + tien(sp.giaTu) + ' đ' + (sp.giaGoc > sp.giaTu ? '<s>' + tien(sp.giaGoc) + ' đ</s>' : '');
        $('bt-tim').value = '';
        $('bt-loc').style.display = sp.bt.length > 6 ? 'block' : 'none';      // nhieu phan loai moi hien o loc
        veBienThe();
        $('lop-bt').classList.add('mo');
        if (sp.bt.length > 6) $('bt-tim').focus();
    }
    function dongPopupBienThe() { $('lop-bt').classList.remove('mo'); spDangMo = null; }
    function bienTheTheoId(id) { if (!spDangMo) return null; for (var i = 0; i < spDangMo.bt.length; i++) { if (spDangMo.bt[i].id === id) return spDangMo.bt[i]; } return null; }

    // ve cac dong phan loai (loc theo o tim kiem neu co)
    function veBienThe() {
        var tu = chuanHoa($('bt-tim').value).trim().split(/\s+/).filter(Boolean);
        var h = '';
        spDangMo.bt.forEach(function (m) {
            var chu = chuanHoa((m.moTa || '') + ' ' + m.sku);
            if (!tu.every(function (t) { return chu.indexOf(t) >= 0; })) return;
            var km = m.giaBan < m.giaGoc;
            h += '<div class="bt-dong" data-id="' + m.id + '">' +
                '<div><div class="bt-ten">' + esc(m.moTa || m.ten) + '</div><div class="bt-sku">' + esc(m.sku) + '</div></div>' +
                '<div class="bt-gia-dong">' + tien(m.giaBan) + ' đ' + (km ? '<s>' + tien(m.giaGoc) + ' đ</s>' : '') + '</div>' +
                '<div class="bt-ton"></div>' +
                '<div class="stepper"><button type="button" data-g>−</button><input type="text" inputmode="numeric" class="bt-sl" value="1"><button type="button" data-t>+</button></div>' +
                '<button type="button" class="btn lime" data-them>Thêm</button></div>';
        });
        $('bt-ds').innerHTML = h || '<div class="rong">Không có phân loại phù hợp</div>';
        capNhatBienThe();
    }
    // cap nhat TAI CHO so ton / trang thai tung dong (khong ve lai de khong mat so luong nguoi dung dang go)
    function capNhatBienThe() {
        if (!spDangMo) return;
        Array.prototype.forEach.call($('bt-ds').querySelectorAll('.bt-dong'), function (dong) {
            var m = bienTheTheoId(parseInt(dong.dataset.id, 10));
            if (!m) return;
            var ban = m.trangThai === 'DANG_BAN' && m.ton > 0;
            var con = ban ? Math.max(0, conLai(m)) : 0;
            var het = con <= 0;
            dong.classList.toggle('het', het);
            var t = dong.querySelector('.bt-ton');
            t.className = 'bt-ton' + (het ? ' het' : (con <= m.nguong ? ' it' : ''));
            t.textContent = !ban ? 'Hết hàng' : (het ? 'Đã chọn hết' : 'Còn ' + con);
            Array.prototype.forEach.call(dong.querySelectorAll('button, input'), function (x) { x.disabled = het; });
            var o = dong.querySelector('.bt-sl');
            var sl = parseInt(o.value, 10);
            if (!sl || sl < 1) o.value = 1; else if (!het && sl > con) o.value = con;      // khong cho chon qua so con lai
        });
    }
    // bam trong danh sach phan loai: - / + / Them
    function xuLyClickBienThe(e) {
        var dong = e.target.closest('.bt-dong');
        if (!dong) return;
        var m = bienTheTheoId(parseInt(dong.dataset.id, 10));
        if (!m) return;
        var o = dong.querySelector('.bt-sl'), sl = parseInt(o.value, 10) || 1;
        if (e.target.closest('[data-g]')) { o.value = Math.max(1, sl - 1); }
        else if (e.target.closest('[data-t]')) { o.value = Math.min(Math.max(1, conLai(m)), sl + 1); }
        else if (e.target.closest('[data-them]')) {
            if (themMatHang(m, sl)) { o.value = 1; toast('Đã thêm ' + (m.moTa || m.ten) + ' × ' + sl); }
        }
    }

    // =====================================================================
    // GOI Y VOUCHER KHI GO O "MA GIAM GIA"
    // note code nhu nay nhe ban: bam vao / go vao o ma giam gia -> hien danh sach voucher dang dung duoc, loc theo tu khoa
    // (ma bat dau bang tu khoa len truoc, roi den ma / ten co chua tu khoa; khong phan biet hoa thuong va dau). Dung chung du lieu
    // voi popup "Voucher" (dsVoucher). Chon bang chuot hoac phim Len / Xuong + Enter; Esc de dong.
    // =====================================================================
    var dsGoiY = [], viTriGoiY = -1, dsVcLuc = 0, dsVcTamTinh = -1;
    function napDsVoucher(xong) {
        var tt = tamTinh(hd());
        if (dsVoucher.length && dsVcTamTinh === tt && Date.now() - dsVcLuc < 20000) { xong(); return; }     // da co du lieu moi, khong goi lai
        goi(CTX + '/pos/api/phieu-giam-gia/danh-sach?tamTinh=' + tt).then(function (kq) {
            if (kq.ok) { dsVoucher = kq.ds; dsVcTamTinh = tt; dsVcLuc = Date.now(); }
            xong();
        }).catch(function () { });
    }
    function dongGoiY() { $('goi-y-phieu').classList.remove('mo'); viTriGoiY = -1; }
    function veGoiY() {
        var o = $('o-phieu'), tu = chuanHoa(o.value).trim(), kq = [];
        dsVoucher.forEach(function (v, i) {
            var ma = chuanHoa(v.ma), ten = chuanHoa(v.ten);
            var hang = !tu ? 0 : (ma.indexOf(tu) === 0 ? 0 : (ma.indexOf(tu) > 0 ? 1 : (ten.indexOf(tu) >= 0 ? 2 : -1)));
            if (hang >= 0) kq.push({v: v, hang: hang, i: i});
        });
        kq.sort(function (a, b) { return a.hang - b.hang || a.i - b.i; });         // ma bat dau bang tu khoa len dau, con lai giu thu tu cua server
        dsGoiY = kq.slice(0, 6).map(function (x) { return x.v; });
        var h = '';
        dsGoiY.forEach(function (v, i) {
            h += '<div class="gy' + (v.duDieuKien ? '' : ' khoa') + '" data-i="' + i + '">' +
                '<div class="gy-tren"><b>' + esc(v.ma) + '</b><span class="gy-giam">' + esc(mucGiamChu(v)) + '</span></div>' +
                '<div class="gy-duoi">' + esc(v.ten) + (v.duDieuKien ? '' : ' · cần mua thêm ' + tien(v.canThem) + ' đ') + '</div></div>';
        });
        if (!h) h = '<div class="gy-trong">' + (tu ? 'Không có voucher khớp "' + esc(o.value.trim()) + '"' : 'Hiện chưa có voucher nào đang dùng được') + '</div>';
        $('goi-y-phieu').innerHTML = h;
        $('goi-y-phieu').classList.add('mo');
        viTriGoiY = -1;
    }
    function sangGoiY(i) {
        var cac = $('goi-y-phieu').querySelectorAll('.gy');
        Array.prototype.forEach.call(cac, function (g, k) { g.classList.toggle('sang', k === i); });
        viTriGoiY = i;
        if (cac[i] && cac[i].scrollIntoView) cac[i].scrollIntoView({block: 'nearest'});
    }
    function chonGoiY(i) {
        var v = dsGoiY[i];
        if (!v) return;
        if (!v.duDieuKien) { toast('Chưa đủ điều kiện: cần mua thêm ' + tien(v.canThem) + ' đ để dùng mã ' + v.ma, true); return; }
        dongGoiY();
        chonVoucher(v.ma);
    }
    function ganGoiYVoucher() {
        var o = $('o-phieu'), khung = $('goi-y-phieu');
        o.addEventListener('focus', function () { napDsVoucher(veGoiY); });
        o.addEventListener('input', function () { napDsVoucher(veGoiY); });
        o.addEventListener('blur', function () { setTimeout(dongGoiY, 150); });
        // chon bang chuot: dung mousedown (xay ra truoc khi o nhap mat focus nen khung chua kip dong)
        khung.addEventListener('mousedown', function (e) {
            e.preventDefault();
            var g = e.target.closest('.gy');
            if (g) chonGoiY(parseInt(g.dataset.i, 10));
        });
        // phim: gan o phan tu CHA va chay o pha capture de chay TRUOC phim Enter co san cua o ma giam gia (Enter = Ap dung)
        o.parentNode.addEventListener('keydown', function (e) {
            if (e.target !== o || !khung.classList.contains('mo')) return;
            if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
                e.preventDefault(); e.stopPropagation();
                if (!dsGoiY.length) return;
                sangGoiY(e.key === 'ArrowDown' ? Math.min(dsGoiY.length - 1, viTriGoiY + 1) : Math.max(0, viTriGoiY - 1));
            } else if (e.key === 'Enter' && viTriGoiY >= 0) {
                e.preventDefault(); e.stopPropagation();
                chonGoiY(viTriGoiY);                         // dang to sang 1 goi y -> chon no (khong ap dung ma dang go do)
            } else if (e.key === 'Escape') {
                e.stopPropagation(); dongGoiY();
            }
        }, true);
    }

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
            var sp = timSanPham(parseInt(the.dataset.sp, 10));
            if (sp) chonSanPham(sp);
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
        // popup chon voucher
        $('btn-ds-phieu').onclick = moVoucher;
        $('vc-dong').onclick = dongVoucher;
        $('vc-tim').addEventListener('input', veVoucher);
        $('vc-ds').addEventListener('click', function (e) { var n = e.target.closest('[data-ma]'); if (n) chonVoucher(n.dataset.ma); });
        $('lop-voucher').addEventListener('click', function (e) { if (e.target === this) dongVoucher(); });   // bam ra vung toi ben ngoai de dong

        // popup chon bien the (phan loai) cua san pham
        $('bt-dong').onclick = dongPopupBienThe;
        $('lop-bt').addEventListener('click', function (e) { if (e.target === this) dongPopupBienThe(); });
        $('bt-tim').addEventListener('input', veBienThe);
        $('bt-ds').addEventListener('click', xuLyClickBienThe);
        $('bt-ds').addEventListener('input', function (e) { if (e.target.classList.contains('bt-sl')) e.target.value = e.target.value.replace(/\D/g, ''); });   // o so luong chi nhan chu so
        $('bt-ds').addEventListener('change', function (e) { if (e.target.classList.contains('bt-sl')) capNhatBienThe(); });
        document.addEventListener('keydown', function (e) { if (e.key === 'Escape' && spDangMo) dongPopupBienThe(); });
        ganGoiYVoucher();

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
            else if (e.key === 'F9') { e.preventDefault(); if (!$('lop-tc').classList.contains('mo') && !$('lop-voucher').classList.contains('mo') && !$('lop-bt').classList.contains('mo')) thanhToan(); }
            else if (e.key === 'Escape') { $('lop-tc').classList.remove('mo'); dongVoucher(); }
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

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- Man hinh BAN HANG TAI QUAY. Servlet gui sang: tuyChon (TuyChonBoLocDTO), tongKet (TongKetNgayDTO), session nhanVien --%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bán hàng tại quầy - PickHub POS</title>
    <link rel="stylesheet" href="${ctx}/assets/pos.css">
    <%-- note code nhu nay nhe ban: giao dien kinh mo xanh la (sang/toi). Nap SAU pos.css --%>
    <%@ include file="/WEB-INF/theme-head.jspf" %>
    <link rel="stylesheet" href="${ctx}/assets/pos-glass.css">
    <%-- note code nhu nay nhe ban: moi o <select> trong trang deu tu co thanh tim kiem (select-search.js + select-search.css). Tat 1 o: them data-no-search --%>
    <link rel="stylesheet" href="${ctx}/assets/select-search.css">
    <script src="${ctx}/assets/select-search.js" defer></script>
    <script>var CTX = '${ctx}';</script>
    <script src="${ctx}/assets/pos.js" defer></script>
</head>
<body>
<div class="pos">
    <header class="pos-top">
        <div class="logo">PICK<span>HUB</span> <span class="pos-tag">POS</span></div>
        <div class="ca">Ca hôm nay: <b id="ca-so">${tongKet.soHoaDon}</b> hóa đơn · <b id="ca-tien"><fmt:formatNumber value="${tongKet.doanhThu}" pattern="#,##0"/></b> đ</div>
        <div class="phai">
            <span id="dong-ho"></span>
            <span>👤 <b><c:out value="${sessionScope.nhanVien.hoTen}"/></b></span>
            <a href="${ctx}/pos/hoa-don" target="_blank">Hóa đơn đã bán</a>
            <a href="${ctx}/dang-xuat">Đăng xuất</a>
        </div>
    </header>

    <div class="pos-body">
        <%-- ===================== CHON SAN PHAM ===================== --%>
        <section class="pos-left">
            <div class="o-tim">
                <input id="o-tim" type="text" autocomplete="off" placeholder="Quét mã vạch / nhập mã hàng rồi Enter · hoặc gõ tên, thương hiệu để lọc (F2)">
                <button class="btn line" id="btn-loc" type="button">⚙ Bộ lọc</button>
            </div>
            <div class="chips-nhanh">
                <button type="button" data-loai="" class="on">Tất cả</button>
                <button type="button" data-loai="VOT">Vợt</button>
                <button type="button" data-loai="BONG">Bóng</button>
                <button type="button" data-loai="PHU_KIEN">Phụ kiện</button>
                <button type="button" data-loai="TUI">Túi</button>
            </div>
            <div class="bo-loc" id="bo-loc">
                <div class="luoi-loc">
                    <select id="loc-danhMuc">
                        <option value="">Mọi danh mục</option>
                        <c:forEach items="${tuyChon.danhMucGoc}" var="g">
                            <option value="${g.maDanhMuc}">${g.tenDanhMuc}</option>
                            <c:forEach items="${tuyChon.danhMucCon}" var="con">
                                <c:if test="${con.danhMucCha.maDanhMuc == g.maDanhMuc}"><option value="${con.maDanhMuc}">&nbsp;&nbsp;› ${con.tenDanhMuc}</option></c:if>
                            </c:forEach>
                        </c:forEach>
                    </select>
                    <select id="loc-thuongHieu">
                        <option value="">Mọi thương hiệu</option>
                        <c:forEach items="${tuyChon.thuongHieu}" var="t"><option value="${t.maThuongHieu}">${t.tenThuongHieu}</option></c:forEach>
                    </select>
                    <select id="loc-loai">
                        <option value="">Mọi loại sản phẩm</option>
                        <option value="VOT">Vợt</option><option value="BONG">Bóng</option><option value="PHU_KIEN">Phụ kiện</option><option value="TUI">Túi</option>
                    </select>
                    <select id="loc-mau">
                        <option value="">Mọi màu sắc</option>
                        <c:forEach items="${tuyChon.mauSac}" var="m"><option value="${m.maMauSac}">${m.tenMauSac}</option></c:forEach>
                    </select>
                    <select id="loc-khoiLuong">
                        <option value="">Mọi khối lượng</option>
                        <c:forEach items="${tuyChon.khoiLuong}" var="k"><option value="${k.maKhoiLuong}">${k.khoiLuongGram} g</option></c:forEach>
                    </select>
                    <select id="loc-chatLieu">
                        <option value="">Mọi chất liệu</option>
                        <c:forEach items="${tuyChon.chatLieu}" var="c"><option value="${c.maChatLieu}">${c.tenChatLieu}</option></c:forEach>
                    </select>
                    <select id="loc-chuVi">
                        <option value="">Mọi chu vi tay cầm</option>
                        <c:forEach items="${tuyChon.chuVi}" var="v"><option value="${v.maChuViTayCam}">${v.chuViInch} inch</option></c:forEach>
                    </select>
                    <input id="loc-giaTu" type="text" inputmode="numeric" placeholder="Giá từ (đ)">
                    <input id="loc-giaDen" type="text" inputmode="numeric" placeholder="Giá đến (đ)">
                    <select id="loc-ton">
                        <option value="CON_HANG">Còn hàng</option>
                        <option value="SAP_HET">Sắp hết hàng</option>
                        <option value="HET_HANG">Hết hàng</option>
                        <option value="TAT_CA">Tất cả tình trạng</option>
                    </select>
                    <select id="loc-sapXep">
                        <option value="ten">Tên A → Z</option>
                        <option value="gia_tang">Giá thấp → cao</option>
                        <option value="gia_giam">Giá cao → thấp</option>
                        <option value="moi">Mới nhất</option>
                        <option value="ton_it">Tồn ít nhất</option>
                        <option value="ton_nhieu">Tồn nhiều nhất</option>
                    </select>
                    <label class="chk"><input id="loc-km" type="checkbox"> Đang khuyến mãi</label>
                    <button class="btn line sm" id="btn-xoa-loc" type="button">Xóa bộ lọc</button>
                </div>
            </div>
            <div class="dau-ds"><span id="so-ket-qua">Đang tải...</span><span>Bấm vào sản phẩm để chọn phân loại và thêm vào hóa đơn</span></div>
            <div class="luoi" id="luoi"></div>
            <div class="phan-trang" id="phan-trang"></div>
        </section>

        <%-- ===================== HOA DON + THANH TOAN ===================== --%>
        <aside class="pos-right">
            <div class="tabs-hd" id="tabs"></div>
            <div class="dong-hd" id="dong-hd"></div>

            <div class="tong-ket">
                <div class="phieu">
                    <input id="o-phieu" type="text" placeholder="Mã giảm giá" maxlength="30" autocomplete="off">
                    <%-- note code nhu nay nhe ban: khung GOI Y voucher - pos.js do ket qua vao day khi bam vao / go vao o ma giam gia --%>
                    <div class="goi-y" id="goi-y-phieu"></div>
                    <button class="btn line" id="btn-phieu" type="button">Áp dụng</button>
                    <%-- nut mo popup "Xem va chon voucher" (yeu cau 6) --%>
                    <button class="btn lime" id="btn-ds-phieu" type="button" title="Xem danh sách voucher đang dùng được và chọn">🎟 Voucher</button>
                </div>
                <div class="phieu-ok" id="phieu-ok" style="display:none"><span id="phieu-ok-ten"></span><a href="#" id="btn-bo-phieu" onclick="return false" style="text-decoration:underline">Bỏ mã</a></div>
                <div class="dong-tien"><span>Tạm tính</span><span id="t-tamtinh">0 đ</span></div>
                <div class="dong-tien"><span>Giảm giá</span><span id="t-giam">- 0 đ</span></div>
                <div class="dong-tien tong"><span>KHÁCH CẦN TRẢ</span><span id="t-tong">0 đ</span></div>
            </div>

            <div class="thanh-toan">
                <div class="pt-chon">
                    <button type="button" data-pt="TIEN_MAT">Tiền mặt</button>
                    <button type="button" data-pt="CHUYEN_KHOAN">Chuyển khoản</button>
                    <button type="button" data-pt="THE">Quẹt thẻ</button>
                </div>
                <div class="tien-dua" id="khu-tien-mat">
                    <div class="hang"><label for="o-dua">Khách đưa</label><input id="o-dua" type="text" inputmode="numeric" placeholder="0"></div>
                    <div class="nhanh-tien" id="nhanh-tien"></div>
                    <div class="dong-tien"><span>Tiền thừa trả khách</span><span id="t-thua" class="tien-thua">—</span></div>
                </div>
                <details class="them-tt">
                    <summary>Thông tin khách &amp; ghi chú (không bắt buộc)</summary>
                    <div class="luoi-tt">
                        <input id="o-khach" type="text" placeholder="Tên khách" maxlength="100">
                        <input id="o-sdt" type="text" placeholder="Số điện thoại" maxlength="11">
                        <input id="o-ghichu" class="rong2" type="text" placeholder="Ghi chú hóa đơn" maxlength="500">
                    </div>
                </details>
                <button class="btn lime btn-tra" id="btn-tra" type="button">THANH TOÁN (F9)</button>
                <div style="display:flex;gap:8px;margin-top:8px">
                    <button class="btn line sm" id="btn-xoa-hd" type="button" style="flex:1">Xóa hóa đơn</button>
                </div>
                <div class="phim-tat">F2 tìm kiếm · F4 hóa đơn mới · F9 thanh toán</div>
            </div>
        </aside>
    </div>
</div>

<%-- Hop thoai thanh toan thanh cong --%>
<div class="lop-phu" id="lop-tc">
    <div class="hop">
        <div class="tick">✓</div>
        <h2>Thanh toán thành công</h2>
        <div class="so muted">Mã hóa đơn: <b id="tc-ma"></b></div>
        <div class="so">Tổng tiền: <b id="tc-tong"></b></div>
        <div class="so" id="tc-thua-dong">Tiền thừa trả khách: <b id="tc-thua" style="color:#dc2626"></b></div>
        <div class="nut">
            <button class="btn lime" id="tc-in" type="button">🖨 In hóa đơn</button>
            <button class="btn line" id="tc-moi" type="button">Bán tiếp (Esc)</button>
        </div>
    </div>
</div>
<%-- ================= POPUP XEM VA CHON VOUCHER =================
     note code nhu nay nhe ban: popup nay chi la khung rong, noi dung do pos.js ve (ham moVoucher/veVoucher) sau khi goi
     API GET /pos/api/phieu-giam-gia/danh-sach?tamTinh=... (PosPhieuDanhSachApiServlet). Esc hoac bam ra ngoai de dong. --%>
<div class="lop-phu" id="lop-voucher">
    <div class="hop-vc">
        <div class="vc-dau">
            <h2>🎟 Chọn voucher</h2>
            <button type="button" class="vc-dong" id="vc-dong" title="Đóng (Esc)">✕</button>
        </div>
        <div class="vc-ghi-chu" id="vc-ghi-chu"></div>
        <input type="text" id="vc-tim" placeholder="Tìm theo mã hoặc tên voucher..." autocomplete="off">
        <div class="vc-ds" id="vc-ds"></div>
    </div>
</div>
<%-- ================= POPUP CHON BIEN THE (phan loai) CUA 1 SAN PHAM =================
     note code nhu nay nhe ban: khung rong, noi dung do pos.js ve (moPopupBienThe / veBienThe) khi bam vao the san pham co nhieu bien the.
     Moi dong co o tang giam so luong + nut "Them"; so ton ben canh GIAM NGAY khi them vao hoa don. Esc / bam ra ngoai de dong. --%>
<div class="lop-phu" id="lop-bt">
    <div class="hop-bt">
        <div class="bt-dau">
            <div class="bt-anh"><img id="bt-anh" alt=""></div>
            <div class="bt-tt">
                <h2 id="bt-ten"></h2>
                <div class="bt-hieu" id="bt-hieu"></div>
                <div class="bt-gia" id="bt-gia"></div>
            </div>
            <button type="button" class="vc-dong" id="bt-dong" title="Đóng (Esc)">✕</button>
        </div>
        <div class="bt-loc" id="bt-loc"><input type="text" id="bt-tim" placeholder="Lọc phân loại (màu, khối lượng, mã hàng...)" autocomplete="off"></div>
        <div class="bt-ds" id="bt-ds"></div>
        <div class="bt-chan">Chọn số lượng rồi bấm <b>Thêm</b>. Có thể thêm nhiều phân loại liên tiếp, bấm Esc để đóng.</div>
    </div>
</div>
<div id="toast"></div>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- note code nhu nay nhe ban: the so co dong nho tang truong, loai = up (xanh) / down (do) / flat (xam) --%>
<div class="grid-4">
    <div class="card stat">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/></svg></div>
        <div>
            <span>Doanh thu tháng này</span>
            <strong>${ph:tien(tq.doanhThuThang)}</strong>
            <small class="growth ${tq.tangTruongDoanhThu.loai}">
                <c:choose>
                    <c:when test="${tq.tangTruongDoanhThu.coSoSoSanh}"><em>${tq.tangTruongDoanhThu.chu}</em> so với cùng kỳ tháng trước</c:when>
                    <c:otherwise>Tháng trước chưa có dữ liệu</c:otherwise>
                </c:choose>
            </small>
        </div>
    </div>
    <a class="card stat" href="${ctx}/wp-admin/don-hang?trangThai=CHO_XAC_NHAN">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><path d="M5 7h14l-1 13H6L5 7z"/><path d="M9 7a3 3 0 016 0"/></svg></div>
        <div><span>Đơn mới</span><strong>${tq.donMoi}</strong></div>
    </a>
    <%-- note code nhu nay nhe ban: the nay thay cho the "Luot dat san" (dat san chua lam). Trung binh = doanh thu thang / so don hoan thanh --%>
    <div class="card stat">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><path d="M4 19V5"/><path d="M4 19h16"/><path d="M8 15l3-4 3 2 4-6"/></svg></div>
        <div>
            <span>Trung bình mỗi đơn (tháng này)</span>
            <strong>${ph:tien(tq.giaTriTrungBinhDon)}</strong>
            <small class="growth flat">${tq.soDonHoanThanhThang} đơn hoàn thành</small>
        </div>
    </div>
    <a class="card stat" href="${ctx}/wp-admin/khach-hang">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 20c.5-3.5 3-5.5 6.5-5.5s6 2 6.5 5.5"/></svg></div>
        <div>
            <span>Khách hàng</span>
            <strong>${tq.soKhachHang}</strong>
            <small class="growth ${tq.tangTruongKhachHang.loai}">
                ${tq.khachMoiThang} khách mới
                <c:if test="${tq.tangTruongKhachHang.coSoSoSanh}">· <em>${tq.tangTruongKhachHang.chu}</em> so với tháng trước</c:if>
            </small>
        </div>
    </a>
</div>

<div class="grid-2">
    <%-- Bieu do doanh thu: chi cao cot = phanTram tinh san o ThongKeService --%>
    <div class="card">
        <div class="chart-head">
            <h3>Doanh thu ${tq.kieuBieuDo == 'thang' ? '6 tháng' : '7 ngày'} (triệu đồng)</h3>
            <div class="tabs">
                <a class="${tq.kieuBieuDo == 'ngay' ? 'on' : ''}" href="${ctx}/wp-admin?kieu=ngay">7 ngày</a>
                <a class="${tq.kieuBieuDo == 'thang' ? 'on' : ''}" href="${ctx}/wp-admin?kieu=thang">6 tháng</a>
            </div>
        </div>
        <div class="bars">
            <c:forEach items="${tq.bieuDo}" var="cot">
                <div class="bar-col">
                    <div class="bar-val">${ph:trieu(cot.giaTri)}</div>
                    <div class="bar ${cot.homNay ? 'now' : ''}" style="height: ${cot.phanTram}%"></div>
                    <div class="bar-label">${cot.nhan}</div>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="card">
        <h3>Cần xử lý</h3>
        <ul class="todo">
            <li><a href="${ctx}/wp-admin/don-hang?trangThai=CHO_XAC_NHAN">Đơn mới cần xác nhận <span class="count ${tq.donChoXacNhan == 0 ? 'zero' : ''}">${tq.donChoXacNhan}</span></a></li>
            <li><a href="${ctx}/wp-admin/don-hang?trangThai=DANG_GIAO">Đơn đang giao <span class="count ${tq.donDangGiao == 0 ? 'zero' : ''}">${tq.donDangGiao}</span></a></li>
            <li><a href="${ctx}/wp-admin/ton-kho?canhBao=1">Sản phẩm sắp hết hoặc hết hàng <span class="count ${tq.tonKhoCanhBao == 0 ? 'zero' : ''}">${tq.tonKhoCanhBao}</span></a></li>
            <li><a href="${ctx}/wp-admin/khuyen-mai?trangThai=SAP_CHAY">Khuyến mãi sắp chạy <span class="count ${tq.khuyenMaiSapChay == 0 ? 'zero' : ''}">${tq.khuyenMaiSapChay}</span></a></li>
        </ul>
    </div>
</div>

<div class="section-title">Đơn hàng gần đây</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã đơn</th><th>Khách hàng</th><th>Tổng tiền</th><th>Trạng thái</th></tr>
        <c:forEach items="${tq.donGanDay}" var="h">
            <tr>
                <td><a class="code" href="${ctx}/wp-admin/don-hang/chi-tiet?id=${h.maHoaDon}">#${h.maTraCuu}</a></td>
                <td>${h.khachHang.hoTen}</td>
                <td>${ph:tien(h.tongTien)}</td>
                <td><span class="badge ${h.trangThai}">${ph:nhan(h.trangThai)}</span></td>
            </tr>
        </c:forEach>
        <c:if test="${empty tq.donGanDay}"><tr><td colspan="4" class="empty">Chưa có đơn hàng</td></tr></c:if>
    </table>
</div>

<div class="grid-2e" style="margin-top:18px">
    <div class="card">
        <h3>Cảnh báo tồn kho</h3>
        <table class="tbl">
            <c:forEach items="${tq.tonKhoThap}" var="c">
                <%-- note code nhu nay nhe ban: ton = 0 -> DO (het han), con lai (cham nguong) -> CAM. Hai muc co mau + nhan khac nhau --%>
                <c:set var="hetHang" value="${c.soLuongTon == 0}"/>
                <tr>
                    <td>${c.sanPham.tenSanPham}<br><span class="muted">${c.maHang}</span></td>
                    <td class="num">
                        <b class="${hetHang ? 'stock-out' : 'stock-low'}">${c.soLuongTon}</b> / ${c.nguongCanhBaoTonKho}
                        <span class="stock-tag ${hetHang ? 'out' : 'low'}">${hetHang ? 'Hết hàng' : 'Sắp hết'}</span>
                    </td>
                        <%-- note code nhu nay nhe ban: NHAN_VIEN khong co quyen ghi o ton-kho (PhanQuyenUtil) nen an nut nhap hang --%>
                        <%-- note code nhu nay nhe ban: NHAN_VIEN cung duoc nhap hang nhanh (PhanQuyenUtil da mo quyen ghi cho ton-kho, kiem ke van bi chan o TonKhoServlet) --%>
                    <td class="num">
                        <form class="quick-import" method="post" action="${ctx}/wp-admin/ton-kho">
                            <input type="hidden" name="hanhDong" value="nhapThem">
                            <input type="hidden" name="id" value="${c.maChiTietSanPham}">
                            <input type="hidden" name="quayLai" value="dashboard">
                            <input type="number" name="soLuong" min="1" value="10" required>
                            <button class="btn sm lime" type="submit">Nhập hàng nhanh</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty tq.tonKhoThap}"><tr><td class="empty">Tồn kho ổn định</td></tr></c:if>
        </table>
    </div>
    <div class="card">
        <h3>Sản phẩm bán chạy</h3>
        <table class="tbl">
            <c:forEach items="${tq.topBanChay}" var="dong">
                <tr><td>${dong[0]}</td><td class="num"><b>${dong[1]}</b> đã bán</td></tr>
            </c:forEach>
            <c:if test="${empty tq.topBanChay}"><tr><td class="empty">Chưa có dữ liệu</td></tr></c:if>
        </table>
    </div>
</div>

<%-- note code nhu nay nhe ban: co cau doanh thu thang nay theo danh muc cha (Vot / Bong / Phu kien / Tui) --%>
<div class="section-title">Cơ cấu doanh thu theo danh mục (tháng này)</div>
<div class="card">
    <div class="share-list">
        <c:forEach items="${tq.coCauDanhMuc}" var="d">
            <div class="share-row">
                <span>${d.ten}</span>
                <div class="share-track"><div class="share-fill" style="width: ${d.phanTram}%"></div></div>
                <span class="share-val"><b>${d.phanTram}%</b> · ${ph:tien(d.giaTri)}</span>
            </div>
        </c:forEach>
    </div>
    <c:if test="${empty tq.coCauDanhMuc}"><div class="empty">Tháng này chưa có đơn hoàn thành</div></c:if>
</div>

<%-- note code nhu nay nhe ban: tong quan du lieu 18 bang, THU GON san (the details). Bam vao o nao -> mo trang quan ly cua bang do --%>
<details class="data-overview">
    <summary>Tổng quan dữ liệu (18 bảng) - bấm vào từng ô để mở trang quản lý</summary>
    <div class="tiles">
        <c:forEach items="${tq.thongKeBang}" var="o">
            <%-- note code nhu nay nhe ban: o nao vai tro hien tai khong duoc vao (nhan vien + khuyen mai/phieu giam gia/nhan vien) thi an, tranh bam vao bi loi 403 --%>
            <c:if test="${ph:coQuyen(sessionScope.taiKhoan.vaiTro, o.duongDan)}">
                <a class="tile" href="${ctx}${o.duongDan}" title="Mở trang ${o.ten}">
                    <b>${o.soDong}</b><span>${o.ten}</span>
                </a>
            </c:if>
        </c:forEach>
    </div>
</details>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>
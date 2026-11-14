<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/don-hang">← Danh sách đơn hàng</a>
<div class="card">
    <div class="chart-head">
        <h3>Đơn ${empty hd.maTraCuu ? '(giỏ hàng)' : hd.maTraCuu} <span class="badge ${hd.trangThai}">${ph:nhan(hd.trangThai)}</span></h3>
        <%-- note code nhu nay nhe ban: chi hien 1 nut chuyen sang buoc ke tiep (xac nhan -> giao -> hoan thanh). Huy don: TODO --%>
        <c:if test="${not empty buocTiepTheo}">
            <form class="inline" method="post" action="${ctx}/wp-admin/don-hang/chi-tiet">
                <input type="hidden" name="id" value="${hd.maHoaDon}">
                <input type="hidden" name="trangThaiMoi" value="${buocTiepTheo}">
                <button class="btn lime" type="submit">Chuyển sang: ${ph:nhan(buocTiepTheo)}</button>
            </form>
        </c:if>
    </div>
    <dl class="info-grid">
        <div><dt>Khách hàng</dt><dd>${hd.khachHang.hoTen} (${hd.khachHang.soDienThoai})</dd></div>
        <div><dt>Người nhận</dt><dd>${hd.tenNguoiNhan} - ${hd.soDienThoaiNguoiNhan}</dd></div>
        <div><dt>Địa chỉ giao</dt><dd>${hd.soNhaDuongGiao}, ${hd.phuongXaGiao}, ${hd.quanHuyenGiao}, ${hd.tinhThanhGiao}</dd></div>
        <div><dt>Ngày đặt</dt><dd>${ph:ngayGio(hd.ngayDatHang)}</dd></div>
        <div><dt>Nhân viên xác nhận</dt><dd>${hd.nhanVienXacNhan.hoTen}</dd></div>
        <div><dt>Ngày giao thành công</dt><dd>${ph:ngayGio(hd.ngayGiaoThanhCong)}</dd></div>
        <div><dt>Phiếu giảm giá</dt><dd>${hd.phieuGiamGia.maGiamGia}</dd></div>
        <div><dt>Ghi chú</dt><dd>${hd.ghiChu}</dd></div>
        <c:if test="${hd.trangThai == 'DA_HUY'}"><div><dt>Lý do hủy</dt><dd>${hd.lyDoHuy}</dd></div></c:if>
    </dl>
</div>

<div class="section-title">Sản phẩm trong đơn</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Sản phẩm</th><th>Mã hàng</th><th class="num">Đơn giá gốc</th><th class="num">Đơn giá</th><th class="num">SL</th><th class="num">Thành tiền</th></tr>
        <c:forEach items="${dongSanPham}" var="d">
            <tr>
                <td>${d.tenSanPham}<br><span class="muted">${d.moTaChiTiet}</span></td>
                <td>${d.maHang}</td>
                <td class="num muted">${ph:tien(d.donGiaGoc)}</td>
                <td class="num">${ph:tien(d.donGia)}</td>
                <td class="num">${d.soLuong}</td>
                <td class="num"><b>${ph:tien(d.thanhTien)}</b></td>
            </tr>
        </c:forEach>
        <tr><td colspan="5" class="num muted">Tạm tính</td><td class="num">${ph:tien(hd.tamTinh)}</td></tr>
        <tr><td colspan="5" class="num muted">Giảm giá (phiếu)</td><td class="num">- ${ph:tien(hd.soTienGiam)}</td></tr>
        <tr><td colspan="5" class="num"><b>Tổng tiền</b></td><td class="num"><b>${ph:tien(hd.tongTien)}</b></td></tr>
    </table>
</div>

<div class="section-title">Lịch sử trạng thái</div>
<div class="card">
    <ul class="timeline">
        <c:forEach items="${lichSu}" var="l">
            <li><b>${ph:nhan(l.denTrangThai)}</b>
                <small>${ph:ngayGio(l.ngayThayDoi)} · ${empty l.nguoiThayDoi ? 'Khách hàng / hệ thống' : l.nguoiThayDoi.hoTen}</small>
                <small>${l.ghiChu}</small></li>
        </c:forEach>
    </ul>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

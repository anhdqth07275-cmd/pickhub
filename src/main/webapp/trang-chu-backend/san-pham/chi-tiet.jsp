<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/san-pham">← Danh sách sản phẩm</a>
<div class="card">
    <h3>${sp.tenSanPham}</h3>
    <dl class="info-grid">
        <div><dt>Danh mục</dt><dd>${sp.danhMuc.tenDanhMuc}</dd></div>
        <div><dt>Thương hiệu</dt><dd>${sp.thuongHieu.tenThuongHieu}</dd></div>
        <div><dt>Loại</dt><dd>${ph:nhan(sp.loaiSanPham)}</dd></div>
        <div><dt>Đường dẫn</dt><dd>${sp.duongDan}</dd></div>
        <div><dt>Hiển thị</dt><dd>${sp.dangHienThi ? 'Đang hiện' : 'Đang ẩn'}</dd></div>
        <div><dt>Ngày tạo</dt><dd>${ph:ngayGio(sp.ngayTao)}</dd></div>
    </dl>
    <p><b>Mô tả ngắn:</b> ${sp.moTaNgan}</p>
    <p class="muted" style="margin-bottom:0">${sp.moTa}</p>
</div>

<div class="section-title">Biến thể (màu × khối lượng × chất liệu × chu vi)</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã hàng</th><th>Màu</th><th>Khối lượng</th><th>Chất liệu</th><th>Chu vi</th><th class="num">Giá gốc</th><th class="num">Tồn</th><th>Trạng thái</th></tr>
        <c:forEach items="${bienThe}" var="b">
            <tr>
                <td class="code">${b.maHang}</td>
                <td>${b.mauSac.tenMauSac}</td>
                <td>${b.khoiLuong.khoiLuongGram}${empty b.khoiLuong ? '' : ' g'}</td>
                <td>${b.chatLieu.tenChatLieu}</td>
                <td>${b.chuViTayCam.chuViInch}</td>
                <td class="num">${ph:tien(b.giaGoc)}</td>
                <td class="num"><b>${b.soLuongTon}</b></td>
                <td><span class="badge ${b.trangThai}">${ph:nhan(b.trangThai)}</span></td>
            </tr>
        </c:forEach>
        <c:if test="${empty bienThe}"><tr><td colspan="8" class="empty">Chưa có biến thể</td></tr></c:if>
    </table>
</div>

<div class="section-title">Hình ảnh</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Đường dẫn ảnh</th><th>Loại tệp</th><th class="num">Dung lượng (KB)</th><th>Ảnh chính</th><th class="num">Thứ tự</th></tr>
        <c:forEach items="${hinhAnh}" var="h">
            <tr><td>${h.duongDanAnh}</td><td>${h.loaiTep}</td><td class="num">${h.dungLuongKb}</td>
                <td>${h.laAnhChinh ? '✔' : ''}</td><td class="num">${h.thuTuSapXep}</td></tr>
        </c:forEach>
        <c:if test="${empty hinhAnh}"><tr><td colspan="5" class="empty">Chưa có hình ảnh</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

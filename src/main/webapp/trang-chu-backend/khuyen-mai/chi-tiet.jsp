<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/khuyen-mai">← Chương trình khuyến mãi</a>
<div class="card">
    <h3>${ct.tenChuongTrinh} <span class="badge ${ct.trangThai}">${ph:nhan(ct.trangThai)}</span></h3>
    <p class="muted">${ct.moTa}</p>
    <p style="margin-bottom:0">${ph:ngayGio(ct.ngayBatDau)} → ${ph:ngayGio(ct.ngayKetThuc)}</p>
</div>
<div class="section-title">Chi tiết sản phẩm được giảm</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã hàng</th><th>Sản phẩm</th><th class="num">Giá gốc</th><th>Mức giảm</th><th class="num">Đã bán / giới hạn</th></tr>
        <c:forEach items="${dsChiTiet}" var="d">
            <tr>
                <td class="code">${d.chiTietSanPham.maHang}</td>
                <td>${d.chiTietSanPham.sanPham.tenSanPham}</td>
                <td class="num">${ph:tien(d.chiTietSanPham.giaGoc)}</td>
                <td><b>${ph:giamGia(d.loaiGiam, d.giaTriGiam)}</b></td>
                <td class="num">${d.soLuongDaBan} / ${empty d.soLuongGioiHan ? '∞' : d.soLuongGioiHan}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty dsChiTiet}"><tr><td colspan="5" class="empty">Chưa có sản phẩm trong chương trình</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

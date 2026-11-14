<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/phieu-giam-gia">← Phiếu giảm giá</a>
<form class="toolbar" method="get" action="${ctx}/wp-admin/phieu-giam-gia/su-dung">
    <select name="trangThai">
        <option value="">Tất cả</option>
        <option value="DA_SU_DUNG" ${param.trangThai == 'DA_SU_DUNG' ? 'selected' : ''}>Đã sử dụng</option>
        <option value="DA_HOAN" ${param.trangThai == 'DA_HOAN' ? 'selected' : ''}>Đã hoàn</option>
    </select>
    <button class="btn" type="submit">Lọc</button>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Phiếu</th><th>Khách hàng</th><th>Hóa đơn</th><th class="num">Số tiền giảm</th><th>Ngày dùng</th><th>Trạng thái</th><th>Ngày hoàn</th></tr>
        <c:forEach items="${pt.danhSach}" var="s">
            <tr>
                <td class="code">${s.phieuGiamGia.maGiamGia}</td>
                <td>${s.khachHang.hoTen}</td>
                <td><a href="${ctx}/wp-admin/don-hang/chi-tiet?id=${s.maHoaDon}">#${s.maHoaDon}</a></td>
                <td class="num">${ph:tien(s.soTienGiam)}</td>
                <td class="nowrap">${ph:ngayGio(s.ngaySuDung)}</td>
                <td><span class="badge ${s.trangThai}">${ph:nhan(s.trangThai)}</span></td>
                <td class="nowrap">${ph:ngayGio(s.ngayHoanLai)}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="7" class="empty">Chưa có lượt sử dụng</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

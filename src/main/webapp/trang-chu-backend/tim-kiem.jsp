<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<c:choose>
    <c:when test="${fn:length(q) < 2}">
        <div class="card"><div class="empty">Nhập ít nhất 2 ký tự: mã đơn (#HD...), số điện thoại khách hoặc tên sản phẩm.</div></div>
    </c:when>
    <c:otherwise>
        <p class="muted">Kết quả cho "<b><c:out value="${q}"/></b>"</p>

        <div class="section-title">Đơn hàng (${donHang.tongSo})</div>
        <div class="card table-wrap" style="padding:0">
            <table class="tbl">
                <tr><th>Mã đơn</th><th>Người nhận</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr>
                <c:forEach items="${donHang.danhSach}" var="h">
                    <tr>
                        <td class="code">#${h.maTraCuu}</td>
                        <td>${h.tenNguoiNhan}<br><span class="muted">${h.soDienThoaiNguoiNhan}</span></td>
                        <td class="nowrap">${ph:tien(h.tongTien)}</td>
                        <td><span class="badge ${h.trangThai}">${ph:nhan(h.trangThai)}</span></td>
                        <td><a class="btn sm line" href="${ctx}/wp-admin/don-hang/chi-tiet?id=${h.maHoaDon}">Xem</a></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty donHang.danhSach}"><tr><td colspan="5" class="empty">Không có đơn hàng phù hợp</td></tr></c:if>
            </table>
        </div>

        <div class="section-title">Khách hàng (${khachHang.tongSo})</div>
        <div class="card table-wrap" style="padding:0">
            <table class="tbl">
                <tr><th>Họ tên</th><th>Số điện thoại</th><th>Email</th><th></th></tr>
                <c:forEach items="${khachHang.danhSach}" var="k">
                    <tr>
                        <td>${k.hoTen}</td>
                        <td>${k.soDienThoai}</td>
                        <td class="muted">${k.thuDienTu}</td>
                        <td><a class="btn sm line" href="${ctx}/wp-admin/khach-hang?tuKhoa=${k.soDienThoai}">Xem</a></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty khachHang.danhSach}"><tr><td colspan="4" class="empty">Không có khách hàng phù hợp</td></tr></c:if>
            </table>
        </div>

        <div class="section-title">Sản phẩm (${sanPham.tongSo})</div>
        <div class="card table-wrap" style="padding:0">
            <table class="tbl">
                <tr><th>Tên sản phẩm</th><th>Thương hiệu</th><th></th></tr>
                <c:forEach items="${sanPham.danhSach}" var="sp">
                    <tr>
                        <td>${sp.tenSanPham}</td>
                        <td class="muted">${sp.thuongHieu.tenThuongHieu}</td>
                        <td><a class="btn sm line" href="${ctx}/wp-admin/san-pham/chi-tiet?id=${sp.maSanPham}">Xem</a></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty sanPham.danhSach}"><tr><td colspan="3" class="empty">Không có sản phẩm phù hợp</td></tr></c:if>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>
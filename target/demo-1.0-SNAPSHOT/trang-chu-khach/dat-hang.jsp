<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-khach/layout/header.jspf" %>
<h1>Đặt hàng</h1>
<c:if test="${not empty loi}"><div class="alert loi"><c:out value="${loi}"/></div></c:if>
<div class="layout">
    <form class="card form-grid" method="post" action="${ctx}/dat-hang">
        <b>Thông tin nhận hàng</b>
        <div class="hai">
            <div><label>Người nhận *</label><input type="text" name="tenNguoiNhan" maxlength="100" value="<c:out value='${tt.tenNguoiNhan}'/>" required></div>
            <div><label>Số điện thoại *</label><input type="text" name="soDienThoaiNguoiNhan" maxlength="15" value="<c:out value='${tt.soDienThoaiNguoiNhan}'/>" required></div>
        </div>
        <div class="hai">
            <div><label>Tỉnh / Thành phố *</label><input type="text" name="tinhThanh" maxlength="100" value="<c:out value='${tt.tinhThanh}'/>" required></div>
            <div><label>Quận / Huyện *</label><input type="text" name="quanHuyen" maxlength="100" value="<c:out value='${tt.quanHuyen}'/>" required></div>
        </div>
        <div class="hai">
            <div><label>Phường / Xã *</label><input type="text" name="phuongXa" maxlength="100" value="<c:out value='${tt.phuongXa}'/>" required></div>
            <div><label>Số nhà, đường *</label><input type="text" name="soNhaDuong" maxlength="255" value="<c:out value='${tt.soNhaDuong}'/>" required></div>
        </div>
        <div><label>Ghi chú</label><textarea name="ghiChu" rows="3" maxlength="500"><c:out value="${tt.ghiChu}"/></textarea></div>
        <p class="muted" style="margin:0">Thanh toán khi nhận hàng (COD) · Miễn phí vận chuyển.</p>
        <div><button class="btn lime" type="submit">Xác nhận đặt hàng</button> <a class="btn line" href="${ctx}/gio-hang">Quay lại giỏ hàng</a></div>
    </form>

    <div class="card tom-tat">
        <b>Đơn hàng của bạn</b>
        <c:forEach items="${gio.dong}" var="d">
            <div class="hang" style="font-size:14px"><span>${d.tenSanPham} × ${d.soLuong}</span><span>${ph:tien(d.thanhTien)}</span></div>
        </c:forEach>
        <div class="hang" style="border-top:1px solid var(--line);margin-top:6px"><span>Tạm tính</span><span>${ph:tien(gio.tamTinh)}</span></div>
        <div class="hang"><span>Giảm giá <c:if test="${not empty gio.maPhieu}">(${gio.maPhieu})</c:if></span><span>- ${ph:tien(gio.soTienGiam)}</span></div>
        <div class="hang tong"><span>Tổng cộng</span><span>${ph:tien(gio.tongTien)}</span></div>
    </div>
</div>
<%@ include file="/trang-chu-khach/layout/footer.jspf" %>

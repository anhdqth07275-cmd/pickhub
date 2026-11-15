<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-khach/layout/header.jspf" %>
<div class="card thanh-cong">
    <div style="font-size:48px">🎉</div>
    <h1>Đặt hàng thành công!</h1>
    <p class="muted">Mã tra cứu đơn hàng của bạn</p>
    <div class="ma">${hd.maTraCuu}</div>
    <p>Trạng thái: <b>${ph:nhan(hd.trangThai)}</b> · Cửa hàng sẽ sớm xác nhận đơn của bạn.</p>
</div>
<div class="card" style="margin-top:16px">
    <b>Giao tới:</b> ${hd.tenNguoiNhan} - ${hd.soDienThoaiNguoiNhan}<br>
    ${hd.soNhaDuongGiao}, ${hd.phuongXaGiao}, ${hd.quanHuyenGiao}, ${hd.tinhThanhGiao}
    <table class="tbl" style="margin-top:14px">
        <c:forEach items="${dongSanPham}" var="d">
            <tr><td><b>${d.tenSanPham}</b><br><span class="muted" style="font-size:13px">${d.moTaChiTiet}</span></td>
                <td class="num">${ph:tien(d.donGia)} × ${d.soLuong}</td><td class="num"><b>${ph:tien(d.thanhTien)}</b></td></tr>
        </c:forEach>
        <tr><td colspan="2" class="num muted">Giảm giá</td><td class="num">- ${ph:tien(hd.soTienGiam)}</td></tr>
        <tr><td colspan="2" class="num"><b>Tổng tiền</b></td><td class="num"><b>${ph:tien(hd.tongTien)}</b></td></tr>
    </table>
    <p style="margin-bottom:0"><a class="btn lime" href="${ctx}/cua-hang">Tiếp tục mua sắm</a></p>
</div>
<%@ include file="/trang-chu-khach/layout/footer.jspf" %>

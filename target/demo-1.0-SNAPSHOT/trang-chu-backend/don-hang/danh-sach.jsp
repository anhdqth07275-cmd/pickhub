<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/don-hang">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm mã tra cứu, tên người nhận, SĐT...">
    <select name="trangThai">
        <option value="">Tất cả đơn hàng</option>
        <c:forTokens items="CHO_XAC_NHAN,DA_XAC_NHAN,DANG_GIAO,HOAN_THANH,DA_HUY,GIO_HANG" delims="," var="t">
            <option value="${t}" ${param.trangThai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <button class="btn" type="submit">Lọc</button>
    <span class="muted">Chờ xác nhận: <b>${soChoXacNhan}</b></span>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã đơn</th><th>Khách hàng</th><th>Người nhận</th><th>Tổng tiền</th><th>Trạng thái</th><th>Ngày đặt</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="h">
            <tr>
                <td class="code">${empty h.maTraCuu ? '(giỏ hàng)' : h.maTraCuu}</td>
                <td>${h.khachHang.hoTen}</td>
                <td>${h.tenNguoiNhan}<br><span class="muted">${h.soDienThoaiNguoiNhan}</span></td>
                <td class="nowrap">${ph:tien(h.tongTien)}</td>
                <td><span class="badge ${h.trangThai}">${ph:nhan(h.trangThai)}</span></td>
                <td class="nowrap">${ph:ngayGio(h.ngayDatHang)}</td>
                <td><a class="btn sm line" href="${ctx}/wp-admin/don-hang/chi-tiet?id=${h.maHoaDon}">Xem</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="7" class="empty">Không có đơn hàng phù hợp</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

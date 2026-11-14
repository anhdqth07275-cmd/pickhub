<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/khuyen-mai">← Chương trình khuyến mãi</a>
<div class="card">
    <c:if test="${not empty loi}"><div class="alert loi"><c:out value="${loi}"/></div></c:if>
    <form class="form-grid" method="post" action="${ctx}/wp-admin/khuyen-mai">
        <input type="hidden" name="hanhDong" value="luu">
        <input type="hidden" name="id" value="${ct.maChuongTrinhKhuyenMai}">
        <div><label>Tên chương trình *</label><input type="text" name="tenChuongTrinh" maxlength="150" value="<c:out value='${ct.tenChuongTrinh}'/>" required></div>
        <div><label>Mô tả</label><textarea name="moTa" rows="3" maxlength="500"><c:out value="${ct.moTa}"/></textarea></div>
        <div><label>Bắt đầu *</label><input type="datetime-local" name="ngayBatDau" value="${ct.ngayBatDau}" required></div>
        <div><label>Kết thúc *</label><input type="datetime-local" name="ngayKetThuc" value="${ct.ngayKetThuc}" required></div>
        <p class="muted" style="margin:0">Trạng thái (Sắp chạy → Đang chạy → Kết thúc) tự đổi theo thời gian. Sau khi lưu, vào "Sản phẩm giảm giá" để thêm sản phẩm vào chương trình.</p>
        <div><button class="btn lime" type="submit">Lưu</button> <a class="btn line" href="${ctx}/wp-admin/khuyen-mai">Hủy</a></div>
    </form>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

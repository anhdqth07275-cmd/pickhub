<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/phieu-giam-gia">← Phiếu giảm giá</a>
<div class="card">
    <c:if test="${not empty loi}"><div class="alert loi"><c:out value="${loi}"/></div></c:if>
    <form class="form-grid" method="post" action="${ctx}/wp-admin/phieu-giam-gia">
        <input type="hidden" name="hanhDong" value="luu">
        <input type="hidden" name="id" value="${p.maPhieuGiamGia}">
        <div><label>Mã giảm giá * <span class="muted">(tối đa 30 ký tự, tự đổi thành IN HOA)</span></label>
            <input type="text" name="maGiamGia" maxlength="30" value="<c:out value='${p.maGiamGia}'/>" style="text-transform:uppercase" required></div>
        <div><label>Tên phiếu *</label>
            <input type="text" name="tenPhieu" maxlength="150" value="<c:out value='${p.tenPhieu}'/>" required></div>
        <div>
            <label>Loại giảm *</label>
            <select name="loaiGiam">
                <option value="PHAN_TRAM" ${p.loaiGiam == 'PHAN_TRAM' ? 'selected' : ''}>Phần trăm (%)</option>
                <option value="SO_TIEN" ${p.loaiGiam == 'SO_TIEN' ? 'selected' : ''}>Số tiền (đ)</option>
            </select>
        </div>
        <div><label>Giá trị giảm * <span class="muted">(% trong khoảng 0-100, hoặc số tiền)</span></label>
            <input type="number" name="giaTriGiam" step="any" min="0" value="${p.giaTriGiam}" required></div>
        <div><label>Giảm tối đa (đ) <span class="muted">(chỉ dùng cho loại phần trăm, để trống = không giới hạn)</span></label>
            <input type="number" name="giamToiDa" min="0" value="${p.giamToiDa}"></div>
        <div><label>Giá trị đơn tối thiểu (đ)</label>
            <input type="number" name="giaTriDonToiThieu" min="0" value="${p.giaTriDonToiThieu}"></div>
        <div><label>Giới hạn tổng lượt dùng <span class="muted">(để trống = không giới hạn)</span></label>
            <input type="number" name="gioiHanSuDung" min="1" value="${p.gioiHanSuDung}"></div>
        <div><label>Giới hạn mỗi khách * <span class="muted">(từ 1 trở lên)</span></label>
            <input type="number" name="gioiHanMoiNguoi" min="1" value="${p.gioiHanMoiNguoi}" required></div>
        <div><label>Bắt đầu *</label><input type="datetime-local" name="ngayBatDau" value="${p.ngayBatDau}" required></div>
        <div><label>Kết thúc *</label><input type="datetime-local" name="ngayKetThuc" value="${p.ngayKetThuc}" required></div>
        <c:if test="${not empty p.maPhieuGiamGia}">
            <p class="muted" style="margin:0">Đã dùng ${p.soLanDaSuDung} lượt. Trạng thái hiện tại: ${ph:nhan(p.trangThai)}.</p>
        </c:if>
        <div><button class="btn lime" type="submit">Lưu</button> <a class="btn line" href="${ctx}/wp-admin/phieu-giam-gia">Hủy</a></div>
    </form>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

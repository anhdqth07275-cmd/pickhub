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
        <%-- note code nhu nay nhe ban: co 2 gioi han KHAC NHAU, tach rieng cho de nhin:
             - Tong luot dung  : ca he thong cho phep toi da bao nhieu lan (de trong = khong gioi han)
             - Moi khach       : 1 tai khoan khach hang duoc dung toi da bao nhieu lan (>= 1, bat buoc)
             Ca 2 duoc kiem tra khi khach ap ma (PhieuGiamGiaService.kiemTraVaTinhGiam) va khi dat hang (SuDungPhieuService.ghiNhanSuDung) --%>
        <div class="limit-box">
            <h4>Giới hạn sử dụng</h4>
            <p class="hint">Hệ thống kiểm tra cả hai giới hạn khi khách áp mã và khi đặt hàng.</p>
            <div style="display:grid;gap:12px">
                <div class="fld"><label>Giới hạn mỗi khách hàng * <span class="muted">(số lần 1 tài khoản được dùng mã này)</span></label>
                    <input type="number" name="gioiHanMoiNguoi" min="1" value="${empty p.gioiHanMoiNguoi ? 1 : p.gioiHanMoiNguoi}" required></div>
                <div class="fld"><label>Tổng lượt dùng toàn hệ thống <span class="muted">(để trống = không giới hạn)</span></label>
                    <input type="number" name="gioiHanSuDung" min="1" value="${p.gioiHanSuDung}"></div>
            </div>
            <p class="hint" style="margin-top:10px">Ví dụ: tổng <b>100</b> lượt, mỗi khách <b>2</b> lần → mỗi tài khoản dùng tối đa 2 lần và cả hệ thống tối đa 100 lượt.<br>
                Lượt đã hoàn (hủy đơn) không bị tính vào giới hạn. Bán tại quầy POS là khách lẻ nên chỉ kiểm tra giới hạn tổng.</p>
            <c:if test="${not empty p.maPhieuGiamGia}">
                <p style="margin:8px 0 0"><a class="btn sm line" href="${ctx}/wp-admin/phieu-giam-gia/su-dung?maPhieu=${p.maPhieuGiamGia}">Xem từng khách đã dùng mấy lần →</a></p>
            </c:if>
        </div>
        <div><label>Bắt đầu *</label><input type="datetime-local" name="ngayBatDau" value="${p.ngayBatDau}" required></div>
        <div><label>Kết thúc *</label><input type="datetime-local" name="ngayKetThuc" value="${p.ngayKetThuc}" required></div>
        <c:if test="${not empty p.maPhieuGiamGia}">
            <p class="muted" style="margin:0">Đã dùng ${p.soLanDaSuDung} lượt. Trạng thái hiện tại: ${ph:nhan(p.trangThai)}.</p>
        </c:if>
        <div><button class="btn lime" type="submit">Lưu</button> <a class="btn line" href="${ctx}/wp-admin/phieu-giam-gia">Hủy</a></div>
    </form>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

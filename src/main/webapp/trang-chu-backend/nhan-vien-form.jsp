<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>

<div class="card" style="max-width:850px">
    <div style="display:flex;justify-content:space-between;align-items:center;gap:12px;margin-bottom:18px">
        <div>
            <h2 style="margin:0">Thông tin nhân viên</h2>
            <p class="muted" style="margin:6px 0 0">Cập nhật thông tin cá nhân và quyền truy cập.</p>
        </div>
        <a class="btn" href="${ctx}/wp-admin/nhan-vien">Quay lại</a>
    </div>

    <form method="post" action="${ctx}/wp-admin/nhan-vien" class="form-grid">
        <input type="hidden" name="hanhDong" value="cap-nhat">
        <input type="hidden" name="id" value="${nhanVien.maNhanVien}">

        <label>Họ và tên
            <input name="hoTen" maxlength="100" minlength="2" required value="<c:out value='${nhanVien.hoTen}'/>">
        </label>
        <label>Email
            <input type="email" name="email" maxlength="255" required value="<c:out value='${nhanVien.thuDienTu}'/>">
        </label>
        <label>Số điện thoại
            <input type="tel" name="soDienThoai" maxlength="15" pattern="[0-9]{9,15}" required
                   value="<c:out value='${nhanVien.soDienThoai}'/>">
        </label>
        <label>Ngày sinh
            <input type="date" name="ngaySinh" value="${nhanVien.ngaySinh}">
        </label>

        <c:choose>
            <c:when test="${laQuanTri}">
                <label>Quyền nhân viên
                    <select name="vaiTro" required>
                        <option value="NHAN_VIEN" ${nhanVien.vaiTro == 'NHAN_VIEN' ? 'selected' : ''}>Nhân viên</option>
                        <option value="QUAN_LY" ${nhanVien.vaiTro == 'QUAN_LY' ? 'selected' : ''}>Quản lý</option>
                        <option value="QUAN_TRI" ${nhanVien.vaiTro == 'QUAN_TRI' ? 'selected' : ''}>Quản trị viên</option>
                    </select>
                </label>
            </c:when>
            <c:otherwise>
                <label>Quyền hiện tại
                    <input value="Nhân viên" readonly>
                </label>
            </c:otherwise>
        </c:choose>

        <div style="display:flex;gap:10px;align-items:center">
            <button class="btn lime" type="submit">Lưu thay đổi</button>
            <a class="btn" href="${ctx}/wp-admin/nhan-vien">Hủy</a>
        </div>
    </form>
</div>

<c:if test="${laQuanTri}">
    <div class="card" style="max-width:850px;margin-top:18px">
        <h2 style="margin-top:0">Đặt lại mật khẩu</h2>
        <p class="muted">Chức năng riêng biệt. Đặt mật khẩu mới không làm thay đổi thông tin cá nhân hoặc quyền của nhân viên.</p>
        <form method="post" action="${ctx}/wp-admin/nhan-vien" class="form-grid"
              onsubmit="return this.matKhauMoi.value === this.xacNhanMatKhau.value && this.matKhauMoi.value.length >= 8
                  ? true : (alert('Mật khẩu phải khớp nhau và có ít nhất 8 ký tự.'), false)">
            <input type="hidden" name="hanhDong" value="dat-lai-mat-khau">
            <input type="hidden" name="id" value="${nhanVien.maNhanVien}">
            <label>Mật khẩu mới
                <input type="password" name="matKhauMoi" minlength="8" autocomplete="new-password" required>
            </label>
            <label>Xác nhận mật khẩu mới
                <input type="password" name="xacNhanMatKhau" minlength="8" autocomplete="new-password" required>
            </label>
            <button class="btn danger" type="submit">Đặt lại mật khẩu</button>
        </form>
    </div>
</c:if>

<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<div class="card">
    <dl class="info-grid">
        <div><dt>Họ tên</dt><dd>${nv.hoTen}</dd></div>
        <div><dt>Email</dt><dd>${nv.thuDienTu}</dd></div>
        <div><dt>Số điện thoại</dt><dd>${nv.soDienThoai}</dd></div>
        <div><dt>Vai trò</dt><dd>${ph:nhan(nv.vaiTro)}</dd></div>
        <div><dt>Ngày sinh</dt><dd>${ph:ngay(nv.ngaySinh)}</dd></div>
        <div><dt>Trạng thái</dt><dd><span class="badge ${nv.trangThai}">${ph:nhan(nv.trangThai)}</span></dd></div>
        <div><dt>Đăng nhập gần nhất</dt><dd>${ph:ngayGio(nv.ngayDangNhapCuoi)}</dd></div>
        <div><dt>Ngày tạo</dt><dd>${ph:ngayGio(nv.ngayTao)}</dd></div>
    </dl>
    <%-- TODO (M2): form sua ho so + doi mat khau (can nhap mat khau cu) + doi anh dai dien --%>
    <p class="muted" style="margin-bottom:0">Chức năng sửa hồ sơ, đổi mật khẩu sẽ hoàn thiện ở module Tài khoản.</p>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

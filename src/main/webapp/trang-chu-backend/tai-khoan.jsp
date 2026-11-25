<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- TAI KHOAN CA NHAN (backend). TaiKhoanServlet gui sang: nv (NhanVien dang dang nhap), tab ("ho-so" | "mat-khau").
     note code nhu nay nhe ban: 2 chuc nang la 2 TAB co link rieng (?tab=ho-so, ?tab=mat-khau) thay vi 2 o canh nhau nhu truoc.
     Loi/thanh cong hien o thanh flash tren cung (header.jspf), va quay lai DUNG tab vua thao tac. --%>

<div class="profile-wrap">

    <%-- ============ THE THONG TIN TOM TAT ============ --%>
    <aside class="card profile-card">
        <div class="avatar-lg">${sessionScope.taiKhoan.chuCaiDau}</div>
        <h2><c:out value="${nv.hoTen}"/></h2>
        <div class="mail"><c:out value="${nv.thuDienTu}"/></div>
        <span class="badge ${nv.vaiTro == 'QUAN_TRI' ? 'blue' : 'orange'}">${ph:nhan(nv.vaiTro)}</span>
        <div class="profile-meta">
            <div><span>Số điện thoại</span><c:out value="${nv.soDienThoai}"/></div>
            <div><span>Ngày sinh</span>${empty nv.ngaySinh ? 'Chưa cập nhật' : ph:ngay(nv.ngaySinh)}</div>
            <div><span>Ngày tạo tài khoản</span>${ph:ngayGio(nv.ngayTao)}</div>
            <div><span>Đăng nhập gần nhất</span>${empty nv.ngayDangNhapCuoi ? '—' : ph:ngayGio(nv.ngayDangNhapCuoi)}</div>
        </div>
    </aside>

    <%-- ============ KHUNG CHINH: 2 TAB ============ --%>
    <section class="card">
        <nav class="tabs-nav">
            <a class="${tab == 'ho-so' ? 'on' : ''}" href="${ctx}/wp-admin/tai-khoan?tab=ho-so">Thông tin cá nhân</a>
            <a class="${tab == 'mat-khau' ? 'on' : ''}" href="${ctx}/wp-admin/tai-khoan?tab=mat-khau">Đổi mật khẩu</a>
        </nav>

        <c:choose>
            <%-- ---------- TAB 2: DOI MAT KHAU ---------- --%>
            <c:when test="${tab == 'mat-khau'}">
                <h3 class="panel-title">Đổi mật khẩu</h3>
                <p class="panel-sub">Nên dùng mật khẩu riêng, không trùng với tài khoản khác.</p>
                <form method="post" action="${ctx}/wp-admin/tai-khoan" autocomplete="off" style="max-width:480px">
                    <input type="hidden" name="hanhDong" value="doi-mat-khau">
                    <div class="form-stack">
                        <div class="fld">
                            <label for="mkCu">Mật khẩu hiện tại <span class="req">*</span></label>
                            <input id="mkCu" class="mk" type="password" name="matKhauCu" required autocomplete="current-password">
                        </div>
                        <div class="fld">
                            <label for="mkMoi">Mật khẩu mới <span class="req">*</span></label>
                            <input id="mkMoi" class="mk" type="password" name="matKhauMoi" required minlength="8" autocomplete="new-password">
                            <ul class="rule-list">
                                <li>Ít nhất 8 ký tự</li>
                                <li>Gồm cả chữ và số</li>
                                <li>Phải khác mật khẩu hiện tại</li>
                            </ul>
                        </div>
                        <div class="fld">
                            <label for="mkLai">Nhập lại mật khẩu mới <span class="req">*</span></label>
                            <input id="mkLai" class="mk" type="password" name="nhapLai" required minlength="8" autocomplete="new-password">
                        </div>
                        <label class="show-pass"><input type="checkbox" id="hienMk"> Hiện mật khẩu</label>
                    </div>
                    <div class="form-foot">
                        <button class="btn lime" type="submit">Đổi mật khẩu</button>
                        <a class="btn line" href="${ctx}/wp-admin/tai-khoan?tab=ho-so">Hủy</a>
                    </div>
                </form>
                <script>
                    // note code nhu nay nhe ban: tick "Hien mat khau" thi doi type password <-> text cho ca 3 o
                    document.getElementById('hienMk').addEventListener('change', function () {
                        document.querySelectorAll('input.mk').forEach(function (o) { o.type = this.checked ? 'text' : 'password'; }, this);
                    });
                </script>
            </c:when>

            <%-- ---------- TAB 1: THONG TIN CA NHAN ---------- --%>
            <c:otherwise>
                <h3 class="panel-title">Thông tin cá nhân</h3>
                <p class="panel-sub">Cập nhật họ tên, số điện thoại và ngày sinh của bạn.</p>
                <form method="post" action="${ctx}/wp-admin/tai-khoan" style="max-width:560px">
                    <input type="hidden" name="hanhDong" value="cap-nhat-ho-so">
                    <div class="form-stack">
                        <div class="fld">
                            <label for="hoTen">Họ và tên <span class="req">*</span></label>
                            <input id="hoTen" type="text" name="hoTen" required minlength="2" maxlength="100" value="<c:out value='${nv.hoTen}'/>">
                        </div>
                        <div class="form-2col">
                            <div class="fld">
                                <label for="sdt">Số điện thoại <span class="req">*</span></label>
                                <input id="sdt" type="tel" name="soDienThoai" required pattern="[0-9]{9,15}" title="9 - 15 chữ số" value="<c:out value='${nv.soDienThoai}'/>">
                            </div>
                            <div class="fld">
                                <label for="ngaySinh">Ngày sinh</label>
                                <input id="ngaySinh" type="date" name="ngaySinh" value="${nv.ngaySinh}">
                            </div>
                        </div>
                        <div class="form-2col">
                            <div class="fld">
                                <label>Email đăng nhập</label>
                                <input type="text" value="<c:out value='${nv.thuDienTu}'/>" disabled>
                                <p class="hint">Email không đổi được.</p>
                            </div>
                            <div class="fld">
                                <label>Vai trò</label>
                                <input type="text" value="${ph:nhan(nv.vaiTro)}" disabled>
                                <p class="hint">Do quản trị viên phân quyền.</p>
                            </div>
                        </div>
                    </div>
                    <div class="form-foot">
                        <button class="btn lime" type="submit">Lưu thay đổi</button>
                        <a class="btn line" href="${ctx}/wp-admin">Quay lại</a>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </section>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

# PickHub - Hướng dẫn M1 Tài khoản & Phân quyền

## 1. Phạm vi
- Nhân viên: đăng nhập/đăng xuất, đổi mật khẩu trong hồ sơ; Quản trị viên có thể đặt lại mật khẩu nhân viên.
- Khách hàng: đăng ký và đăng nhập ngay, không cần xác thực email; khách hàng có thể đổi mật khẩu trong hồ sơ.
- BCrypt cho mọi mật khẩu; tài khoản tự khóa sau 5 lần đăng nhập sai.

## 2. Vai trò
- `QUAN_TRI`: toàn quyền; tạo và phân vai trò mọi cấp; quản lý tất cả nhân viên.
- `QUAN_LY`: quản lý nghiệp vụ và chỉ được tạo/khóa/mở khóa tài khoản có vai trò `NHAN_VIEN`; không được tự cấp vai trò quản lý/quản trị.
- `NHAN_VIEN`: xem các trang nghiệp vụ được phép; chỉ được ghi đơn hàng và cập nhật tài khoản cá nhân; không vào quản lý nhân viên, khuyến mãi và phiếu giảm giá.
- `KhachHang`: tài khoản riêng, không thuộc 3 vai trò backend và không thể truy cập `/wp-admin/*`.

## 3. Đường dẫn
| Tác vụ | URL |
|---|---|
| Trang khách hàng | `/` |
| Khách hàng đăng ký | `/tai-khoan/dang-ky` |
| Khách hàng đăng nhập/đăng xuất | `/tai-khoan/dang-nhap`, `/tai-khoan/dang-xuat` |
| Hồ sơ khách hàng | `/tai-khoan/ho-so` |
| Nhân viên đăng nhập/đăng xuất | `/wp-admin/dang-nhap`, `/wp-admin/dang-xuat` |
| Hồ sơ nhân viên | `/wp-admin/tai-khoan` |
| Quản lý nhân viên | `/wp-admin/nhan-vien` |


## 4. Cấu hình và chạy
1. Chạy `pickhub_sqlserver_tiengviet.sql` trong SQL Server để tạo database `PickHub`.
2. Mở `src/main/java/util/HibernateConfig.java`, chỉnh server, database, username và password theo SQL Server trên máy.
3. Mở `pom.xml` bằng IntelliJ, chọn Maven Reload Project. Bật Annotation Processing cho Lombok.
4. Chạy Tomcat 10.x (Jakarta Servlet 5).
5. Vào `/` để thử luồng khách hàng hoặc `/wp-admin/dang-nhap` để thử nhân viên.

Tài khoản mẫu: `admin@pickhub.vn`, `huong.nguyen@pickhub.vn`, `trang.pham@pickhub.vn`; mật khẩu đều là `PickHub@123`.

**Lưu ý:** file SQL hiện tại lưu vai trò trực tiếp trong `NhanVien.vaiTro` (`QUAN_TRI`, `QUAN_LY`, `NHAN_VIEN`), vì vậy code M1 dùng đúng cột này, không tự thêm bảng VaiTro/Quyen vào database hiện tại.

## 5. Lưu ý khi bỏ OTP
- Đăng ký khách hàng thành công sẽ tạo tài khoản ở trạng thái `HOAT_DONG` và có thể đăng nhập ngay.
- Không còn luồng xác thực email hoặc quên mật khẩu qua mã OTP.
- Nhân viên quên mật khẩu cần liên hệ Quản trị viên để được đặt lại tại trang Quản lý nhân viên.
- Khách hàng quên mật khẩu cần liên hệ bộ phận hỗ trợ; không dùng biểu mẫu đặt lại mật khẩu qua email.
- Nếu database cũ có khách hàng `CHUA_XAC_THUC`, hãy cân nhắc xác nhận danh sách trước khi chuyển trạng thái sang `HOAT_DONG`.

# PickHub – Trang chủ backend `/wp-admin`

## Chạy thử
1. Chạy `pickhub_sqlserver_tiengviet.sql` trong SQL Server (tạo DB + dữ liệu mẫu).
2. Reload Maven (pom đã thêm `at.favre.lib:bcrypt`, nâng Lombok lên 1.18.30, thêm UTF-8). IntelliJ: bật *Annotation Processing* cho Lombok.
3. Deploy lên **Tomcat 10.x** (Jakarta). Mở `http://localhost:8080/<context>/wp-admin`
4. Đăng nhập: `admin@pickhub.vn` / `PickHub@123` (sai 5 lần sẽ bị khóa).

## Cấu trúc (khớp thư mục của bạn)
| Thư mục | Nội dung |
|---|---|
| `entity/` | 18 entity + `BaseEntity` (ngayTao/ngayCapNhat) |
| `repository/` | `BaseRepository<T>` (timKiem, phân trang, luu, capNhat, dem...) + 18 repo + `ThongKeRepository` |
| `service/` | `ThongKeService` (dashboard), `DonHangService` (chuyển trạng thái), `XacThucService` (đăng nhập) |
| `controller/` | `BaseServlet` + 19 servlet, mọi URL bắt đầu `/wp-admin` |
| `filter/` | `EncodingFilter`, `WpAdminFilter` (đăng nhập + phân quyền + chặn truy cập thẳng JSP) |
| `listener/` | `KhoiTaoListener` |
| `enums/` | `TrangThaiHoaDon` (luồng chuyển trạng thái) |
| `dto/`, `exception/`, `util/` | PhanTrang, BoLoc, TongQuanDTO..., NghiepVuException, GiaoDich, MatKhauUtil, SlugUtil, DinhDangUtil |
| `webapp/trang-chu-backend/` | JSP của từng module + `layout/` (header, footer, phân trang) |
| `webapp/assets/admin.css` | giao diện |
| `webapp/WEB-INF/pickhub.tld` | hàm `${ph:tien(x)}`, `${ph:nhan(x)}`, `${ph:ngayGio(x)}`... |

## Bản đồ module ↔ URL ↔ bảng DB
| Menu | URL | Servlet | JSP | Bảng | Mức hoàn thiện |
|---|---|---|---|---|---|
| Dashboard | `/wp-admin` | DashboardServlet | dashboard | 18 bảng (đếm) | Xong |
| Đơn hàng | `/wp-admin/don-hang`, `/chi-tiet` | HoaDonServlet, HoaDonChiTietServlet | don-hang/* | HoaDon, HoaDonChiTiet, LichSuTrangThaiHoaDon | Xem + xác nhận→giao→hoàn thành. **Chưa có HỦY** |
| Sản phẩm | `/wp-admin/san-pham`, `/chi-tiet` | SanPhamServlet, SanPhamChiTietServlet | san-pham/* | SanPham, ChiTietSanPham, HinhAnhSanPham | Xem + ẩn/hiện. **Chưa có form thêm/sửa, upload ảnh** |
| Tồn kho | `/wp-admin/ton-kho` | TonKhoServlet | san-pham/ton-kho | ChiTietSanPham | Xem + lọc sắp hết. **Chưa có điều chỉnh tồn** |
| Danh mục | `/wp-admin/danh-muc` | DanhMucServlet | san-pham/danh-muc | DanhMuc | Xem + ngừng/kích hoạt. **Chưa có form** |
| Thương hiệu | `/wp-admin/thuong-hieu` | ThuongHieuServlet | thuong-hieu/* | ThuongHieu | **CRUD đầy đủ – module mẫu** |
| Thuộc tính | `/wp-admin/thuoc-tinh?loai=` | ThuocTinhServlet | san-pham/thuoc-tinh | MauSac, KhoiLuong, ChatLieu, ChuViTayCam | Xem + thêm + ngừng. **Chưa có sửa** |
| Khách hàng | `/wp-admin/khach-hang` | KhachHangServlet | khach-hang | KhachHang | Xem + khóa/mở khóa |
| Nhân viên | `/wp-admin/nhan-vien` | NhanVienServlet | nhan-vien | NhanVien | Xem + khóa/mở khóa. **Chưa có form tạo** |
| Khuyến mãi | `/wp-admin/khuyen-mai`, `/chi-tiet` | KhuyenMaiServlet, KhuyenMaiChiTietServlet | khuyen-mai/* | ChuongTrinhKhuyenMai, ChiTietChuongTrinhKhuyenMai | **M4 xong**: thêm/sửa chương trình, tạm dừng/chạy lại, thêm/sửa/gỡ sản phẩm giảm giá, hàm `tinhGiaSauKhuyenMai`, scheduler |
| Phiếu giảm giá | `/wp-admin/phieu-giam-gia`, `/su-dung` | PhieuGiamGiaServlet, SuDungPhieuServlet | phieu-giam-gia/* | PhieuGiamGia, SuDungPhieuGiamGia | **M4 xong**: thêm/sửa phiếu, tạm dừng/chạy lại, ô "Thử phiếu", hàm `kiemTraVaTinhGiam`, `ghiNhanSuDung`, `hoanLai`, scheduler |
| Đặt sân / Bán tại quầy | `/wp-admin/dat-san`, `/ban-hang-tai-quay` | SapRaMatServlet | sap-ra-mat | – | Trang "Sắp ra mắt" |
| Tài khoản | `/wp-admin/tai-khoan` | TaiKhoanServlet | tai-khoan | NhanVien | Chỉ xem |
| Đăng nhập/xuất | `/wp-admin/dang-nhap`, `/dang-xuat` | DangNhapServlet, DangXuatServlet | dang-nhap | NhanVien | Xong (bản tối giản) |

Tìm trong code: `note code nhu nay nhe ban` = ghi chú nghiệp vụ; `TODO` = việc còn lại.

## Lưu ý
- Mã chưa build được trong môi trường của mình (không có Maven Central) → lần đầu chạy nếu IntelliJ báo lỗi, gửi lại log để sửa.
- Dữ liệu mẫu có ngày hiện tại là 11/11/2026 nên biểu đồ "7 ngày" ít cột; bấm tab "6 tháng" để thấy rõ doanh thu.
- `HibernateConfig` vẫn chứa mật khẩu `sa` (đã đẩy lên GitHub) → nên đổi mật khẩu và đưa ra `db.properties`.

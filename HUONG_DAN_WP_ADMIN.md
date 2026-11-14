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


---
# M5 – Giỏ hàng & Đặt hàng (phía khách)

## Thử nhanh
1. Mở `/cua-hang` (trang demo liệt kê biến thể đang bán) → bấm **Thêm vào giỏ** (AJAX). Chưa đăng nhập sẽ chuyển sang `/dang-nhap` rồi quay lại và **vẫn thêm sản phẩm đã chọn**.
2. Tài khoản khách mẫu: `tuankiet.do@example.com` / `PickHub@123` (khách này có sẵn giỏ hàng trong dữ liệu mẫu).
3. `/gio-hang`: sửa số lượng, xóa dòng, xóa giỏ, áp / bỏ mã giảm giá → `/dat-hang` nhập địa chỉ → `/dat-hang/thanh-cong`.
4. Vào `/wp-admin/don-hang` thấy đơn mới ở trạng thái *Chờ xác nhận*; `/wp-admin/ton-kho` thấy tồn đã giảm.

Mã để thử: `PICKHUB10` (10%, tối đa 300.000, đơn từ 1.000.000), `VIP5` (5%), `WELCOME50K`.
`DOUBLE11` đã hết hạn (13/11) nên sẽ báo "đã hết hạn" – dùng để thử trường hợp từ chối.
Dữ liệu mẫu có khuyến mãi đã qua hạn nên giỏ mẫu sẽ hiện cảnh báo **"Giá ... đã thay đổi"** (đúng chức năng làm mới giá).

## Bản đồ M5
| Việc | File |
|---|---|
| Giỏ hàng = HoaDon `GIO_HANG` (thêm, sửa SL, xóa dòng, cộng dồn, không vượt tồn) | `service/GioHangService`, `controller/GioHangServlet`, `controller/ApiGioHangServlet` |
| Chốt giá (donGiaGoc, donGia, mã khuyến mãi) và làm mới giá | `GioHangService.lamMoiGia`, `service/KhuyenMaiService.tinhGia` (giá thấp nhất, không cộng dồn) |
| Áp phiếu giảm giá, tính tamTinh / soTienGiam / tongTien | `service/PhieuGiamGiaService`, `GioHangService.tinhLaiTong` |
| Đặt hàng 1 transaction: kiểm tra lại → trừ tồn (UPDATE có điều kiện) → tăng lượt KM → ghi phiếu → sinh maTraCuu → CHO_XAC_NHAN → ghi lịch sử | `service/DatHangService`, `service/SuDungPhieuService`, `service/MaTraCuuGenerator`, `controller/DatHangServlet` |
| Bắt buộc đăng nhập khách | `filter/KhachHangFilter`, `controller/DangNhapKhachServlet` |
| Giao diện | `webapp/trang-chu-khach/*.jsp`, `webapp/assets/cua-hang.css` |

Đối chiếu số liệu: biến thể 1 giảm 15% = 4.156.500đ; đơn 4.926.600đ + DOUBLE11 (11%, tối đa 600.000) = 541.926đ.

## Còn lại (TODO)
- Gửi email xác nhận đơn (MailUtil) – `DatHangService.datHang`.
- Khách xem "Đơn hàng của tôi" / hủy đơn + hoàn tồn, hoàn lượt khuyến mãi, hoàn phiếu (`SuDungPhieuService.hoanLai`) – module M6/M7.
- `/cua-hang` chỉ là trang demo; storefront đầy đủ thuộc M3. Đăng ký / quên mật khẩu khách thuộc M2.

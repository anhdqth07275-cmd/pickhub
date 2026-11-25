# Thay đổi theo yêu cầu `vungu.txt`

> Quy ước: mọi chỗ cần chú ý trong code đều có comment dạng `// note code nhu nay nhe ban: ...`
> (JSP: `<%-- note code ... --%>`, CSS: `/* note code ... */`).
> **Không đổi** `pom.xml`, `web.xml`, database, các package/file khác. CSS chỉ *thêm class mới* ở cuối file.

## 1. Cách tích hợp

1. Giải nén đè lên thư mục dự án (hoặc `git pull` nếu dùng git). Không cần chạy thêm script SQL.
2. IntelliJ: **Maven → Reload project**, rồi **Build → Rebuild Project**, chạy lại Tomcat.
3. Nhớ tải lại cứng trình duyệt (**Ctrl + F5**) để nhận `admin.css`, `pos.css`, `pos.js` mới.

## 2. Bảng đối chiếu yêu cầu → file

| # | Yêu cầu | File sửa / thêm |
|---|---------|-----------------|
| 1 | Phân quyền: chỉ QUAN_LY + QUAN_TRI vào backend, vai trò khác đẩy sang POS | `util/PhanQuyenUtil` (thêm `duocVaoAdmin`), `filter/WpAdminFilter` |
| 2 | CRUD sản phẩm + CSS | `san-pham/danh-sach.jsp`, `form.jsp` (trước là trang TODO), `chi-tiet.jsp`, `admin.css`, `SanPhamServlet` |
| 3 | Giới hạn mã giảm giá theo khách | `phieu-giam-gia/danh-sach.jsp`, `form.jsp`, `su-dung.jsp`, `SuDungPhieuServlet`, `PhieuGiamGiaService`, `SuDungPhieuGiamGiaRepository`, `dto/ThongKeKhachDungPhieuDTO` (mới) |
| 4 | Tài khoản cá nhân: 2 tab riêng | `tai-khoan.jsp`, `TaiKhoanServlet`, `service/TaiKhoanService` (mới) |
| 5 | Bộ lọc sản phẩm chi tiết | `san-pham/danh-sach.jsp`, `SanPhamRepository` (sửa lọc khoảng giá) |
| 6 | Popup "Xem và chọn voucher" ở POS | `jsp/pos/ban-hang.jsp`, `assets/pos.js`, `assets/pos.css`, `PosPhieuDanhSachApiServlet` (mới), `PhieuKhaDungDTO` (mới), `IPhieuGiamGiaService/Impl`, `IPhieuGiamGiaRepository/Impl` |
| 7 | Tồn kho bị trống | `TonKhoServlet`, `TonKhoService`, `san-pham/ton-kho.jsp` |

> Backend của dự án nằm ở **`/wp-admin/*`** (không phải `/admin/*`), nên bộ lọc áp dụng cho `/wp-admin/*`.

## 3. Nguyên nhân các lỗi đã tìm ra

- **Tồn kho trống**: servlet gửi biến `ketQua` nhưng JSP đọc `pt` (và thiếu `duongDan`); tên tham số lọc servlet (`tinhTrang`) lệch với form và link dashboard (`trangThai`, `canhBao`).
- **Chi tiết sản phẩm trống**: JSP đọc `sp`, `bienThe`, `hinhAnh` nhưng servlet gửi `sanPham`, `danhSachBienThe`, `danhSachAnh`.
- **Lọc khoảng giá sai**: "giá từ" và "giá đến" nằm ở 2 `EXISTS` riêng nên sản phẩm có biến thể rẻ + biến thể đắt vẫn lọt vào khoảng giữa. Đã gộp thành 1 `EXISTS`.
- **Kiểm kê tồn = 0 bị từ chối**: `docSo()` coi số < 1 là không hợp lệ. Đã dùng `DocSoUtil.soNguyen`.
- **Phân trang hiện `Â·`, `dÃ²ng`**: `phan-trang.jspf` thiếu khai báo `pageEncoding="UTF-8"` (đã thêm 1 dòng).

## 4. Luồng phân quyền (yêu cầu 1)

```
Chưa đăng nhập ............. -> /wp-admin/dang-nhap
QUAN_TRI / QUAN_LY ......... -> vào /wp-admin bình thường (vẫn xét coQuyen từng trang như cũ)
NHAN_VIEN (hoặc vai trò lạ)  -> redirect /pos
/wp-admin/dang-xuat ........ -> ai đã đăng nhập cũng bấm được
```

Trang admin lưu người dùng ở session `taiKhoan`, còn POS (`PosFilter`) đọc session `nhanVien`.
Khi đẩy nhân viên sang POS, filter nạp thêm `nhanVien` nên **không phải đăng nhập lại**.

## 5. Những điều cần biết (giới hạn của DB 18 bảng)

1. **Bình luận**: DB hiện **không có bảng bình luận** nên phần "giới hạn bình luận theo khách" chưa làm được. Cần thêm bảng (vd `BinhLuan`) rồi mới giới hạn.
2. **Khuyến mãi theo khách**: `ChiTietChuongTrinhKhuyenMai` chỉ có `soLuongGioiHan` (tổng theo sản phẩm), không có giới hạn theo từng khách. Phiếu giảm giá thì **có** (`gioiHanMoiNguoi`) và đã được hiển thị rõ ở danh sách, form, trang lượt dùng.
3. **POS và giới hạn theo khách**: bán tại quầy là khách lẻ nên không biết "mỗi người đã dùng mấy lần". POS chỉ kiểm tra **tổng lượt** (`gioiHanSuDung`, chỉ đọc), còn giới hạn mỗi khách vẫn do đơn online kiểm tra. Đoạn code kiểm tra theo khách đã để sẵn trong comment ở `PhieuGiamGiaServiceImpl.tinhGiam` để bật khi POS có chọn khách hàng.
4. **NHAN_VIEN không vào được `/wp-admin/tai-khoan`** (theo đúng yêu cầu "chỉ QUAN_LY/QUAN_TRI vào backend"), nên nhân viên hiện chưa có chỗ đổi mật khẩu. Nếu cần, thêm trang đổi mật khẩu ở POS.

## 6. Checklist tự kiểm thử trên máy bạn

- [ ] Đăng nhập `phuc.le@pickhub.vn` hoặc `admin@pickhub.vn` (mật khẩu `PickHub@123`) → vào được `/wp-admin`.
- [ ] Đăng nhập `trang.pham@pickhub.vn` ở `/wp-admin/dang-nhap` → tự sang `/pos`, không phải đăng nhập lại.
- [ ] Sản phẩm: lọc giá 1.000.000 – 3.000.000 + "Còn hàng"; sang trang 2 vẫn giữ bộ lọc.
- [ ] Sản phẩm: Thêm → để trống tên → form hiện lỗi và **giữ lại** dữ liệu đã gõ; Sửa; Chi tiết (thêm biến thể, thêm ảnh); Xóa sản phẩm chưa có biến thể.
- [ ] Tồn kho: có dữ liệu (dữ liệu mẫu có 30 biến thể, chia trang); `?canhBao=1` chỉ còn biến thể sắp hết / hết hàng; kiểm kê về 0 → trạng thái "Hết hàng".
- [ ] Tài khoản: đổi tab bằng link `?tab=ho-so` / `?tab=mat-khau`; sai mật khẩu cũ → quay lại đúng tab mật khẩu.
- [ ] Phiếu giảm giá: cột "Mỗi khách", nút "Lượt dùng" → bảng khách đã dùng / tối đa.
- [ ] POS: thêm hàng ≥ 800.000đ → bấm **🎟 Voucher** → thấy voucher đủ điều kiện xếp trước → **Chọn** → hóa đơn hiện "Đã áp mã".

# PickHub POS – Bán hàng tại quầy (Java Servlet/JSP, MVC thuần)

Hệ thống bán hàng tại quầy cho cửa hàng vợt và phụ kiện Pickleball. Java 11+, Servlet/JSP (Jakarta, Tomcat 10+), Hibernate 5.6, SQL Server.
Các chức năng khác (khách hàng, bán online, quản trị) đã tạm đóng, xem `docs/PHAM_VI.md`.

## 1. Chạy dự án trên IntelliJ IDEA

1. **Cài**: JDK 11 trở lên, Tomcat **10.1** (bản 9 trở xuống KHÔNG chạy vì dự án dùng `jakarta.servlet`), SQL Server + SSMS.
2. **Tạo database** (SSMS, chạy lần lượt):
   1. `sql/01_PickHub_SQLServer.sql` – file SQL của nhóm (18 bảng + dữ liệu mẫu).
   2. `sql/02_Migration_POS.sql` – bổ sung 4 cột cho `HoaDon` và tài khoản "Khách lẻ" (có giải thích ở đầu file; chạy lại nhiều lần vẫn an toàn).
3. **Cấu hình kết nối**: mở `src/main/resources/pickhub.properties`, sửa `db.password` thành mật khẩu SQL Server của bạn.
   Có thể chạy thử `HibernateUtil.main()` (chuột phải → Run), in ra `SessionFactory` là kết nối thành công.
4. **Mở project**: IntelliJ → *File ▸ Open* → chọn thư mục `PickHubPOS` (file `pom.xml`) → chờ Maven tải thư viện.
5. **Cấu hình Tomcat**: *Run ▸ Edit Configurations ▸ + ▸ Tomcat Server ▸ Local* → chọn Tomcat 10.1 → tab *Deployment* ▸ **+ ▸ Artifact ▸ PickHubPOS:war exploded** → *Application context* đặt `/` (hoặc `/PickHubPOS`).
6. Bấm **Run**, mở `http://localhost:8080/` → tự chuyển tới đăng nhập.

Không cần bật Lombok hay thêm plugin: entity viết getter/setter tường minh.

## 2. Tài khoản đăng nhập mẫu (từ file SQL của nhóm)
Mật khẩu chung: `PickHub@123`

| Email | Vai trò |
|---|---|
| admin@pickhub.vn | QUAN_TRI |
| huong.nguyen@pickhub.vn | QUAN_LY |
| trang.pham@pickhub.vn | NHAN_VIEN |

Nhân viên thường chỉ xem hóa đơn của mình; QUAN_TRI / QUAN_LY xem được của tất cả nhân viên. Sai mật khẩu 5 lần thì tài khoản bị khóa.

## 3. Cách bán hàng tại quầy
- **Thêm hàng**: bấm vào mặt hàng, hoặc quét / gõ **mã hàng** (SKU) ở ô tìm kiếm rồi **Enter** (mặt hàng được thêm thẳng). Gõ tên / thương hiệu thì danh sách tự lọc.
- **Bộ lọc** (nút ⚙ Bộ lọc): danh mục (gồm danh mục con), thương hiệu, loại, màu, khối lượng, chất liệu, chu vi tay cầm, khoảng giá, tình trạng tồn (còn / sắp hết / hết / tất cả), đang khuyến mãi, sắp xếp (tên, giá, mới nhất, tồn ít / nhiều). Các điều kiện kết hợp với nhau; từ khóa nhiều từ thì mỗi từ phải khớp tên, mã hàng hoặc thương hiệu.
- **Nhiều hóa đơn cùng lúc**: các tab ở bên phải (F4 tạo mới). Hóa đơn lưu trong trình duyệt nên F5 không mất.
- **Mã giảm giá**: nhập mã rồi "Áp dụng" (không giới hạn số lần dùng ở giai đoạn này).
- **Thanh toán**: chọn tiền mặt / chuyển khoản / quẹt thẻ; tiền mặt thì nhập tiền khách đưa (có nút gợi ý), hệ thống tính tiền thừa. Bấm **THANH TOÁN (F9)**.
- **In hóa đơn**: hộp thoại thành công có nút "In hóa đơn" (khổ 80 mm, đổi sang A5 được). Xem / in lại ở "Hóa đơn đã bán".
- Phím tắt: F2 tìm kiếm, F4 hóa đơn mới, F9 thanh toán, Esc đóng hộp thoại.

**An toàn dữ liệu**: giá và tồn kho do server tự kiểm tra lại lúc thanh toán (không tin dữ liệu từ trình duyệt). Trừ tồn kho, lưu hóa đơn, lưu dòng chi tiết và lịch sử nằm trong **một transaction**; thiếu hàng ở bất kỳ dòng nào thì hủy toàn bộ. Bấm thanh toán hai lần không tạo hai hóa đơn.

## 4. Kiến trúc MVC (không dùng Generic)
```
src/main/java/com/pickhub/
├── entity/        Model: 18 entity + BaseEntity (getter/setter viết tay)
├── repository/    INTERFACE tầng truy cập dữ liệu
│   ├── IBaseRepository          Base CRUD Interface: layTatCa/tim/them/capNhat/xoa/dem cho TỪNG entity
│   └── IChiTietSanPhamRepository, IHoaDonRepository, ... (kế thừa IBaseRepository, thêm truy vấn riêng)
├── repository/impl/
│   ├── BaseRepository           Base Repository tập trung toàn bộ CRUD của 18 entity (viết tường minh)
│   └── ChiTietSanPhamRepository, HoaDonRepository, ... (extends BaseRepository)
├── service/       INTERFACE tầng nghiệp vụ
│   ├── IBaseService             Base CRUD Interface của Service
│   └── IBanHangTaiQuayService, ISanPhamService, IKhuyenMaiService, IPhieuGiamGiaService, IHoaDonService, IXacThucService
├── service/impl/
│   ├── BaseServiceImpl          Base Service Implement: chuyển tiếp CRUD xuống BaseRepository
│   └── BanHangTaiQuayServiceImpl, SanPhamServiceImpl, ... (extends BaseServiceImpl)
├── controller/    Servlet (Controller): DangNhap, DangXuat, Pos, PosSanPhamApi, PosPhieuApi, PosThanhToan, InHoaDon, HoaDonTaiQuay, Upload
├── filter/        EncodingFilter, PosFilter (bắt buộc đăng nhập cho /pos/*)
├── listener/      KhoiTaoListener
├── dto/           Đối tượng chuyển dữ liệu (bộ lọc, trang kết quả, dòng bán, ...)
└── util/          HibernateUtil, JsonUtil, MatKhauUtil, DinhDangUtil, ...
src/main/webapp/
├── jsp/           View: dang-nhap.jsp, pos/ban-hang.jsp, pos/in-hoa-don.jsp, pos/hoa-don.jsp, loi/
└── assets/        pos.css, pos.js, no-image.png
```
Quy ước: không khai báo lớp / phương thức generic (`<T>`), mỗi entity có hàm riêng (`themSanPham`, `capNhatHoaDon`, `xoaPhieuGiamGia`...). Đọc `BaseRepository` là thấy hết các hàm CRUD của hệ thống. Ghi chú nghiệp vụ quan trọng bằng `// note code nhu nay nhe ban: ...`.

## 5. Thay đổi cơ sở dữ liệu (đã giải thích trong `sql/02_Migration_POS.sql`)
- `HoaDon` thêm: `kenhBan` (`ONLINE` mặc định / `TAI_QUAY`), `phuongThucThanhToan`, `tienKhachDua`, `tienThua` + ràng buộc CHECK + chỉ mục.
- Thêm khách hệ thống `khachle@pickhub.local` (bị khóa, không đăng nhập được) vì `HoaDon.maKhachHang` bắt buộc có.
- Hóa đơn bán tại quầy lưu `trangThai = HOAN_THANH`; thu ngân lưu ở `maNhanVienXacNhan`; các cột địa chỉ giao hàng (bắt buộc theo CHECK của bảng) ghi "Tại quầy".

## 6. Ảnh sản phẩm
Ảnh nằm ngoài webapp tại `~/pickhub-uploads` (đổi bằng `-Dpickhub.uploads=D:/anh` trong VM options của Tomcat), phục vụ qua `/uploads/...`. Ảnh mẫu trong dữ liệu chưa có file thật nên hiện ảnh `no-image.png`; chép ảnh vào thư mục trên đúng đường dẫn (vd `~/pickhub-uploads/products/...`) là hiện.

## 7. Kiểm tra gợi ý
| Tình huống | Kết quả mong đợi |
|---|---|
| Thêm 2 mặt hàng, trả tiền mặt đủ tiền | Hóa đơn HOAN_THANH, ton kho giảm đúng, in được hóa đơn |
| Khách đưa thiếu tiền | Báo "Tiền khách đưa chưa đủ", không tạo hóa đơn |
| Số lượng vượt tồn | Báo chỉ còn n cái |
| 2 máy cùng mua mặt hàng chỉ còn 1 | 1 hóa đơn thành công, hóa đơn kia báo thiếu hàng, tồn không âm |
| Nhập mã giảm giá hết hạn / chưa tới hạn / dưới giá trị tối thiểu | Báo lỗi tương ứng |
| Mã giảm giá đã dùng nhiều lần | Vẫn áp dụng (giới hạn dùng đã tắt) |
| Bấm Thanh toán 2 lần liên tiếp | Chỉ 1 hóa đơn |
| Lọc nhiều điều kiện (danh mục + màu + khoảng giá + còn hàng) | Danh sách và tổng số khớp; sang trang vẫn giữ bộ lọc |
| Nhân viên thường mở hóa đơn người khác | Không thấy (chỉ quản lý có tùy chọn "Tất cả nhân viên") |

## 8. Lưu ý trung thực
Mã nguồn đã được kiểm tra biên dịch bằng `javac` với bản giả lập thư viện, **chưa chạy thử trên Tomcat + SQL Server thật** (môi trường tạo project không có). Nếu gặp lỗi Hibernate / EL khi chạy lần đầu, gửi thông báo lỗi để chỉnh tiếp.

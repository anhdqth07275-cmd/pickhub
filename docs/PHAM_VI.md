# Phạm vi giai đoạn này: CHỈ bán hàng tại quầy (POS)

## Đang hoạt động
| Chức năng | Class chính |
|---|---|
| Đăng nhập / đăng xuất nhân viên (khóa sau 5 lần sai) | `DangNhapServlet`, `XacThucServiceImpl` |
| Màn hình bán hàng: chọn mặt hàng, nhiều hóa đơn cùng lúc, thanh toán, tiền thừa | `PosServlet`, `PosSanPhamApiServlet`, `PosThanhToanServlet`, `BanHangTaiQuayServiceImpl`, `pos.js` |
| Bộ lọc sản phẩm nâng cao (từ khóa, danh mục, thương hiệu, loại, màu, khối lượng, chất liệu, chu vi, giá, tồn kho, khuyến mãi, sắp xếp, phân trang) | `ChiTietSanPhamRepository.timKiemBienThe`, `SanPhamServiceImpl` |
| Giá theo khuyến mãi đang chạy | `KhuyenMaiServiceImpl` |
| Mã giảm giá (không kiểm tra giới hạn dùng) | `PhieuGiamGiaServiceImpl` |
| In hóa đơn (80mm / A5), xem và in lại | `InHoaDonServlet`, `in-hoa-don.jsp`, `HoaDonTaiQuayServlet` |
| Tổng kết ca bán | `HoaDonServiceImpl.tongKet` |
| Phục vụ ảnh sản phẩm | `UploadServlet` |

## Đã ĐÓNG (tạm ngưng, sẽ làm ở giai đoạn sau)
Các Servlet / Service / JSP sau **không có trong project này** (Entity và Repository nền tảng vẫn còn để mở lại):

- Quản lý tài khoản khách hàng: đăng ký, đăng nhập khách, quên mật khẩu, quản lý khách (admin).
- Bán hàng online: cửa hàng cho khách, chi tiết sản phẩm cho khách, giỏ hàng, đặt hàng, đơn hàng của tôi.
- Quản trị: CRUD sản phẩm / biến thể / hình ảnh / tồn kho / danh mục / thương hiệu / thuộc tính, khuyến mãi, phiếu giảm giá, nhân viên, xử lý và hủy đơn online, thống kê.

Muốn mở lại một chức năng: Entity + `BaseRepository` + `BaseServiceImpl` đã có đủ hàm CRUD cho cả 18 bảng, chỉ cần viết thêm Service chuyên biệt, Servlet và JSP.

## Tạm TẮT trong nghiệp vụ giảm giá
- Kiểm tra giới hạn số lần dùng (`gioiHanSuDung` / `soLanDaSuDung`) và giới hạn mỗi người (`gioiHanMoiNguoi`) của phiếu giảm giá: đã **comment** trong `PhieuGiamGiaServiceImpl.tinhGiam` (có hướng dẫn bật lại).
- Không ghi nhận lượt dùng phiếu (không tăng `soLanDaSuDung`, không thêm `SuDungPhieuGiamGia`).
- Không kiểm tra / cộng dồn giới hạn số lượng khuyến mãi (`soLuongGioiHan`, `soLuongDaBan`) của chương trình khuyến mãi theo sản phẩm.
- Vẫn kiểm tra: mã tồn tại, phiếu không tạm dừng, đúng khoảng thời gian áp dụng, giá trị đơn tối thiểu.

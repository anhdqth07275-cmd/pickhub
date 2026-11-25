# Thay đổi theo `prompt_y_u_c_u_ch_nh_s_a_h_th_ng.md`

> Mọi chỗ cần chú ý đều có comment `// note code nhu nay nhe ban: ...` (JSP: `<%-- note code ... --%>`).
> Không đổi `pom.xml`, `web.xml`, database. CSS chỉ **thêm** class mới ở cuối file.
> Cách dùng: giải nén đè lên thư mục gốc → IntelliJ: Maven Reload → Rebuild → chạy lại Tomcat → **Ctrl+F5**.

| # | Yêu cầu | File chính |
|---|---------|-----------|
| 1 | Danh mục: đủ CRUD (thêm/sửa/xóa) | `DanhMucServlet`, `DanhMucService.xoa`, `DanhMucRepository.xoa`, `san-pham/danh-muc.jsp` |
| 2 | Khuyến mãi làm lại theo ảnh mẫu (popup, thanh tìm kiếm, checkbox) | `khuyen-mai/danh-sach.jsp`, `dong-san-pham.jspf`, `ket-qua-tim.jsp`, `chi-tiet.jsp`, `KhuyenMaiServlet`, `KhuyenMaiTimSanPhamServlet`, `KhuyenMaiService`, `DongKhuyenMaiDTO`, `assets/khuyen-mai.js` |
| 3 | POS: trừ tồn trực quan + gợi ý voucher khi gõ | `assets/pos.js` (`daGiu`, `conLai`, `capNhatTonTrenGiao`, `ganGoiYVoucher`), `ban-hang.jsp`, `pos.css` |
| 4 | POS: thẻ sản phẩm thoáng + popup chọn biến thể | `SanPhamBanDTO`, `TrangSanPhamBanDTO`, `ChiTietSanPhamRepository` (+3 hàm), `SanPhamServiceImpl.timKiemSanPham`, `PosSanPhamApiServlet` (`?nhom=1`), `pos.js`, `pos.css`, `ban-hang.jsp` |
| 5 | Mọi dropdown có thanh tìm kiếm | `assets/select-search.js` + `select-search.css`, gắn vào 4 layout (admin, POS, khách, frontend) |

## Cách hoạt động ngắn gọn
- **Dropdown (5)**: script bắt sự kiện của *mọi* `<select>`, nên không cần sửa từng ô. Gõ không dấu vẫn tìm được ("vot" → "Vợt"); khớp **đầu cụm từ** xếp trước; tối đa 100 dòng cho dữ liệu lớn. Tắt 1 ô: thêm `data-no-search`.
- **Khuyến mãi (2)**: "Thêm"/"Sửa" mở popup. Tích chọn sản phẩm → tự điền mức giảm mặc định, hiện giá sau giảm + tổng. Gõ ô tìm kiếm → lọc ngay + hỏi server thêm. Lưu 1 lần cả chương trình và sản phẩm.
- **POS (3, 4)**: lưới hiện theo *sản phẩm* (ảnh, tên, giá). Sản phẩm 1 phân loại → thêm thẳng; nhiều phân loại → popup chọn. Tồn hiển thị = tồn server − số lượng trong **tất cả hóa đơn chờ** (chỉ trên màn hình, không ghi DB).

## Lưu ý
- `khuyen-mai/form.jsp` cũ không còn dùng (popup thay thế); xóa cũng được.
- Bộ lọc theo sản phẩm ở POS được chép từ hàm cũ (để không sửa hàm đang chạy). Nếu sau này đổi bộ lọc, nhớ sửa cả `timKiemBienThe` và `taoDieuKienLoc`.
- Xóa danh mục / chương trình chỉ được khi chưa được dùng; nếu đang dùng sẽ báo lỗi và gợi ý "Ngừng/Tạm dừng".

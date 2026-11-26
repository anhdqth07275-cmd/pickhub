# Giao diện kính mờ xanh lá (sáng / tối) - ghi chú cho nhóm

## Thêm mới (không đụng logic)
| File | Việc |
|---|---|
| `assets/theme.css` | Biến màu sáng/tối (đổi tông xanh chỉ sửa `--ph-accent-*`), nút chuyển sáng/tối, thanh cuộn, keyframes |
| `assets/theme.js` | Công tắc sáng/tối (nhớ bằng localStorage), bọc nhãn menu admin để thu gọn mượt, đếm số dashboard, mờ nhòe khi chuyển trang |
| `assets/admin-glass.css` | Ghi đè style admin (`/wp-admin/*`) |
| `assets/store-glass.css` | Ghi đè style trang bán hàng (`trang-chu-khach`) và tài khoản (`trang-chu-frontend`) |
| `assets/pos-glass.css` | Ghi đè style POS |
| `assets/login.css`, `assets/login.js` | Giao diện + hiệu ứng 4 trang đăng nhập |
| `WEB-INF/theme-head.jspf` | Đoạn chèn vào `<head>`: đặt chế độ sáng/tối trước khi vẽ, favicon, nạp theme |
| `favicon.svg`, `favicon.ico`, `apple-touch-icon.png` | Biểu tượng tab: chữ PH nền xanh lá |

## Sửa file có sẵn (chỉ phần head hoặc style)
- Thêm 2 dòng (include `theme-head.jspf` + link CSS khu vực) vào: `trang-chu-backend/layout/header.jspf`, `trang-chu-khach/layout/header.jspf`, `trang-chu-frontend/layout.jspf`, `jsp/pos/ban-hang.jsp`, `jsp/pos/hoa-don.jsp`, `trang-chu-backend/error/{403,404,500}.jsp`.
- `jsp/pos/*.jsp`: 2 chỗ `style="..."` màu cố định đổi thành class (`pos-tag`, `chk-tat-ca`) để chạy được chế độ tối.
- `jsp/loi/{403,404,500}.jsp`: style inline đổi thành class `.ph-err`.
- `jsp/pos/in-hoa-don.jsp`: chỉ thêm favicon (hóa đơn in giấy giữ nguyên đen trắng).

## Viết lại giao diện (giữ nguyên name / action / method)
`trang-chu-backend/dang-nhap.jsp` (POST /wp-admin/dang-nhap), `jsp/dang-nhap.jsp` (POST /dang-nhap),
`trang-chu-khach/dang-nhap.jsp` (POST /dang-nhap, kèm hidden quayLai, them),
`trang-chu-frontend/dang-nhap.jsp` (POST /tai-khoan/dang-nhap). Ô nhập: `email`, `matKhau`.

## Lưu ý
- Công tắc sáng/tối được thêm bằng JS (không xóa/dời khối nào).
- Không có `theme-head.jspf` hoặc `theme.js` thì trang vẫn chạy, chỉ mất chế độ tối và hiệu ứng.

## Cập nhật lần 2
- **Menu admin tự thu gọn:** mặc định chỉ còn icon (rộng 70px). Rê chuột vào (hoặc Tab vào bằng bàn phím) thì mở ra 250px như cũ, đè lên nội dung nên trang không bị giật. Chuột ra ngoài thì tự thu lại. Nút thu gọn thủ công không còn (đã được thay bằng cách tự động). Chỉnh trong `assets/admin-glass.css` (khối "SIDEBAR TU THU GON") và hàm `ganNhanMenu` trong `assets/theme.js`.
- **POS, 3 nút thanh toán:** bỏ icon, chỉ còn chữ "Tiền mặt", "Chuyển khoản", "Quẹt thẻ" (`jsp/pos/ban-hang.jsp`, `data-pt` giữ nguyên).
- **POS, ảnh sản phẩm:** ô ảnh luôn là hình vuông 1:1, ảnh cao/ngang đều được co cho vừa, không còn làm giãn thẻ (`assets/pos-glass.css`, cuối file).
- **Ảnh trong `assets/products`:** 68 ảnh đã chuẩn hóa: ảnh lớn thu nhỏ còn tối đa 800x800, ảnh không vuông được thêm viền trắng cho vuông, ảnh nhỏ giữ nguyên (không phóng to để khỏi bị mờ). Tên file và đường dẫn không đổi.
- Ảnh tải lên MỚI qua trang admin chưa được tự co về 800x800 (cần sửa code Java phần upload nếu muốn).

<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- M3 (Phat): danh sach san pham. Servlet gui sang: ketQua (PhanTrang<SanPhamDongDTO>), loc, danhSachDanhMuc, danhSachThuongHieu, danhSachMauSac, danhSachKhoiLuong --%>
<form method="get" action="${pageContext.request.contextPath}/wp-admin/san-pham">
    <input name="tuKhoa" value="${loc.tuKhoa}" placeholder="Tên hoặc mã hàng (SKU)">
    <select name="maDanhMuc"><option value="">-- Danh mục --</option>
        <c:forEach var="d" items="${danhSachDanhMuc}"><option value="${d.maDanhMuc}" ${loc.maDanhMuc == d.maDanhMuc ? 'selected' : ''}>${d.tenDanhMuc}</option></c:forEach>
    </select>
    <select name="maThuongHieu"><option value="">-- Thương hiệu --</option>
        <c:forEach var="t" items="${danhSachThuongHieu}"><option value="${t.maThuongHieu}" ${loc.maThuongHieu == t.maThuongHieu ? 'selected' : ''}>${t.tenThuongHieu}</option></c:forEach>
    </select>
    <input name="giaTu" value="${loc.giaTu}" placeholder="Giá từ" size="10">
    <input name="giaDen" value="${loc.giaDen}" placeholder="Giá đến" size="10">
    <select name="tinhTrangTon">
        <option value="">-- Tồn kho --</option>
        <option value="HET" ${loc.tinhTrangTon == 'HET' ? 'selected' : ''}>Hết hàng</option>
        <option value="SAP_HET" ${loc.tinhTrangTon == 'SAP_HET' ? 'selected' : ''}>Sắp hết</option>
        <option value="CON" ${loc.tinhTrangTon == 'CON' ? 'selected' : ''}>Còn hàng</option>
    </select>
    <select name="sapXep">
        <option value="moi" ${loc.sapXep == 'moi' ? 'selected' : ''}>Mới nhất</option>
        <option value="ten" ${loc.sapXep == 'ten' ? 'selected' : ''}>Tên A-Z</option>
        <option value="gia_tang" ${loc.sapXep == 'gia_tang' ? 'selected' : ''}>Giá tăng dần</option>
        <option value="gia_giam" ${loc.sapXep == 'gia_giam' ? 'selected' : ''}>Giá giảm dần</option>
    </select>
    <button>Lọc</button>
    <a href="${pageContext.request.contextPath}/wp-admin/san-pham?hanhDong=them">+ Thêm sản phẩm</a>
</form>
<br>
<c:if test="${empty ketQua.danhSach}"><p>Không có sản phẩm nào phù hợp.</p></c:if>
<c:if test="${not empty ketQua.danhSach}">
    <table>
        <tr><th>Ảnh</th><th>Tên sản phẩm</th><th>Thương hiệu</th><th>Danh mục</th><th>Giá</th><th>Biến thể</th><th>Tồn</th><th>Hiển thị</th><th></th></tr>
        <c:forEach var="d" items="${ketQua.danhSach}">
            <tr>
                <td><img src="${d.anhChinh.startsWith('/') ? pageContext.request.contextPath.concat(d.anhChinh) : d.anhChinh}" width="48"></td>
                <td><a href="${pageContext.request.contextPath}/wp-admin/san-pham/chi-tiet?maSanPham=${d.sanPham.maSanPham}">${d.sanPham.tenSanPham}</a></td>
                <td>${d.sanPham.thuongHieu.tenThuongHieu}</td>
                <td>${d.sanPham.danhMuc.tenDanhMuc}</td>
                <td><c:choose><c:when test="${d.giaThapNhat == null}">Chưa có</c:when>
                    <c:otherwise><fmt:formatNumber value="${d.giaThapNhat}" pattern="#,###"/> - <fmt:formatNumber value="${d.giaCaoNhat}" pattern="#,###"/></c:otherwise></c:choose></td>
                <td>${d.soBienThe}</td>
                <td>${d.tongTon}</td>
                <td>${d.sanPham.dangHienThi ? 'Đang bán' : 'Đã ẩn'}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/wp-admin/san-pham?hanhDong=sua&id=${d.sanPham.maSanPham}">Sửa</a>
                    <form method="post" action="${pageContext.request.contextPath}/wp-admin/san-pham" style="display:inline" onsubmit="return confirm('Ngừng kinh doanh sản phẩm này và mọi biến thể?')">
                        <input type="hidden" name="hanhDong" value="ngungKinhDoanh"><input type="hidden" name="id" value="${d.sanPham.maSanPham}">
                        <button>Ngừng KD</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/wp-admin/san-pham" style="display:inline" onsubmit="return confirm('Xóa sản phẩm này?')">
                        <input type="hidden" name="hanhDong" value="xoa"><input type="hidden" name="id" value="${d.sanPham.maSanPham}">
                        <button>Xóa</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
    <p>Trang ${ketQua.trangHienTai} / ${ketQua.tongTrang} - ${ketQua.tongSo} sản phẩm</p>
</c:if>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

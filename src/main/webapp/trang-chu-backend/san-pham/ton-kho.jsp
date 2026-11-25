<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- TON KHO theo tung bien the (M3).
     TonKhoServlet gui sang: pt (PhanTrang<ChiTietSanPham>), duongDan, soSapHet.
     LOI CU: servlet gui "ketQua" con JSP doc "pt" nen bang luon "Khong co du lieu" -> da sua servlet gui dung ten "pt". --%>

<div class="mini-stats">
    <div class="box"><div><b>${pt.tongSo}</b><span>Biến thể khớp bộ lọc</span></div></div>
    <a class="box warn" href="${ctx}/wp-admin/ton-kho?canhBao=1" title="Chỉ hiện biến thể sắp hết hoặc hết hàng">
        <div><b>${soSapHet}</b><span>Sắp hết / hết hàng (tồn ≤ ngưỡng)</span></div>
    </a>
</div>

<form class="card filter-card" method="get" action="${ctx}/wp-admin/ton-kho">
    <div class="filter-grid">
        <div class="fld fld-wide">
            <label for="t-tuKhoa">Tìm kiếm</label>
            <input id="t-tuKhoa" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Mã hàng hoặc tên sản phẩm...">
        </div>
        <div class="fld">
            <label for="t-trangThai">Trạng thái bán</label>
            <select id="t-trangThai" name="trangThai">
                <option value="">Tất cả</option>
                <option value="DANG_BAN" ${param.trangThai == 'DANG_BAN' ? 'selected' : ''}>Đang bán</option>
                <option value="HET_HANG" ${param.trangThai == 'HET_HANG' ? 'selected' : ''}>Hết hàng</option>
                <option value="NGUNG_BAN" ${param.trangThai == 'NGUNG_BAN' ? 'selected' : ''}>Ngừng bán</option>
            </select>
        </div>
        <div class="fld">
            <label>Cảnh báo</label>
            <label class="switch-row" style="margin:0;font-weight:500">
                <input type="checkbox" name="canhBao" value="1" ${param.canhBao == '1' ? 'checked' : ''}>
                <div><b>Chỉ sắp hết / hết hàng</b></div>
            </label>
        </div>
        <div class="filter-actions" style="margin-left:0">
            <button class="btn" type="submit">Lọc</button>
            <a class="btn line" href="${ctx}/wp-admin/ton-kho">Xóa lọc</a>
        </div>
    </div>
</form>

<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr>
            <th>Mã hàng</th><th>Sản phẩm</th><th>Biến thể</th><th class="num">Giá gốc</th>
            <th class="num">Tồn</th><th class="num">Ngưỡng</th><th>Trạng thái</th><th>Điều chỉnh tồn kho</th>
        </tr>
        <c:forEach var="c" items="${pt.danhSach}">
            <tr>
                <td class="code">${c.maHang}</td>
                <td><a href="${ctx}/wp-admin/san-pham/chi-tiet?maSanPham=${c.sanPham.maSanPham}"><b><c:out value="${c.sanPham.tenSanPham}"/></b></a></td>
                <td class="muted">
                    <c:if test="${not empty c.mauSac}">${c.mauSac.tenMauSac}</c:if>
                    <c:if test="${not empty c.khoiLuong}"> · ${c.khoiLuong.khoiLuongGram} g</c:if>
                    <c:if test="${not empty c.chuViTayCam}"> · ${c.chuViTayCam.chuViInch}"</c:if>
                    <c:if test="${empty c.mauSac and empty c.khoiLuong and empty c.chuViTayCam}">—</c:if>
                </td>
                <td class="num">${ph:tien(c.giaGoc)}</td>
                <%-- note code nhu nay nhe ban: do = het hang (0), cam = sap het (ton <= nguong canh bao), den = binh thuong --%>
                <td class="num"><span class="qty ${c.soLuongTon == 0 ? 'zero' : (c.soLuongTon <= c.nguongCanhBaoTonKho ? 'low' : '')}">${c.soLuongTon}</span></td>
                <td class="num">${c.nguongCanhBaoTonKho}</td>
                <td><span class="badge ${c.trangThai}">${ph:nhan(c.trangThai)}</span></td>
                <td>
                    <div class="stock-forms">
                        <%-- nhap them: CONG vao ton hien tai --%>
                        <form method="post" action="${ctx}/wp-admin/ton-kho">
                            <input type="hidden" name="hanhDong" value="nhapThem"><input type="hidden" name="id" value="${c.maChiTietSanPham}">
                            <input type="number" name="soLuong" min="1" placeholder="+ SL" required>
                            <button class="btn sm lime" type="submit" title="Cộng thêm vào tồn hiện tại">Nhập thêm</button>
                        </form>
                        <%-- kiem ke: DAT LAI ton bang dung so nay (co the = 0) --%>
                        <form method="post" action="${ctx}/wp-admin/ton-kho" onsubmit="return confirm('Đặt lại tồn kho của ${c.maHang} bằng đúng số vừa nhập?')">
                            <input type="hidden" name="hanhDong" value="kiemKe"><input type="hidden" name="id" value="${c.maChiTietSanPham}">
                            <input type="number" name="soLuong" min="0" placeholder="= SL" required>
                            <button class="btn sm line" type="submit" title="Đặt lại tồn bằng số này (kiểm kê)">Kiểm kê</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="8" class="empty">Không có dữ liệu tồn kho phù hợp.</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

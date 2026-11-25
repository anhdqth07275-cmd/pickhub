<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- DANH SACH SAN PHAM (M3) - giao dien moi + bo loc chi tiet.
     Servlet SanPhamServlet gui sang: ketQua (PhanTrang<SanPhamDongDTO>), loc (SanPhamLocDTO),
     danhSachDanhMuc, danhSachThuongHieu, danhSachMauSac, danhSachKhoiLuong (2 danh sach sau la ThuocTinhDTO: ma, giaTri) --%>

<%-- note code nhu nay nhe ban: coLoc = co it nhat 1 dieu kien loc dang ap dung -> hien thanh "Dang loc" va nut "Xoa loc" --%>
<c:set var="coLoc" value="${not empty loc.tuKhoa or loc.maDanhMuc != null or loc.maThuongHieu != null or loc.maMauSac != null or loc.maKhoiLuong != null or loc.giaTu != null or loc.giaDen != null or not empty loc.tinhTrangTon or loc.dangHienThi != null}"/>

<div class="page-actions">
    <span class="muted">Tìm thấy <b>${ketQua.tongSo}</b> sản phẩm</span>
    <div class="btn-group">
        <a class="btn line" href="${ctx}/wp-admin/ton-kho">Xem tồn kho</a>
        <a class="btn lime" href="${ctx}/wp-admin/san-pham?hanhDong=them">+ Thêm sản phẩm</a>
    </div>
</div>

<%-- ===================== BO LOC ===================== --%>
<form class="card filter-card" method="get" action="${ctx}/wp-admin/san-pham">
    <div class="filter-grid">
        <div class="fld fld-wide">
            <label for="f-tuKhoa">Tìm kiếm</label>
            <input id="f-tuKhoa" type="text" name="tuKhoa" value="<c:out value='${loc.tuKhoa}'/>" placeholder="Tên sản phẩm hoặc mã hàng (SKU)...">
        </div>
        <div class="fld">
            <label for="f-danhMuc">Danh mục</label>
            <select id="f-danhMuc" name="maDanhMuc">
                <option value="">Tất cả danh mục</option>
                <c:forEach var="d" items="${danhSachDanhMuc}">
                    <option value="${d.maDanhMuc}" ${loc.maDanhMuc == d.maDanhMuc ? 'selected' : ''}><c:if test="${not empty d.danhMucCha}">${d.danhMucCha.tenDanhMuc} › </c:if>${d.tenDanhMuc}</option>
                </c:forEach>
            </select>
        </div>
        <div class="fld">
            <label for="f-thuongHieu">Thương hiệu</label>
            <select id="f-thuongHieu" name="maThuongHieu">
                <option value="">Tất cả thương hiệu</option>
                <c:forEach var="t" items="${danhSachThuongHieu}">
                    <option value="${t.maThuongHieu}" ${loc.maThuongHieu == t.maThuongHieu ? 'selected' : ''}>${t.tenThuongHieu}</option>
                </c:forEach>
            </select>
        </div>
        <%-- note code nhu nay nhe ban: khoang gia loc theo GIA GOC cua bien the; go "1.500.000" hay "1500000" deu duoc (DocSoUtil tu bo dau cham) --%>
        <div class="fld">
            <label for="f-giaTu">Giá từ (đ)</label>
            <input id="f-giaTu" type="text" inputmode="numeric" name="giaTu" value="<c:out value='${loc.giaTu}'/>" placeholder="VD: 1000000">
        </div>
        <div class="fld">
            <label for="f-giaDen">Giá đến (đ)</label>
            <input id="f-giaDen" type="text" inputmode="numeric" name="giaDen" value="<c:out value='${loc.giaDen}'/>" placeholder="VD: 5000000">
        </div>
        <div class="fld">
            <label for="f-sapXep">Sắp xếp</label>
            <select id="f-sapXep" name="sapXep">
                <option value="moi" ${loc.sapXep == 'moi' ? 'selected' : ''}>Mới nhất</option>
                <option value="ten" ${loc.sapXep == 'ten' ? 'selected' : ''}>Tên A → Z</option>
                <option value="gia_tang" ${loc.sapXep == 'gia_tang' ? 'selected' : ''}>Giá thấp → cao</option>
                <option value="gia_giam" ${loc.sapXep == 'gia_giam' ? 'selected' : ''}>Giá cao → thấp</option>
            </select>
        </div>
    </div>

    <div class="filter-row">
        <div class="fld">
            <label>Tình trạng tồn kho</label>
            <div class="chip-group">
                <label><input type="radio" name="tinhTrangTon" value="" ${empty loc.tinhTrangTon ? 'checked' : ''}><span>Tất cả</span></label>
                <label><input type="radio" name="tinhTrangTon" value="CON" ${loc.tinhTrangTon == 'CON' ? 'checked' : ''}><span>Còn hàng</span></label>
                <label><input type="radio" name="tinhTrangTon" value="SAP_HET" ${loc.tinhTrangTon == 'SAP_HET' ? 'checked' : ''}><span>Sắp hết</span></label>
                <label><input type="radio" name="tinhTrangTon" value="HET" ${loc.tinhTrangTon == 'HET' ? 'checked' : ''}><span>Hết hàng</span></label>
            </div>
            <p class="hint">Tính theo biến thể: sản phẩm có ít nhất 1 biến thể ở tình trạng đã chọn.</p>
        </div>
        <div class="fld">
            <label>Hiển thị trên web</label>
            <div class="chip-group">
                <label><input type="radio" name="dangHienThi" value="" ${loc.dangHienThi == null ? 'checked' : ''}><span>Tất cả</span></label>
                <label><input type="radio" name="dangHienThi" value="1" ${loc.dangHienThi == true ? 'checked' : ''}><span>Đang bán</span></label>
                <label><input type="radio" name="dangHienThi" value="0" ${loc.dangHienThi == false ? 'checked' : ''}><span>Đã ẩn</span></label>
            </div>
        </div>
        <div class="filter-actions">
            <c:if test="${coLoc}"><a class="btn line" href="${ctx}/wp-admin/san-pham">Xóa bộ lọc</a></c:if>
            <button class="btn" type="submit">Áp dụng bộ lọc</button>
        </div>
    </div>

    <%-- loc nang cao: mo san neu dang loc theo mau / khoi luong --%>
    <details style="margin-top:12px" ${loc.maMauSac != null or loc.maKhoiLuong != null ? 'open' : ''}>
        <summary style="cursor:pointer;font-weight:600;color:var(--blue)">Lọc nâng cao (màu sắc, khối lượng)</summary>
        <div class="filter-grid" style="margin-top:12px">
            <div class="fld">
                <label for="f-mau">Màu sắc</label>
                <select id="f-mau" name="maMauSac">
                    <option value="">Tất cả màu</option>
                    <c:forEach var="m" items="${danhSachMauSac}"><option value="${m.ma}" ${loc.maMauSac == m.ma ? 'selected' : ''}>${m.giaTri}</option></c:forEach>
                </select>
            </div>
            <div class="fld">
                <label for="f-kl">Khối lượng</label>
                <select id="f-kl" name="maKhoiLuong">
                    <option value="">Tất cả khối lượng</option>
                    <c:forEach var="k" items="${danhSachKhoiLuong}"><option value="${k.ma}" ${loc.maKhoiLuong == k.ma ? 'selected' : ''}>${k.giaTri} g</option></c:forEach>
                </select>
            </div>
        </div>
    </details>
</form>

<%-- ===================== TOM TAT DIEU KIEN DANG LOC ===================== --%>
<c:if test="${coLoc}">
    <div class="active-filters">
        <span class="lbl">Đang lọc:</span>
        <c:if test="${not empty loc.tuKhoa}"><span class="tag">Từ khóa: <c:out value="${loc.tuKhoa}"/></span></c:if>
        <c:if test="${loc.maDanhMuc != null}">
            <c:forEach var="d" items="${danhSachDanhMuc}"><c:if test="${d.maDanhMuc == loc.maDanhMuc}"><span class="tag">Danh mục: ${d.tenDanhMuc}</span></c:if></c:forEach>
        </c:if>
        <c:if test="${loc.maThuongHieu != null}">
            <c:forEach var="t" items="${danhSachThuongHieu}"><c:if test="${t.maThuongHieu == loc.maThuongHieu}"><span class="tag">Thương hiệu: ${t.tenThuongHieu}</span></c:if></c:forEach>
        </c:if>
        <c:if test="${loc.giaTu != null}"><span class="tag">Giá từ ${ph:tien(loc.giaTu)}</span></c:if>
        <c:if test="${loc.giaDen != null}"><span class="tag">Giá đến ${ph:tien(loc.giaDen)}</span></c:if>
        <c:if test="${loc.tinhTrangTon == 'CON'}"><span class="tag">Còn hàng</span></c:if>
        <c:if test="${loc.tinhTrangTon == 'SAP_HET'}"><span class="tag">Sắp hết hàng</span></c:if>
        <c:if test="${loc.tinhTrangTon == 'HET'}"><span class="tag">Hết hàng</span></c:if>
        <c:if test="${loc.dangHienThi == true}"><span class="tag">Đang bán</span></c:if>
        <c:if test="${loc.dangHienThi == false}"><span class="tag">Đã ẩn</span></c:if>
    </div>
</c:if>

<%-- ===================== BANG SAN PHAM ===================== --%>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr>
            <th>Sản phẩm</th><th>Thương hiệu / Danh mục</th><th>Giá gốc</th>
            <th class="num">Biến thể</th><th class="num">Tồn</th><th>Hiển thị</th><th>Thao tác</th>
        </tr>
        <c:forEach var="d" items="${ketQua.danhSach}">
            <%-- note code nhu nay nhe ban: anh bat dau bang "/" la anh trong web -> them context path; anh http(s) thi giu nguyen --%>
            <c:choose>
                <c:when test="${fn:startsWith(d.anhChinh, '/')}"><c:set var="anhSrc" value="${ctx}${d.anhChinh}"/></c:when>
                <c:otherwise><c:set var="anhSrc" value="${d.anhChinh}"/></c:otherwise>
            </c:choose>
            <tr>
                <td>
                    <div class="sp-cell">
                        <img class="thumb" src="${anhSrc}" alt="" onerror="this.onerror=null;this.src='${ctx}/assets/no-image.png'">
                        <div>
                            <a class="ten" href="${ctx}/wp-admin/san-pham/chi-tiet?maSanPham=${d.sanPham.maSanPham}"><c:out value="${d.sanPham.tenSanPham}"/></a>
                            <div class="slug">${ph:nhan(d.sanPham.loaiSanPham)} · #${d.sanPham.maSanPham}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <b>${d.sanPham.thuongHieu.tenThuongHieu}</b>
                    <div class="muted">${d.sanPham.danhMuc.tenDanhMuc}</div>
                </td>
                <td class="nowrap">
                    <c:choose>
                        <c:when test="${d.giaThapNhat == null}"><span class="muted">Chưa có</span></c:when>
                        <c:when test="${d.giaThapNhat == d.giaCaoNhat}"><b>${ph:tien(d.giaThapNhat)}</b></c:when>
                        <c:otherwise><b>${ph:tien(d.giaThapNhat)}</b><div class="muted">đến ${ph:tien(d.giaCaoNhat)}</div></c:otherwise>
                    </c:choose>
                </td>
                <td class="num">${d.soBienThe}</td>
                <td class="num">
                    <c:choose>
                        <c:when test="${d.soBienThe == 0}"><span class="badge gray">Chưa có biến thể</span></c:when>
                        <c:when test="${d.tongTon == 0}"><span class="badge red">Hết hàng</span></c:when>
                        <c:otherwise><b>${d.tongTon}</b></c:otherwise>
                    </c:choose>
                </td>
                <td><span class="badge ${d.sanPham.dangHienThi ? 'green' : 'gray'}">${d.sanPham.dangHienThi ? 'Đang bán' : 'Đã ẩn'}</span></td>
                <td>
                    <div class="row-actions">
                        <a class="btn sm line" href="${ctx}/wp-admin/san-pham/chi-tiet?maSanPham=${d.sanPham.maSanPham}">Chi tiết</a>
                        <a class="btn sm line" href="${ctx}/wp-admin/san-pham?hanhDong=sua&amp;id=${d.sanPham.maSanPham}">Sửa</a>
                        <form method="post" action="${ctx}/wp-admin/san-pham">
                            <input type="hidden" name="hanhDong" value="doiHienThi"><input type="hidden" name="id" value="${d.sanPham.maSanPham}">
                            <button class="btn sm line" type="submit">${d.sanPham.dangHienThi ? 'Ẩn' : 'Hiện'}</button>
                        </form>
                        <form method="post" action="${ctx}/wp-admin/san-pham" onsubmit="return confirm('Ngừng kinh doanh sản phẩm này và mọi biến thể của nó?')">
                            <input type="hidden" name="hanhDong" value="ngungKinhDoanh"><input type="hidden" name="id" value="${d.sanPham.maSanPham}">
                            <button class="btn sm warn" type="submit">Ngừng KD</button>
                        </form>
                        <form method="post" action="${ctx}/wp-admin/san-pham" onsubmit="return confirm('Xóa sản phẩm này? (Chỉ xóa được khi chưa có biến thể)')">
                            <input type="hidden" name="hanhDong" value="xoa"><input type="hidden" name="id" value="${d.sanPham.maSanPham}">
                            <button class="btn sm danger" type="submit">Xóa</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty ketQua.danhSach}">
            <tr><td colspan="7" class="empty">Không có sản phẩm nào phù hợp với điều kiện lọc.</td></tr>
        </c:if>
    </table>
</div>

<%-- ===================== PHAN TRANG (giu nguyen bo loc khi sang trang) ===================== --%>
<%-- note code nhu nay nhe ban: phan-trang.jspf chung chi giu tuKhoa/trangThai nen o day tu ghep link rieng,
     de sang trang 2, 3... van giu DUNG cac dieu kien loc cua san pham --%>
<c:if test="${ketQua.tongTrang > 1}">
    <c:url var="goc" value="/wp-admin/san-pham">
        <c:param name="tuKhoa" value="${loc.tuKhoa}"/>
        <c:param name="maDanhMuc" value="${loc.maDanhMuc}"/>
        <c:param name="maThuongHieu" value="${loc.maThuongHieu}"/>
        <c:param name="maMauSac" value="${loc.maMauSac}"/>
        <c:param name="maKhoiLuong" value="${loc.maKhoiLuong}"/>
        <c:param name="giaTu" value="${loc.giaTu}"/>
        <c:param name="giaDen" value="${loc.giaDen}"/>
        <c:param name="tinhTrangTon" value="${loc.tinhTrangTon}"/>
        <c:param name="dangHienThi" value="${loc.dangHienThi == null ? '' : (loc.dangHienThi ? '1' : '0')}"/>
        <c:param name="sapXep" value="${loc.sapXep}"/>
    </c:url>
    <c:set var="cur" value="${ketQua.trangHienTai}"/>
    <c:set var="tong" value="${ketQua.tongTrang}"/>
    <c:set var="tu" value="${cur - 2 < 1 ? 1 : cur - 2}"/>
    <c:set var="den" value="${cur + 2 > tong ? tong : cur + 2}"/>
    <div class="pager">
        <span>Trang ${cur}/${tong} · ${ketQua.tongSo} sản phẩm</span>
        <c:if test="${cur > 1}"><a href="${goc}&amp;trang=${cur - 1}">‹ Trước</a></c:if>
        <c:if test="${tu > 1}"><a href="${goc}&amp;trang=1">1</a><c:if test="${tu > 2}"><span class="gap">…</span></c:if></c:if>
        <c:forEach begin="${tu}" end="${den}" var="i">
            <a class="${i == cur ? 'on' : ''}" href="${goc}&amp;trang=${i}">${i}</a>
        </c:forEach>
        <c:if test="${den < tong}"><c:if test="${den < tong - 1}"><span class="gap">…</span></c:if><a href="${goc}&amp;trang=${tong}">${tong}</a></c:if>
        <c:if test="${cur < tong}"><a href="${goc}&amp;trang=${cur + 1}">Sau ›</a></c:if>
    </div>
</c:if>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

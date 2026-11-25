<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- FORM THEM / SUA SAN PHAM (M3).
     Servlet gui sang: sanPham (null khi them moi), danhSachDanhMuc, danhSachThuongHieu,
     giuDuLieu (true khi luu bi loi -> hien lai dung nhung gi nguoi dung vua go thay vi gia tri trong DB) --%>

<%-- note code nhu nay nhe ban: moi o lay gia tri 1 trong 2 nguon: param (khi vua luu loi) hoac sanPham (khi mo form sua) --%>
<c:set var="vTen" value="${giuDuLieu ? param.tenSanPham : sanPham.tenSanPham}"/>
<c:set var="vLoai" value="${giuDuLieu ? param.loaiSanPham : sanPham.loaiSanPham}"/>
<c:set var="vDanhMuc" value="${giuDuLieu ? param.maDanhMuc : sanPham.danhMuc.maDanhMuc}"/>
<c:set var="vThuongHieu" value="${giuDuLieu ? param.maThuongHieu : sanPham.thuongHieu.maThuongHieu}"/>
<c:set var="vMoTaNgan" value="${giuDuLieu ? param.moTaNgan : sanPham.moTaNgan}"/>
<c:set var="vMoTa" value="${giuDuLieu ? param.moTa : sanPham.moTa}"/>
<c:choose>
    <c:when test="${giuDuLieu}"><c:set var="vHienThi" value="${param.dangHienThi == '1'}"/></c:when>
    <c:when test="${empty sanPham}"><c:set var="vHienThi" value="${true}"/></c:when>   <%-- them moi: mac dinh la hien thi --%>
    <c:otherwise><c:set var="vHienThi" value="${sanPham.dangHienThi}"/></c:otherwise>
</c:choose>

<a class="back" href="${ctx}/wp-admin/san-pham">← Danh sách sản phẩm</a>

<form method="post" action="${ctx}/wp-admin/san-pham">
    <input type="hidden" name="hanhDong" value="luu">
    <%-- id rong = them moi, co so = sua (SanPhamService.luu dua vao day de phan biet) --%>
    <input type="hidden" name="id" value="${sanPham.maSanPham}">

    <div class="form-layout">
        <%-- ============ COT TRAI: thong tin chinh ============ --%>
        <div class="form-stack">
            <div class="card form-card">
                <h3>Thông tin sản phẩm</h3>
                <div class="form-stack">
                    <div class="fld">
                        <label for="tenSanPham">Tên sản phẩm <span class="req">*</span></label>
                        <input id="tenSanPham" type="text" name="tenSanPham" maxlength="200" required value="<c:out value='${vTen}'/>" placeholder="VD: Vợt Selkirk Vanguard Power Air">
                        <p class="hint">Tối đa 200 ký tự. Đường dẫn (slug) được tự tạo từ tên và không được trùng với sản phẩm khác.</p>
                    </div>
                    <div class="form-2col">
                        <div class="fld">
                            <label for="loaiSanPham">Loại sản phẩm <span class="req">*</span></label>
                            <select id="loaiSanPham" name="loaiSanPham" required>
                                <option value="">-- Chọn loại --</option>
                                <c:forTokens items="VOT,BONG,PHU_KIEN,TUI" delims="," var="l">
                                    <option value="${l}" ${vLoai == l ? 'selected' : ''}>${ph:nhan(l)}</option>
                                </c:forTokens>
                            </select>
                        </div>
                        <div class="fld">
                            <label for="maThuongHieu">Thương hiệu <span class="req">*</span></label>
                            <select id="maThuongHieu" name="maThuongHieu" required>
                                <option value="">-- Chọn thương hiệu --</option>
                                <c:forEach var="t" items="${danhSachThuongHieu}">
                                    <option value="${t.maThuongHieu}" ${vThuongHieu == t.maThuongHieu ? 'selected' : ''}>${t.tenThuongHieu}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="fld">
                        <label for="maDanhMuc">Danh mục <span class="req">*</span></label>
                        <select id="maDanhMuc" name="maDanhMuc" required>
                            <option value="">-- Chọn danh mục --</option>
                            <c:forEach var="d" items="${danhSachDanhMuc}">
                                <option value="${d.maDanhMuc}" ${vDanhMuc == d.maDanhMuc ? 'selected' : ''}><c:if test="${not empty d.danhMucCha}">${d.danhMucCha.tenDanhMuc} › </c:if>${d.tenDanhMuc}</option>
                            </c:forEach>
                        </select>
                        <p class="hint">Chỉ hiện các danh mục và thương hiệu đang hoạt động.</p>
                    </div>
                </div>
            </div>

            <div class="card form-card">
                <h3>Mô tả</h3>
                <div class="form-stack">
                    <div class="fld">
                        <label for="moTaNgan">Mô tả ngắn <small>(tối đa 500 ký tự)</small></label>
                        <textarea id="moTaNgan" name="moTaNgan" maxlength="500" rows="3" placeholder="1-2 câu giới thiệu hiện ở danh sách sản phẩm"><c:out value="${vMoTaNgan}"/></textarea>
                    </div>
                    <div class="fld">
                        <label for="moTa">Mô tả chi tiết</label>
                        <textarea id="moTa" name="moTa" rows="8" placeholder="Thông số, chất liệu, đối tượng phù hợp..."><c:out value="${vMoTa}"/></textarea>
                    </div>
                </div>
            </div>
        </div>

        <%-- ============ COT PHAI: hien thi + hanh dong ============ --%>
        <div class="form-stack">
            <div class="card form-card">
                <h3>Hiển thị</h3>
                <label class="switch-row">
                    <input type="checkbox" name="dangHienThi" value="1" ${vHienThi ? 'checked' : ''}>
                    <div><b>Hiển thị sản phẩm</b><span>Bỏ chọn để ẩn sản phẩm khỏi cửa hàng (không xóa dữ liệu).</span></div>
                </label>
                <div class="form-foot">
                    <button class="btn lime" type="submit">${empty sanPham ? 'Thêm sản phẩm' : 'Lưu thay đổi'}</button>
                    <a class="btn line" href="${ctx}/wp-admin/san-pham">Hủy</a>
                </div>
            </div>

            <div class="card form-card">
                <h3>Lưu ý</h3>
                <c:choose>
                    <c:when test="${empty sanPham}">
                        <p class="hint" style="margin:0">Sau khi thêm sản phẩm, vào trang <b>Chi tiết</b> để thêm các <b>biến thể</b> (màu, khối lượng, chất liệu, chu vi, giá, tồn kho) và <b>hình ảnh</b>.</p>
                    </c:when>
                    <c:otherwise>
                        <dl class="info-grid" style="grid-template-columns:1fr">
                            <div><dt>Đường dẫn hiện tại</dt><dd style="word-break:break-all"><c:out value="${sanPham.duongDan}"/></dd></div>
                            <div><dt>Ngày tạo</dt><dd>${ph:ngayGio(sanPham.ngayTao)}</dd></div>
                            <div><dt>Cập nhật lần cuối</dt><dd>${ph:ngayGio(sanPham.ngayCapNhat)}</dd></div>
                        </dl>
                        <p style="margin:14px 0 0"><a class="btn sm line" href="${ctx}/wp-admin/san-pham/chi-tiet?maSanPham=${sanPham.maSanPham}">Quản lý biến thể &amp; hình ảnh →</a></p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</form>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

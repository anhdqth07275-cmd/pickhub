<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- CHI TIET 1 SAN PHAM (M3): thong tin + BIEN THE + HINH ANH.
     Servlet SanPhamChiTietServlet gui sang: sanPham, danhSachBienThe, danhSachAnh, bienTheDangSua (null khi them moi),
     danhSachMauSac / danhSachKhoiLuong / danhSachChatLieu / danhSachChuVi (ThuocTinhDTO: ma, giaTri)
     LOI CU: JSP doc ${sp}, ${bienThe}, ${hinhAnh} trong khi servlet gui ten khac nen trang trong -> da doi cho khop servlet --%>

<a class="back" href="${ctx}/wp-admin/san-pham">← Danh sách sản phẩm</a>

<%-- ===================== THONG TIN SAN PHAM ===================== --%>
<div class="card">
    <div class="detail-head">
        <div>
            <h3><c:out value="${sanPham.tenSanPham}"/></h3>
            <span class="badge ${sanPham.dangHienThi ? 'green' : 'gray'}">${sanPham.dangHienThi ? 'Đang hiển thị' : 'Đang ẩn'}</span>
            <span class="badge blue">${ph:nhan(sanPham.loaiSanPham)}</span>
        </div>
        <div class="row-actions">
            <a class="btn sm lime" href="${ctx}/wp-admin/san-pham?hanhDong=sua&amp;id=${sanPham.maSanPham}">Sửa sản phẩm</a>
            <form method="post" action="${ctx}/wp-admin/san-pham">
                <input type="hidden" name="hanhDong" value="doiHienThi"><input type="hidden" name="id" value="${sanPham.maSanPham}">
                <button class="btn sm line" type="submit">${sanPham.dangHienThi ? 'Ẩn sản phẩm' : 'Hiện sản phẩm'}</button>
            </form>
            <form method="post" action="${ctx}/wp-admin/san-pham" onsubmit="return confirm('Xóa sản phẩm này? (Chỉ xóa được khi chưa có biến thể)')">
                <input type="hidden" name="hanhDong" value="xoa"><input type="hidden" name="id" value="${sanPham.maSanPham}">
                <button class="btn sm danger" type="submit">Xóa</button>
            </form>
        </div>
    </div>
    <dl class="info-grid">
        <div><dt>Danh mục</dt><dd>${sanPham.danhMuc.tenDanhMuc}</dd></div>
        <div><dt>Thương hiệu</dt><dd>${sanPham.thuongHieu.tenThuongHieu}</dd></div>
        <div><dt>Đường dẫn (slug)</dt><dd style="word-break:break-all"><c:out value="${sanPham.duongDan}"/></dd></div>
        <div><dt>Ngày tạo</dt><dd>${ph:ngayGio(sanPham.ngayTao)}</dd></div>
        <div><dt>Cập nhật lần cuối</dt><dd>${ph:ngayGio(sanPham.ngayCapNhat)}</dd></div>
    </dl>
    <c:if test="${not empty sanPham.moTaNgan}"><p style="margin-bottom:6px"><b>Mô tả ngắn:</b> <c:out value="${sanPham.moTaNgan}"/></p></c:if>
    <c:if test="${not empty sanPham.moTa}"><p class="muted" style="margin:0;white-space:pre-line"><c:out value="${sanPham.moTa}"/></p></c:if>
</div>

<%-- ===================== BIEN THE ===================== --%>
<div class="section-title">Biến thể <span class="muted" style="font-weight:500;font-size:14px">(màu × khối lượng × chất liệu × chu vi tay cầm)</span></div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã hàng</th><th>Màu</th><th>Khối lượng</th><th>Chất liệu</th><th>Chu vi</th><th class="num">Giá gốc</th><th class="num">Tồn</th><th>Trạng thái</th><th>Thao tác</th></tr>
        <c:forEach items="${danhSachBienThe}" var="b">
            <tr>
                <td class="code">${b.maHang}</td>
                <td>${empty b.mauSac ? '—' : b.mauSac.tenMauSac}</td>
                <td>${empty b.khoiLuong ? '—' : b.khoiLuong.khoiLuongGram}${empty b.khoiLuong ? '' : ' g'}</td>
                <td>${empty b.chatLieu ? '—' : b.chatLieu.tenChatLieu}</td>
                <td>${empty b.chuViTayCam ? '—' : b.chuViTayCam.chuViInch}${empty b.chuViTayCam ? '' : '"'}</td>
                <td class="num">${ph:tien(b.giaGoc)}</td>
                <td class="num"><span class="qty ${b.soLuongTon == 0 ? 'zero' : (b.soLuongTon <= b.nguongCanhBaoTonKho ? 'low' : '')}">${b.soLuongTon}</span></td>
                <td><span class="badge ${b.trangThai}">${ph:nhan(b.trangThai)}</span></td>
                <td>
                    <div class="row-actions">
                        <a class="btn sm line" href="${ctx}/wp-admin/san-pham/chi-tiet?maSanPham=${sanPham.maSanPham}&amp;suaBienThe=${b.maChiTietSanPham}#form-bien-the">Sửa</a>
                        <form method="post" action="${ctx}/wp-admin/san-pham/chi-tiet">
                            <input type="hidden" name="hanhDong" value="doiTrangThaiBan"><input type="hidden" name="maSanPham" value="${sanPham.maSanPham}"><input type="hidden" name="id" value="${b.maChiTietSanPham}">
                            <button class="btn sm warn" type="submit">${b.trangThai == 'NGUNG_BAN' ? 'Bán lại' : 'Ngừng bán'}</button>
                        </form>
                        <form method="post" action="${ctx}/wp-admin/san-pham/chi-tiet" onsubmit="return confirm('Xóa biến thể ${b.maHang}? (Không xóa được nếu đã có trong hóa đơn hoặc khuyến mãi)')">
                            <input type="hidden" name="hanhDong" value="xoaBienThe"><input type="hidden" name="maSanPham" value="${sanPham.maSanPham}"><input type="hidden" name="id" value="${b.maChiTietSanPham}">
                            <button class="btn sm danger" type="submit">Xóa</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty danhSachBienThe}"><tr><td colspan="9" class="empty">Chưa có biến thể. Hãy thêm biến thể ở form bên dưới.</td></tr></c:if>
    </table>
</div>

<%-- ---------- form them / sua bien the ---------- --%>
<div class="card form-card" id="form-bien-the" style="margin-top:16px">
    <h3>${empty bienTheDangSua ? 'Thêm biến thể mới' : 'Sửa biến thể'} <c:if test="${not empty bienTheDangSua}"><small>${bienTheDangSua.maHang}</small></c:if></h3>
    <form method="post" action="${ctx}/wp-admin/san-pham/chi-tiet">
        <input type="hidden" name="hanhDong" value="luuBienThe">
        <input type="hidden" name="maSanPham" value="${sanPham.maSanPham}">
        <%-- id rong = them moi; co so = sua bien the do --%>
        <input type="hidden" name="id" value="${bienTheDangSua.maChiTietSanPham}">
        <div class="form-stack">
            <div class="form-3col">
                <div class="fld">
                    <label for="b-maHang">Mã hàng (SKU) <span class="req">*</span></label>
                    <input id="b-maHang" type="text" name="maHang" maxlength="50" required style="text-transform:uppercase" value="<c:out value='${bienTheDangSua.maHang}'/>" placeholder="VD: SEL-VAN-BK-230G">
                </div>
                <div class="fld">
                    <label for="b-gia">Giá gốc (đ) <span class="req">*</span></label>
                    <input id="b-gia" type="text" inputmode="numeric" name="giaGoc" required value="<c:out value='${bienTheDangSua.giaGoc}'/>" placeholder="VD: 4890000">
                </div>
                <div class="fld">
                    <label for="b-day">Độ dày (mm)</label>
                    <input id="b-day" type="text" inputmode="decimal" name="doDayMm" value="<c:out value='${bienTheDangSua.doDayMm}'/>" placeholder="VD: 16">
                </div>
            </div>
            <div class="form-2col">
                <div class="fld">
                    <label for="b-mau">Màu sắc</label>
                    <select id="b-mau" name="maMauSac">
                        <option value="">-- Không có --</option>
                        <c:forEach var="m" items="${danhSachMauSac}"><option value="${m.ma}" ${bienTheDangSua.mauSac.maMauSac == m.ma ? 'selected' : ''}>${m.giaTri}</option></c:forEach>
                    </select>
                </div>
                <div class="fld">
                    <label for="b-kl">Khối lượng</label>
                    <select id="b-kl" name="maKhoiLuong">
                        <option value="">-- Không có --</option>
                        <c:forEach var="k" items="${danhSachKhoiLuong}"><option value="${k.ma}" ${bienTheDangSua.khoiLuong.maKhoiLuong == k.ma ? 'selected' : ''}>${k.giaTri} g</option></c:forEach>
                    </select>
                </div>
                <div class="fld">
                    <label for="b-cl">Chất liệu</label>
                    <select id="b-cl" name="maChatLieu">
                        <option value="">-- Không có --</option>
                        <c:forEach var="c" items="${danhSachChatLieu}"><option value="${c.ma}" ${bienTheDangSua.chatLieu.maChatLieu == c.ma ? 'selected' : ''}>${c.giaTri}</option></c:forEach>
                    </select>
                </div>
                <div class="fld">
                    <label for="b-cv">Chu vi tay cầm</label>
                    <select id="b-cv" name="maChuViTayCam">
                        <option value="">-- Không có --</option>
                        <c:forEach var="v" items="${danhSachChuVi}"><option value="${v.ma}" ${bienTheDangSua.chuViTayCam.maChuViTayCam == v.ma ? 'selected' : ''}>${v.giaTri} inch</option></c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-2col">
                <div class="fld">
                    <label for="b-ton">Số lượng tồn</label>
                    <input id="b-ton" type="number" name="soLuongTon" min="0" value="${empty bienTheDangSua ? 0 : bienTheDangSua.soLuongTon}">
                    <p class="hint">Tồn = 0 thì biến thể tự chuyển sang "Hết hàng".</p>
                </div>
                <div class="fld">
                    <label for="b-nguong">Ngưỡng cảnh báo tồn kho</label>
                    <input id="b-nguong" type="number" name="nguongCanhBaoTonKho" min="0" value="${empty bienTheDangSua ? 5 : bienTheDangSua.nguongCanhBaoTonKho}">
                    <p class="hint">Tồn ≤ ngưỡng này sẽ xuất hiện ở cảnh báo "sắp hết".</p>
                </div>
            </div>
        </div>
        <div class="form-foot">
            <button class="btn lime" type="submit">${empty bienTheDangSua ? 'Thêm biến thể' : 'Lưu biến thể'}</button>
            <c:if test="${not empty bienTheDangSua}"><a class="btn line" href="${ctx}/wp-admin/san-pham/chi-tiet?maSanPham=${sanPham.maSanPham}">Hủy sửa</a></c:if>
        </div>
    </form>
</div>

<%-- ===================== HINH ANH ===================== --%>
<div class="section-title">Hình ảnh</div>
<div class="card">
    <c:if test="${empty danhSachAnh}"><div class="empty" style="padding:14px 0">Chưa có hình ảnh. Thêm đường dẫn ảnh ở form bên dưới.</div></c:if>
    <div class="img-grid">
        <c:forEach items="${danhSachAnh}" var="h">
            <%-- note code nhu nay nhe ban: duong dan anh bat dau bang "/" la anh trong web -> them context path --%>
            <c:choose>
                <c:when test="${fn:startsWith(h.duongDanAnh, '/')}"><c:set var="anhSrc" value="${ctx}${h.duongDanAnh}"/></c:when>
                <c:otherwise><c:set var="anhSrc" value="${h.duongDanAnh}"/></c:otherwise>
            </c:choose>
            <div class="img-card">
                <img src="${anhSrc}" alt="" onerror="this.onerror=null;this.src='${ctx}/assets/no-image.png'">
                <div class="body">
                    <div>
                        <c:if test="${h.laAnhChinh}"><span class="badge green">Ảnh chính</span></c:if>
                        <span class="badge gray">${h.loaiTep}</span>
                        <c:choose>
                            <c:when test="${h.maChiTietSanPham == null}"><span class="badge blue">Ảnh chung</span></c:when>
                            <c:otherwise>
                                <span class="badge orange"><c:forEach items="${danhSachBienThe}" var="b"><c:if test="${b.maChiTietSanPham == h.maChiTietSanPham}">${b.maHang}</c:if></c:forEach></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="path"><c:out value="${h.duongDanAnh}"/></div>
                    <div class="muted">Thứ tự: ${h.thuTuSapXep}</div>
                    <div class="row-actions">
                        <c:if test="${!h.laAnhChinh and h.maChiTietSanPham == null}">
                            <form method="post" action="${ctx}/wp-admin/hinh-anh">
                                <input type="hidden" name="hanhDong" value="datAnhChinh"><input type="hidden" name="maSanPham" value="${sanPham.maSanPham}"><input type="hidden" name="id" value="${h.maHinhAnhSanPham}">
                                <button class="btn sm line" type="submit">Đặt làm ảnh chính</button>
                            </form>
                        </c:if>
                        <form method="post" action="${ctx}/wp-admin/hinh-anh" onsubmit="return confirm('Xóa hình ảnh này?')">
                            <input type="hidden" name="hanhDong" value="xoa"><input type="hidden" name="maSanPham" value="${sanPham.maSanPham}"><input type="hidden" name="id" value="${h.maHinhAnhSanPham}">
                            <button class="btn sm danger" type="submit">Xóa</button>
                        </form>
                    </div>
                    <details>
                        <summary>Sửa đường dẫn / thứ tự</summary>
                        <form method="post" action="${ctx}/wp-admin/hinh-anh">
                            <input type="hidden" name="hanhDong" value="sua"><input type="hidden" name="maSanPham" value="${sanPham.maSanPham}"><input type="hidden" name="id" value="${h.maHinhAnhSanPham}">
                            <input type="text" name="duongDanAnh" value="<c:out value='${h.duongDanAnh}'/>" maxlength="500" required>
                            <input type="number" name="thuTu" min="1" value="${h.thuTuSapXep}" placeholder="Thứ tự">
                            <button class="btn sm" type="submit">Lưu</button>
                        </form>
                    </details>
                </div>
            </div>
        </c:forEach>
    </div>

    <form method="post" action="${ctx}/wp-admin/hinh-anh" style="margin-top:18px;padding-top:16px;border-top:1px dashed var(--line)">
        <input type="hidden" name="hanhDong" value="them">
        <input type="hidden" name="maSanPham" value="${sanPham.maSanPham}">
        <div class="form-stack">
            <div class="fld">
                <label for="a-url">Đường dẫn ảnh <span class="req">*</span></label>
                <input id="a-url" type="text" name="duongDanAnh" maxlength="500" required placeholder="https://... hoặc /uploads/products/ten-anh.jpg (chỉ .jpg .jpeg .png .webp)">
            </div>
            <div class="form-2col">
                <div class="fld">
                    <label for="a-bt">Gắn cho</label>
                    <select id="a-bt" name="maChiTietSanPham">
                        <option value="">Ảnh chung của sản phẩm</option>
                        <c:forEach items="${danhSachBienThe}" var="b"><option value="${b.maChiTietSanPham}">Chỉ biến thể ${b.maHang}</option></c:forEach>
                    </select>
                </div>
                <label class="switch-row" style="align-self:end">
                    <input type="checkbox" name="laAnhChinh" value="1">
                    <div><b>Đặt làm ảnh chính</b><span>Chỉ áp dụng cho ảnh chung.</span></div>
                </label>
            </div>
        </div>
        <div class="form-foot"><button class="btn lime" type="submit">Thêm hình ảnh</button></div>
    </form>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

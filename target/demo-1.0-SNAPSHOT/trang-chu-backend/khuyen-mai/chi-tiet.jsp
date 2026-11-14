<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/khuyen-mai">← Chương trình khuyến mãi</a>
<div class="card">
    <h3><c:out value="${ct.tenChuongTrinh}"/> <span class="badge ${ct.trangThai}">${ph:nhan(ct.trangThai)}</span></h3>
    <p class="muted"><c:out value="${ct.moTa}"/></p>
    <p style="margin-bottom:0">${ph:ngayGio(ct.ngayBatDau)} → ${ph:ngayGio(ct.ngayKetThuc)}</p>
</div>

<%-- note code nhu nay nhe ban: chuong trinh KET_THUC thi chi xem, an form them / sua / go --%>
<c:set var="conSua" value="${ct.trangThai != 'KET_THUC'}"/>

<c:if test="${conSua}">
    <div class="section-title">${empty dongSua ? 'Thêm sản phẩm vào chương trình' : 'Sửa mức giảm'}</div>
    <div class="card">
        <form class="form-grid" method="post" action="${ctx}/wp-admin/khuyen-mai/chi-tiet">
            <input type="hidden" name="id" value="${ct.maChuongTrinhKhuyenMai}">
            <input type="hidden" name="hanhDong" value="${empty dongSua ? 'them' : 'sua'}">
            <c:choose>
                <c:when test="${empty dongSua}">
                    <div>
                        <label>Sản phẩm (biến thể) *</label>
                        <select name="maChiTietSanPham" required>
                            <option value="">-- Chọn sản phẩm --</option>
                            <c:forEach items="${dsBienThe}" var="b">
                                <option value="${b.maChiTietSanPham}"><c:out value="${b.maHang}"/> - <c:out value="${b.sanPham.tenSanPham}"/> (${ph:tien(b.giaGoc)})</option>
                            </c:forEach>
                        </select>
                    </div>
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="idDong" value="${dongSua.maChiTietChuongTrinhKhuyenMai}">
                    <div><label>Sản phẩm</label>
                        <b><c:out value="${dongSua.chiTietSanPham.maHang}"/> - <c:out value="${dongSua.chiTietSanPham.sanPham.tenSanPham}"/></b>
                        <span class="muted">(giá gốc ${ph:tien(dongSua.chiTietSanPham.giaGoc)})</span></div>
                </c:otherwise>
            </c:choose>
            <div>
                <label>Loại giảm *</label>
                <select name="loaiGiam">
                    <option value="PHAN_TRAM" ${dongSua.loaiGiam == 'PHAN_TRAM' ? 'selected' : ''}>Phần trăm (%)</option>
                    <option value="SO_TIEN" ${dongSua.loaiGiam == 'SO_TIEN' ? 'selected' : ''}>Số tiền (đ)</option>
                </select>
            </div>
            <div><label>Giá trị giảm * <span class="muted">(% trong khoảng 0-100, hoặc số tiền nhỏ hơn giá gốc)</span></label>
                <input type="number" name="giaTriGiam" step="any" min="0" value="${dongSua.giaTriGiam}" required></div>
            <div><label>Giới hạn số lượng <span class="muted">(để trống = không giới hạn)</span></label>
                <input type="number" name="soLuongGioiHan" min="1" value="${dongSua.soLuongGioiHan}"></div>
            <div>
                <button class="btn lime" type="submit">${empty dongSua ? 'Thêm vào chương trình' : 'Lưu mức giảm'}</button>
                <c:if test="${not empty dongSua}"><a class="btn line" href="${ctx}/wp-admin/khuyen-mai/chi-tiet?id=${ct.maChuongTrinhKhuyenMai}">Hủy</a></c:if>
            </div>
        </form>
    </div>
</c:if>

<div class="section-title">Chi tiết sản phẩm được giảm</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã hàng</th><th>Sản phẩm</th><th class="num">Giá gốc</th><th>Mức giảm</th><th class="num">Giá sau giảm</th><th class="num">Đã bán / giới hạn</th><th></th></tr>
        <c:forEach items="${dsChiTiet}" var="d">
            <tr>
                <td class="code">${d.chiTietSanPham.maHang}</td>
                <td><c:out value="${d.chiTietSanPham.sanPham.tenSanPham}"/></td>
                <td class="num">${ph:tien(d.chiTietSanPham.giaGoc)}</td>
                <td><b>${ph:giamGia(d.loaiGiam, d.giaTriGiam)}</b></td>
                <td class="num"><b>${ph:tien(giaSauGiam[d.maChiTietChuongTrinhKhuyenMai])}</b></td>
                <td class="num">${d.soLuongDaBan} / ${empty d.soLuongGioiHan ? '∞' : d.soLuongGioiHan}</td>
                <td class="nowrap">
                    <c:if test="${conSua}">
                        <a class="btn sm line" href="${ctx}/wp-admin/khuyen-mai/chi-tiet?id=${ct.maChuongTrinhKhuyenMai}&suaDong=${d.maChiTietChuongTrinhKhuyenMai}">Sửa</a>
                        <form class="inline" method="post" action="${ctx}/wp-admin/khuyen-mai/chi-tiet" onsubmit="return confirm('Gỡ sản phẩm này khỏi chương trình?');">
                            <input type="hidden" name="hanhDong" value="go">
                            <input type="hidden" name="id" value="${ct.maChuongTrinhKhuyenMai}">
                            <input type="hidden" name="idDong" value="${d.maChiTietChuongTrinhKhuyenMai}">
                            <button class="btn sm danger" type="submit">Gỡ</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty dsChiTiet}"><tr><td colspan="7" class="empty">Chưa có sản phẩm trong chương trình</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

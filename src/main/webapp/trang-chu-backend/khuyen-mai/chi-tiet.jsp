<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- CHI TIET 1 CHUONG TRINH KHUYEN MAI - CHI XEM.
     note code nhu nay nhe ban: viec them / sua / go san pham gio lam trong POPUP "Sua" o danh sach (tich chon + tim kiem),
     nen trang nay chi hien thong tin va bang san pham da giam. --%>
<a class="back" href="${ctx}/wp-admin/khuyen-mai">← Chương trình khuyến mãi</a>

<div class="card">
    <div class="detail-head">
        <div>
            <h3><c:out value="${ct.tenChuongTrinh}"/> <span class="badge ${ct.trangThai}">${ph:nhan(ct.trangThai)}</span></h3>
            <p class="muted" style="margin:0"><c:out value="${ct.moTa}"/></p>
        </div>
        <a class="btn lime" href="${ctx}/wp-admin/khuyen-mai?hanhDong=form&amp;id=${ct.maChuongTrinhKhuyenMai}">Sửa chương trình &amp; sản phẩm</a>
    </div>
    <p style="margin-bottom:0"><span class="muted">Thời gian:</span> ${ph:ngayGio(ct.ngayBatDau)} → ${ph:ngayGio(ct.ngayKetThuc)}</p>
</div>

<div class="section-title">Sản phẩm được giảm (${dsChiTiet.size()})</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã hàng</th><th>Sản phẩm</th><th class="num">Giá gốc</th><th>Mức giảm</th><th class="num">Giá sau giảm</th><th class="num">Đã bán / giới hạn</th></tr>
        <c:forEach items="${dsChiTiet}" var="d">
            <tr>
                <td class="code">${d.chiTietSanPham.maHang}</td>
                <td><c:out value="${d.chiTietSanPham.sanPham.tenSanPham}"/></td>
                <td class="num">${ph:tien(d.chiTietSanPham.giaGoc)}</td>
                <td><b>${ph:giamGia(d.loaiGiam, d.giaTriGiam)}</b></td>
                <td class="num"><b>${ph:tien(giaSauGiam[d.maChiTietChuongTrinhKhuyenMai])}</b></td>
                <td class="num">${d.soLuongDaBan} / ${empty d.soLuongGioiHan ? '∞' : d.soLuongGioiHan}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty dsChiTiet}"><tr><td colspan="6" class="empty">Chưa có sản phẩm trong chương trình</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

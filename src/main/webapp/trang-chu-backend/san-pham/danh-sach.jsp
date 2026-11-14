<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/san-pham">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm tên sản phẩm...">
    <select name="loai">
        <option value="">Tất cả loại</option>
        <c:forTokens items="VOT,BONG,PHU_KIEN,TUI" delims="," var="t">
            <option value="${t}" ${param.loai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <button class="btn" type="submit">Lọc</button>
    <%-- TODO: nut "Them san pham" tro toi form them san pham --%>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Sản phẩm</th><th>Danh mục</th><th>Thương hiệu</th><th>Loại</th><th>Hiển thị</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="s">
            <tr>
                <td><b>${s.tenSanPham}</b><br><span class="muted">${s.duongDan}</span></td>
                <td>${s.danhMuc.tenDanhMuc}</td>
                <td>${s.thuongHieu.tenThuongHieu}</td>
                <td>${ph:nhan(s.loaiSanPham)}</td>
                <td><span class="badge ${s.dangHienThi ? 'HOAT_DONG' : 'NGUNG'}">${s.dangHienThi ? 'Đang hiện' : 'Đang ẩn'}</span></td>
                <td class="nowrap">
                    <a class="btn sm line" href="${ctx}/wp-admin/san-pham/chi-tiet?id=${s.maSanPham}">Chi tiết</a>
                    <c:if test="${!laNhanVien}">
                        <form class="inline" method="post" action="${ctx}/wp-admin/san-pham">
                            <input type="hidden" name="hanhDong" value="an-hien"><input type="hidden" name="id" value="${s.maSanPham}">
                            <button class="btn sm" type="submit">${s.dangHienThi ? 'Ẩn' : 'Hiện'}</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="6" class="empty">Không có sản phẩm</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/thuong-hieu">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm tên thương hiệu...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <option value="HOAT_DONG" ${param.trangThai == 'HOAT_DONG' ? 'selected' : ''}>Hoạt động</option>
        <option value="NGUNG" ${param.trangThai == 'NGUNG' ? 'selected' : ''}>Ngừng</option>
    </select>
    <button class="btn" type="submit">Lọc</button>
    <c:if test="${!laNhanVien}"><a class="btn lime" href="${ctx}/wp-admin/thuong-hieu?hanhDong=form">+ Thêm thương hiệu</a></c:if>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Tên thương hiệu</th><th>Đường dẫn</th><th>Logo</th><th>Trạng thái</th><th>Ngày tạo</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="t">
            <tr>
                <td><b>${t.tenThuongHieu}</b></td>
                <td class="muted">${t.duongDan}</td>
                <td class="muted">${t.duongDanLogo}</td>
                <td><span class="badge ${t.trangThai}">${ph:nhan(t.trangThai)}</span></td>
                <td>${ph:ngay(t.ngayTao)}</td>
                <td class="nowrap">
                    <c:if test="${!laNhanVien}">
                        <a class="btn sm line" href="${ctx}/wp-admin/thuong-hieu?hanhDong=form&id=${t.maThuongHieu}">Sửa</a>
                        <form class="inline" method="post" action="${ctx}/wp-admin/thuong-hieu">
                            <input type="hidden" name="hanhDong" value="doi-trang-thai"><input type="hidden" name="id" value="${t.maThuongHieu}">
                            <button class="btn sm" type="submit">${t.trangThai == 'HOAT_DONG' ? 'Ngừng' : 'Kích hoạt'}</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="6" class="empty">Không có thương hiệu</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- note code nhu nay nhe ban: nut THEM danh muc -> mo form (DanhMucServlet?hanhDong=form) --%>
<c:if test="${!laNhanVien}">
    <div class="page-actions">
        <span class="muted">Có <b>${pt.tongSo}</b> danh mục</span>
        <a class="btn lime" href="${ctx}/wp-admin/danh-muc?hanhDong=form">+ Thêm danh mục</a>
    </div>
</c:if>
<form class="toolbar" method="get" action="${ctx}/wp-admin/danh-muc">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm tên danh mục...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <option value="HOAT_DONG" ${param.trangThai == 'HOAT_DONG' ? 'selected' : ''}>Hoạt động</option>
        <option value="NGUNG" ${param.trangThai == 'NGUNG' ? 'selected' : ''}>Ngừng</option>
    </select>
    <button class="btn" type="submit">Lọc</button>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Tên danh mục</th><th>Danh mục cha</th><th>Mô tả</th><th>Trạng thái</th><th>Thao tác</th></tr>
        <c:forEach items="${pt.danhSach}" var="d">
            <tr>
                <td><b>${empty d.danhMucCha ? '' : '↳ '}${d.tenDanhMuc}</b></td>
                <td>${empty d.danhMucCha ? '—' : d.danhMucCha.tenDanhMuc}</td>
                <td class="muted">${d.moTa}</td>
                <td><span class="badge ${d.trangThai}">${ph:nhan(d.trangThai)}</span></td>
                <td>
                    <c:if test="${!laNhanVien}">
                        <%-- SUA: mo lai form kem id --%>
                        <a class="btn sm line" href="${ctx}/wp-admin/danh-muc?hanhDong=form&amp;id=${d.maDanhMuc}">Sửa</a>
                        <form class="inline" method="post" action="${ctx}/wp-admin/danh-muc">
                            <input type="hidden" name="hanhDong" value="doi-trang-thai"><input type="hidden" name="id" value="${d.maDanhMuc}">
                            <button class="btn sm line" type="submit">${d.trangThai == 'HOAT_DONG' ? 'Ngừng' : 'Kích hoạt'}</button>
                        </form>
                        <%-- XOA: co hoi lai; chi xoa duoc danh muc trong (khong co con / san pham), nguoc lai se bao loi --%>
                        <form class="inline" method="post" action="${ctx}/wp-admin/danh-muc" onsubmit="return confirm('Xóa danh mục &quot;${fn:escapeXml(d.tenDanhMuc)}&quot;? Không thể hoàn tác.')">
                            <input type="hidden" name="hanhDong" value="xoa"><input type="hidden" name="id" value="${d.maDanhMuc}">
                            <button class="btn sm danger" type="submit">Xóa</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="5" class="empty">Chưa có danh mục nào.</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

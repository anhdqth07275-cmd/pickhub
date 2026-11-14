<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/danh-muc">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm tên danh mục...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <option value="HOAT_DONG" ${param.trangThai == 'HOAT_DONG' ? 'selected' : ''}>Hoạt động</option>
        <option value="NGUNG" ${param.trangThai == 'NGUNG' ? 'selected' : ''}>Ngừng</option>
    </select>
    <button class="btn" type="submit">Lọc</button>
    <c:if test="${!laNhanVien}"><a class="btn lime" href="${ctx}/wp-admin/danh-muc?hanhDong=form">+ Thêm danh mục</a></c:if>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Tên danh mục</th><th>Danh mục cha</th><th>Mô tả</th><th>Số sản phẩm</th><th>Trạng thái</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="d">
            <tr>
                <%-- note code nhu nay nhe ban: danh muc con thut vao + mui ten de nhin ra cay cha - con --%>
                <td><b>${empty d.danhMucCha ? '' : '&nbsp;&nbsp;&nbsp;↳ '}<c:out value="${d.tenDanhMuc}"/></b></td>
                <td>${empty d.danhMucCha ? '—' : d.danhMucCha.tenDanhMuc}</td>
                <td class="muted"><c:out value="${d.moTa}"/></td>
                <td>${soSanPham[d.maDanhMuc]}</td>
                <td><span class="badge ${d.trangThai}">${ph:nhan(d.trangThai)}</span></td>
                <td class="nowrap">
                    <c:if test="${!laNhanVien}">
                        <a class="btn sm line" href="${ctx}/wp-admin/danh-muc?hanhDong=form&id=${d.maDanhMuc}">Sửa</a>
                        <form class="inline" method="post" action="${ctx}/wp-admin/danh-muc">
                            <input type="hidden" name="hanhDong" value="doi-trang-thai"><input type="hidden" name="id" value="${d.maDanhMuc}">
                            <button class="btn sm" type="submit">${d.trangThai == 'HOAT_DONG' ? 'Ngừng' : 'Kích hoạt'}</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="6" class="empty">Không có danh mục</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

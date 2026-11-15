<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/khuyen-mai">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm tên chương trình...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <c:forTokens items="SAP_CHAY,DANG_CHAY,TAM_DUNG,KET_THUC" delims="," var="t">
            <option value="${t}" ${param.trangThai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <button class="btn" type="submit">Lọc</button>
    <a class="btn lime" href="${ctx}/wp-admin/khuyen-mai?hanhDong=form">+ Thêm chương trình</a>
</form>

<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Chương trình</th><th>Bắt đầu</th><th>Kết thúc</th><th>Trạng thái</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="c">
            <tr>
                <td>
                    <b><c:out value="${c.tenChuongTrinh}"/></b>
                    <c:if test="${not empty c.moTa}"><br><span class="muted"><c:out value="${c.moTa}"/></span></c:if>
                </td>
                <td class="nowrap">${ph:ngayGio(c.ngayBatDau)}</td>
                <td class="nowrap">${ph:ngayGio(c.ngayKetThuc)}</td>
                <td><span class="badge ${c.trangThai}">${ph:nhan(c.trangThai)}</span></td>
                <td class="nowrap">
                    <a class="btn sm line" href="${ctx}/wp-admin/khuyen-mai/chi-tiet?id=${c.maChuongTrinhKhuyenMai}">Sản phẩm giảm giá</a>
                    <a class="btn sm line" href="${ctx}/wp-admin/khuyen-mai?hanhDong=form&id=${c.maChuongTrinhKhuyenMai}">Sửa</a>
                    <c:if test="${c.trangThai != 'KET_THUC'}">
                        <form class="inline" method="post" action="${ctx}/wp-admin/khuyen-mai">
                            <input type="hidden" name="hanhDong" value="doi-trang-thai">
                            <input type="hidden" name="id" value="${c.maChuongTrinhKhuyenMai}">
                            <button class="btn sm" type="submit">${c.trangThai == 'TAM_DUNG' ? 'Chạy lại' : 'Tạm dừng'}</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="5" class="empty">Không có chương trình khuyến mãi</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>
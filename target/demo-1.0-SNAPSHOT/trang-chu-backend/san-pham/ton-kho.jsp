<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/ton-kho">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm mã hàng...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <c:forTokens items="DANG_BAN,NGUNG_BAN,HET_HANG" delims="," var="t">
            <option value="${t}" ${param.trangThai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <label><input type="checkbox" name="canhBao" value="1" ${param.canhBao == '1' ? 'checked' : ''}> Chỉ hiện sắp hết</label>
    <button class="btn" type="submit">Lọc</button>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã hàng</th><th>Sản phẩm</th><th class="num">Giá gốc</th><th class="num">Tồn kho</th><th class="num">Ngưỡng cảnh báo</th><th>Trạng thái</th></tr>
        <c:forEach items="${pt.danhSach}" var="c">
            <tr>
                <td class="code">${c.maHang}</td>
                <td>${c.sanPham.tenSanPham}</td>
                <td class="num">${ph:tien(c.giaGoc)}</td>
                <td class="num"><b style="color:${c.soLuongTon <= c.nguongCanhBaoTonKho ? 'var(--bad)' : 'inherit'}">${c.soLuongTon}</b></td>
                <td class="num muted">${c.nguongCanhBaoTonKho}</td>
                <td><span class="badge ${c.trangThai}">${ph:nhan(c.trangThai)}</span></td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="6" class="empty">Không có dữ liệu</td></tr></c:if>
    </table>
</div>
<%-- TODO: form dieu chinh ton kho (soLuongTon >= 0, ton = 0 thi tu chuyen HET_HANG) --%>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

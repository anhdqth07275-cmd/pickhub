<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<div class="toolbar">
    <c:forTokens items="mausac,chat-lieu,khoi-luong,chu-vi" delims="," var="k">
        <a class="btn sm ${loai == k ? '' : 'line'}" href="${ctx}/wp-admin/thuoc-tinh?loai=${k}">
            ${k == 'mausac' ? 'Màu sắc' : k == 'chat-lieu' ? 'Chất liệu' : k == 'khoi-luong' ? 'Khối lượng' : 'Chu vi tay cầm'}</a>
    </c:forTokens>
</div>
<c:if test="${!laNhanVien}">
    <form class="toolbar" method="post" action="${ctx}/wp-admin/thuoc-tinh">
        <input type="hidden" name="hanhDong" value="them"><input type="hidden" name="loai" value="${loai}">
        <input type="text" name="giaTri" placeholder="Thêm ${fn:toLowerCase(tenThuocTinh)} mới..." required>
        <button class="btn lime" type="submit">Thêm</button>
    </form>
</c:if>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã</th><th>${tenThuocTinh}</th><th>Trạng thái</th><th>Ngày tạo</th><th></th></tr>
        <c:forEach items="${dsThuocTinh}" var="t">
            <tr>
                <td class="muted">${t.ma}</td><td><b>${t.giaTri}</b></td>
                <td><span class="badge ${t.trangThai}">${ph:nhan(t.trangThai)}</span></td>
                <td>${ph:ngay(t.ngayTao)}</td>
                <td>
                    <c:if test="${!laNhanVien}">
                        <form class="inline" method="post" action="${ctx}/wp-admin/thuoc-tinh">
                            <input type="hidden" name="loai" value="${loai}"><input type="hidden" name="id" value="${t.ma}">
                            <button class="btn sm line" type="submit">${t.trangThai == 'HOAT_DONG' ? 'Ngừng' : 'Kích hoạt'}</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

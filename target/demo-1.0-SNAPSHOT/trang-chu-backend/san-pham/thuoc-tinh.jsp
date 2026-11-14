<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- 4 tab: mau sac / chat lieu / khoi luong / chu vi --%>
<div class="toolbar">
    <c:forTokens items="mausac,chat-lieu,khoi-luong,chu-vi" delims="," var="k">
        <a class="btn sm ${loai == k ? '' : 'line'}" href="${ctx}/wp-admin/thuoc-tinh?loai=${k}">
            ${k == 'mausac' ? 'Màu sắc' : k == 'chat-lieu' ? 'Chất liệu' : k == 'khoi-luong' ? 'Khối lượng' : 'Chu vi tay cầm'}</a>
    </c:forTokens>
</div>

<%-- tim kiem + loc trang thai --%>
<form class="toolbar" method="get" action="${ctx}/wp-admin/thuoc-tinh">
    <input type="hidden" name="loai" value="${loai}">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm ${fn:toLowerCase(tenThuocTinh)}...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <option value="HOAT_DONG" ${param.trangThai == 'HOAT_DONG' ? 'selected' : ''}>Hoạt động</option>
        <option value="NGUNG" ${param.trangThai == 'NGUNG' ? 'selected' : ''}>Ngừng</option>
    </select>
    <button class="btn" type="submit">Lọc</button>
</form>

<c:if test="${!laNhanVien}">
    <form class="toolbar" method="post" action="${ctx}/wp-admin/thuoc-tinh">
        <input type="hidden" name="hanhDong" value="them"><input type="hidden" name="loai" value="${loai}">
        <input type="text" name="giaTri" placeholder="Thêm ${fn:toLowerCase(tenThuocTinh)} mới...${loai == 'khoi-luong' ? ' (gram)' : loai == 'chu-vi' ? ' (inch, vd 4.125)' : ''}" required>
        <button class="btn lime" type="submit">Thêm</button>
    </form>
</c:if>

<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã</th><th>${tenThuocTinh}</th><th>Trạng thái</th><th>Ngày tạo</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="t">
            <tr>
                <td class="muted">${t.ma}</td>
                <td>
                    <c:choose>
                        <%-- note code nhu nay nhe ban: dong dang sua (suaId trung ma) thi hien o nhap, cac dong khac hien chu --%>
                        <c:when test="${suaId == t.ma && !laNhanVien}">
                            <form class="inline" method="post" action="${ctx}/wp-admin/thuoc-tinh">
                                <input type="hidden" name="hanhDong" value="sua"><input type="hidden" name="loai" value="${loai}"><input type="hidden" name="id" value="${t.ma}">
                                <input type="text" name="giaTri" value="<c:out value='${t.giaTriGoc}'/>" required>
                                <button class="btn sm lime" type="submit">Lưu</button>
                                <a class="btn sm line" href="${ctx}/wp-admin/thuoc-tinh?loai=${loai}">Hủy</a>
                            </form>
                        </c:when>
                        <c:otherwise><b><c:out value="${t.giaTri}"/></b></c:otherwise>
                    </c:choose>
                </td>
                <td><span class="badge ${t.trangThai}">${ph:nhan(t.trangThai)}</span></td>
                <td>${ph:ngay(t.ngayTao)}</td>
                <td class="nowrap">
                    <c:if test="${!laNhanVien}">
                        <a class="btn sm line" href="${ctx}/wp-admin/thuoc-tinh?loai=${loai}&suaId=${t.ma}&trang=${pt.trangHienTai}">Sửa</a>
                        <form class="inline" method="post" action="${ctx}/wp-admin/thuoc-tinh">
                            <input type="hidden" name="hanhDong" value="doi-trang-thai"><input type="hidden" name="loai" value="${loai}"><input type="hidden" name="id" value="${t.ma}">
                            <button class="btn sm" type="submit">${t.trangThai == 'HOAT_DONG' ? 'Ngừng' : 'Kích hoạt'}</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="5" class="empty">Không có dữ liệu</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

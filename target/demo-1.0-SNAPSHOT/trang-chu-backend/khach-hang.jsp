<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/khach-hang">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm tên, email, SĐT...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <c:forTokens items="HOAT_DONG,CHUA_XAC_THUC,KHOA" delims="," var="t">
            <option value="${t}" ${param.trangThai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <button class="btn" type="submit">Lọc</button>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Họ tên</th><th>Email</th><th>SĐT</th><th>Trạng thái</th><th>Đăng nhập gần nhất</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="u">
            <tr>
                <td><b>${u.hoTen}</b></td>
                <td>${u.thuDienTu}</td>
                <td>${u.soDienThoai}</td>
                
                <td><span class="badge ${u.trangThai}">${ph:nhan(u.trangThai)}</span>
                    <c:if test="${u.trangThai == 'KHOA'}"><br><span class="muted" style="font-size:12px">${u.lyDoKhoa}</span></c:if></td>
                <td class="nowrap">${ph:ngayGio(u.ngayDangNhapCuoi)}</td>
                <td class="nowrap">
                    <%-- note code nhu nay nhe ban: khoa can nhap ly do (prompt), mo khoa thi bam la xong --%>
                    <c:if test="${u.trangThai == 'KHOA' && !laNhanVien}">
                        <form class="inline" method="post" action="${ctx}/wp-admin/khach-hang">
                            <input type="hidden" name="hanhDong" value="mo-khoa"><input type="hidden" name="id" value="${u.maKhachHang}">
                            <button class="btn sm lime" type="submit">Mở khóa</button>
                        </form>
                    </c:if>
                    <c:if test="${u.trangThai != 'KHOA' && !laNhanVien}">
                        <form class="inline" method="post" action="${ctx}/wp-admin/khach-hang" onsubmit="var l = prompt('Lý do khóa tài khoản:'); if (l === null) return false; this.lyDo.value = l; return true;">
                            <input type="hidden" name="hanhDong" value="khoa"><input type="hidden" name="id" value="${u.maKhachHang}"><input type="hidden" name="lyDo" value="">
                            <button class="btn sm danger" type="submit">Khóa</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="7" class="empty">Không có dữ liệu</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

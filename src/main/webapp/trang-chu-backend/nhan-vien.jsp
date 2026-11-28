<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>

<form class="toolbar" method="get" action="${ctx}/wp-admin/nhan-vien">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>"
           placeholder="Tìm tên, email, số điện thoại...">
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
        <thead>
        <tr>
            <th>Họ tên</th>
            <th>Email</th>
            <th>Số điện thoại</th>
            <th>Ngày sinh</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${pt.danhSach}" var="u">
            <tr>
                <td><b><c:out value="${u.hoTen}"/></b></td>
                <td><c:out value="${u.thuDienTu}"/></td>
                <td><c:out value="${u.soDienThoai}"/></td>
                <td>${u.ngaySinh != null ? u.ngaySinh : '—'}</td>
                <td>${ph:nhan(u.vaiTro)}</td>
                <td>
                    <span class="badge ${u.trangThai}">${ph:nhan(u.trangThai)}</span>
                    <c:if test="${u.trangThai == 'KHOA' and not empty u.lyDoKhoa}">
                        <br><span class="muted"><c:out value="${u.lyDoKhoa}"/></span>
                    </c:if>
                </td>
                <td class="nowrap">
                    <c:if test="${u.maNhanVien != sessionScope.taiKhoan.id}">
                        <a class="btn sm" href="${ctx}/wp-admin/nhan-vien?hanhDong=sua&id=${u.maNhanVien}">Chỉnh sửa</a>
                        <c:choose>
                            <c:when test="${u.trangThai == 'KHOA'}">
                                <form class="inline" method="post" action="${ctx}/wp-admin/nhan-vien"
                                      onsubmit="return confirm('Bạn có chắc muốn mở khóa tài khoản này?')">
                                    <input type="hidden" name="hanhDong" value="mo-khoa">
                                    <input type="hidden" name="id" value="${u.maNhanVien}">
                                    <button class="btn sm lime" type="submit">Mở khóa</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form class="inline" method="post" action="${ctx}/wp-admin/nhan-vien"
                                      onsubmit="var x=prompt('Nhập lý do khóa:');if(x===null||!x.trim())return false;this.lyDo.value=x;return true">
                                    <input type="hidden" name="hanhDong" value="khoa">
                                    <input type="hidden" name="id" value="${u.maNhanVien}">
                                    <input type="hidden" name="lyDo">
                                    <button class="btn sm danger" type="submit">Khóa</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    <c:if test="${u.maNhanVien == sessionScope.taiKhoan.id}">
                        <span class="muted">Tài khoản của bạn</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}">
            <tr><td colspan="7" class="empty">Không có nhân viên phù hợp</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

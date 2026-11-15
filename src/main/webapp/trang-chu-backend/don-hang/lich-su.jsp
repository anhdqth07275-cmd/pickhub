<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/don-hang">← Danh sách đơn hàng</a>
<form class="toolbar" method="get" action="${ctx}/wp-admin/lich-su-trang-thai">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Nhập mã tra cứu của đơn, vd HD2601140001">
    <select name="trangThai">
        <option value="">Tất cả trạng thái</option>
        <c:forTokens items="CHO_XAC_NHAN,DA_XAC_NHAN,DANG_GIAO,HOAN_THANH,DA_HUY,GIO_HANG" delims="," var="t">
            <option value="${t}" ${param.trangThai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <select name="nguoi">
        <option value="">Mọi người thực hiện</option>
        <option value="NHAN_VIEN" ${param.nguoi == 'NHAN_VIEN' ? 'selected' : ''}>Nhân viên</option>
        <option value="HE_THONG" ${param.nguoi == 'HE_THONG' ? 'selected' : ''}>Khách hàng / hệ thống</option>
    </select>
    <label class="muted">Từ ngày <input type="date" name="tuNgay" value="<c:out value='${param.tuNgay}'/>"></label>
    <label class="muted">Đến ngày <input type="date" name="denNgay" value="<c:out value='${param.denNgay}'/>"></label>
    <button class="btn" type="submit">Lọc</button>
</form>

<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Thời gian</th><th>Đơn hàng</th><th>Chuyển trạng thái</th><th>Người thực hiện</th><th>Ghi chú</th></tr>
        <c:forEach items="${pt.danhSach}" var="d">
            <tr>
                <td class="nowrap">${ph:ngayGio(d.ngayThayDoi)}</td>
                <td><a class="code" href="${ctx}/wp-admin/don-hang/chi-tiet?id=${d.maHoaDon}">
                    <c:out value="${empty d.maTraCuu ? '(giỏ hàng)' : d.maTraCuu}"/></a></td>
                <td class="nowrap">
                    <%-- note code nhu nay nhe ban: tuTrangThai rong = dong khoi tao (vd khach vua tao gio hang) --%>
                    <c:choose>
                        <c:when test="${empty d.tuTrangThai}"><span class="muted">(khởi tạo)</span></c:when>
                        <c:otherwise><span class="badge ${d.tuTrangThai}">${ph:nhan(d.tuTrangThai)}</span></c:otherwise>
                    </c:choose>
                    →
                    <span class="badge ${d.denTrangThai}">${ph:nhan(d.denTrangThai)}</span>
                </td>
                <td><c:out value="${empty d.tenNguoiThayDoi ? 'Khách hàng / hệ thống' : d.tenNguoiThayDoi}"/></td>
                <td><c:out value="${d.ghiChu}"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="5" class="empty">Không có dòng lịch sử phù hợp</td></tr></c:if>
    </table>
</div>

<%-- Thanh phan trang rieng cua trang nay: giu nguyen TOAN BO bo loc (ke ca ngay, nguoi thuc hien) khi chuyen trang --%>
<c:if test="${pt.tongTrang > 1}">
    <div class="pager">
        <span>Trang ${pt.trangHienTai}/${pt.tongTrang} · ${pt.tongSo} dòng</span>
        <c:forEach begin="1" end="${pt.tongTrang}" var="i">
            <c:url var="lienKet" value="/wp-admin/lich-su-trang-thai">
                <c:param name="trang" value="${i}"/>
                <c:param name="tuKhoa" value="${param.tuKhoa}"/>
                <c:param name="trangThai" value="${param.trangThai}"/>
                <c:param name="nguoi" value="${param.nguoi}"/>
                <c:param name="tuNgay" value="${param.tuNgay}"/>
                <c:param name="denNgay" value="${param.denNgay}"/>
            </c:url>
            <a class="${i == pt.trangHienTai ? 'on' : ''}" href="${lienKet}">${i}</a>
        </c:forEach>
    </div>
</c:if>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

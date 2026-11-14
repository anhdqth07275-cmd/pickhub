<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<form class="toolbar" method="get" action="${ctx}/wp-admin/phieu-giam-gia">
    <input class="grow" type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm mã hoặc tên phiếu...">
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <c:forTokens items="SAP_CHAY,DANG_CHAY,TAM_DUNG,HET_HAN" delims="," var="t">
            <option value="${t}" ${param.trangThai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <button class="btn" type="submit">Lọc</button>
    <a class="btn lime" href="${ctx}/wp-admin/phieu-giam-gia?hanhDong=form">+ Thêm phiếu</a>
    <a class="btn line" href="${ctx}/wp-admin/phieu-giam-gia/su-dung">Lượt sử dụng</a>
</form>

<%-- note code nhu nay nhe ban: o "Thu phieu" goi dung ham PhieuGiamGiaService.kiemTraVaTinhGiam ma gio hang (M5) va dat hang (M6) se goi --%>
<div class="card" style="margin-bottom:16px">
    <h3>Thử phiếu giảm giá</h3>
    <form class="toolbar" method="get" action="${ctx}/wp-admin/phieu-giam-gia" style="margin-bottom:0">
        <input type="text" name="thuMa" value="<c:out value='${param.thuMa}'/>" placeholder="Mã phiếu (vd DOUBLE11)">
        <input type="number" name="thuTamTinh" min="0" value="<c:out value='${param.thuTamTinh}'/>" placeholder="Tạm tính của đơn (đ)">
        <input type="number" name="thuKhach" min="1" value="<c:out value='${param.thuKhach}'/>" placeholder="Mã khách hàng (tùy chọn)">
        <button class="btn" type="submit">Kiểm tra</button>
    </form>
    <c:if test="${not empty ketQuaThu}">
        <div class="alert ${ketQuaThu.hopLe ? 'ok' : 'loi'}" style="margin:12px 0 0">
            <c:choose>
                <c:when test="${ketQuaThu.hopLe}">Hợp lệ - số tiền được giảm: ${ph:tien(ketQuaThu.soTienGiam)}</c:when>
                <c:otherwise><c:out value="${ketQuaThu.lyDoTuChoi}"/></c:otherwise>
            </c:choose>
        </div>
    </c:if>
</div>

<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã</th><th>Tên phiếu</th><th>Giảm</th><th class="num">Đơn tối thiểu</th><th class="num">Đã dùng / giới hạn</th><th>Thời gian</th><th>Trạng thái</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="p">
            <tr>
                <td class="code">${p.maGiamGia}</td>
                <td><c:out value="${p.tenPhieu}"/></td>
                <td><b>${ph:giamGia(p.loaiGiam, p.giaTriGiam)}</b><c:if test="${not empty p.giamToiDa}"><br><span class="muted">tối đa ${ph:tien(p.giamToiDa)}</span></c:if></td>
                <td class="num">${ph:tien(p.giaTriDonToiThieu)}</td>
                <td class="num">${p.soLanDaSuDung} / ${empty p.gioiHanSuDung ? '∞' : p.gioiHanSuDung}</td>
                <td class="nowrap">${ph:ngay(p.ngayBatDau)} → ${ph:ngay(p.ngayKetThuc)}</td>
                <td><span class="badge ${p.trangThai}">${ph:nhan(p.trangThai)}</span></td>
                <td class="nowrap">
                    <a class="btn sm line" href="${ctx}/wp-admin/phieu-giam-gia?hanhDong=form&id=${p.maPhieuGiamGia}">Sửa</a>
                    <%-- phieu HET_HAN thi khong con nut tam dung / chay lai --%>
                    <c:if test="${p.trangThai != 'HET_HAN'}">
                        <form class="inline" method="post" action="${ctx}/wp-admin/phieu-giam-gia">
                            <input type="hidden" name="hanhDong" value="doi-trang-thai">
                            <input type="hidden" name="id" value="${p.maPhieuGiamGia}">
                            <button class="btn sm" type="submit">${p.trangThai == 'TAM_DUNG' ? 'Chạy lại' : 'Tạm dừng'}</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="8" class="empty">Không có phiếu giảm giá</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

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
    <a class="btn line" href="${ctx}/wp-admin/phieu-giam-gia/su-dung">Lượt sử dụng</a>
</form>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã</th><th>Tên phiếu</th><th>Giảm</th><th class="num">Đơn tối thiểu</th><th class="num">Đã dùng / giới hạn</th><th>Thời gian</th><th>Trạng thái</th><th></th></tr>
        <c:forEach items="${pt.danhSach}" var="p">
            <tr>
                <td class="code">${p.maGiamGia}</td>
                <td>${p.tenPhieu}</td>
                <td><b>${ph:giamGia(p.loaiGiam, p.giaTriGiam)}</b><c:if test="${not empty p.giamToiDa}"><br><span class="muted">tối đa ${ph:tien(p.giamToiDa)}</span></c:if></td>
                <td class="num">${ph:tien(p.giaTriDonToiThieu)}</td>
                <td class="num">${p.soLanDaSuDung} / ${empty p.gioiHanSuDung ? '∞' : p.gioiHanSuDung}</td>
                <td class="nowrap">${ph:ngay(p.ngayBatDau)} → ${ph:ngay(p.ngayKetThuc)}</td>
                <td><span class="badge ${p.trangThai}">${ph:nhan(p.trangThai)}</span></td>
                <td>
                    <c:if test="${p.trangThai == 'DANG_CHAY' || p.trangThai == 'TAM_DUNG'}">
                        <form class="inline" method="post" action="${ctx}/wp-admin/phieu-giam-gia">
                            <input type="hidden" name="id" value="${p.maPhieuGiamGia}">
                            <button class="btn sm line" type="submit">${p.trangThai == 'DANG_CHAY' ? 'Tạm dừng' : 'Chạy lại'}</button>
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

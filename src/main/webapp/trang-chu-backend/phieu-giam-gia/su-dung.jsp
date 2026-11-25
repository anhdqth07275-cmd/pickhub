<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/phieu-giam-gia">← Phiếu giảm giá</a>
<%-- note code nhu nay nhe ban: chon 1 phieu o day thi servlet gui them phieuDangXem + thongKeKhach -> hien bang "moi khach da dung may lan / toi da bao nhieu lan" --%>
<form class="toolbar" method="get" action="${ctx}/wp-admin/phieu-giam-gia/su-dung">
    <select name="maPhieu">
        <option value="">Tất cả phiếu</option>
        <c:forEach items="${danhSachPhieu}" var="dp">
            <option value="${dp.maPhieuGiamGia}" ${param.maPhieu == dp.maPhieuGiamGia ? 'selected' : ''}>${dp.maGiamGia} - <c:out value="${dp.tenPhieu}"/></option>
        </c:forEach>
    </select>
    <select name="trangThai">
        <option value="">Tất cả trạng thái</option>
        <option value="DA_SU_DUNG" ${param.trangThai == 'DA_SU_DUNG' ? 'selected' : ''}>Đã sử dụng</option>
        <option value="DA_HOAN" ${param.trangThai == 'DA_HOAN' ? 'selected' : ''}>Đã hoàn</option>
    </select>
    <button class="btn" type="submit">Lọc</button>
</form>

<c:if test="${not empty phieuDangXem}">
    <div class="card" style="margin-bottom:16px">
        <h3>Giới hạn sử dụng của phiếu <span class="code">${phieuDangXem.maGiamGia}</span></h3>
        <dl class="info-grid">
            <div><dt>Mỗi khách được dùng tối đa</dt><dd><b>${phieuDangXem.gioiHanMoiNguoi}</b> lần</dd></div>
            <div><dt>Tổng lượt toàn hệ thống</dt><dd>${phieuDangXem.soLanDaSuDung} / ${empty phieuDangXem.gioiHanSuDung ? '∞ (không giới hạn)' : phieuDangXem.gioiHanSuDung}</dd></div>
            <div><dt>Số khách đã dùng</dt><dd>${fn:length(thongKeKhach)}</dd></div>
        </dl>
        <table class="tbl">
            <tr><th>Khách hàng</th><th>Email</th><th>Đã dùng / tối đa</th><th class="num">Còn lại</th></tr>
            <c:forEach items="${thongKeKhach}" var="k">
                <tr>
                    <td><b><c:out value="${k.hoTen}"/></b></td>
                    <td class="muted"><c:out value="${k.thuDienTu}"/></td>
                    <td>
                        <span class="usage-bar ${k.hetLuot ? 'full' : ''}"><i style="width:${k.hetLuot ? 100 : k.soLuotDaDung * 100 / k.gioiHanMoiNguoi}%"></i></span>
                        ${k.soLuotDaDung} / ${k.gioiHanMoiNguoi}
                    </td>
                    <td class="num"><c:choose>
                        <c:when test="${k.hetLuot}"><span class="badge red">Hết lượt</span></c:when>
                        <c:otherwise><b>${k.conLai}</b> lần</c:otherwise>
                    </c:choose></td>
                </tr>
            </c:forEach>
            <c:if test="${empty thongKeKhach}"><tr><td colspan="4" class="empty">Chưa có khách nào dùng phiếu này.</td></tr></c:if>
        </table>
        <p class="hint">Chỉ đếm lượt "Đã sử dụng"; lượt đã hoàn do hủy đơn không bị tính.</p>
    </div>
</c:if>

<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Phiếu</th><th>Khách hàng</th><th>Hóa đơn</th><th class="num">Số tiền giảm</th><th>Ngày dùng</th><th>Trạng thái</th><th>Ngày hoàn</th></tr>
        <c:forEach items="${pt.danhSach}" var="s">
            <tr>
                <td class="code">${s.phieuGiamGia.maGiamGia}</td>
                <td>${s.khachHang.hoTen}</td>
                <td><a href="${ctx}/wp-admin/don-hang/chi-tiet?id=${s.maHoaDon}">#${s.maHoaDon}</a></td>
                <td class="num">${ph:tien(s.soTienGiam)}</td>
                <td class="nowrap">${ph:ngayGio(s.ngaySuDung)}</td>
                <td><span class="badge ${s.trangThai}">${ph:nhan(s.trangThai)}</span></td>
                <td class="nowrap">${ph:ngayGio(s.ngayHoanLai)}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="7" class="empty">Chưa có lượt sử dụng</td></tr></c:if>
    </table>
</div>
<%-- note code nhu nay nhe ban: phan-trang.jspf chung khong giu tham so maPhieu nen o day tu ghep link phan trang --%>
<c:if test="${pt.tongTrang > 1}">
    <div class="pager">
        <span>Trang ${pt.trangHienTai}/${pt.tongTrang} · ${pt.tongSo} lượt</span>
        <c:forEach begin="1" end="${pt.tongTrang}" var="i">
            <c:url var="lienKet" value="/wp-admin/phieu-giam-gia/su-dung">
                <c:param name="trang" value="${i}"/>
                <c:param name="trangThai" value="${param.trangThai}"/>
                <c:param name="maPhieu" value="${param.maPhieu}"/>
            </c:url>
            <a class="${i == pt.trangHienTai ? 'on' : ''}" href="${lienKet}">${i}</a>
        </c:forEach>
    </div>
</c:if>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

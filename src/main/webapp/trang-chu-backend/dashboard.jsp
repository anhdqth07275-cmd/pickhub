<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- 4 the so tren cung (khop anh mau) --%>
<div class="grid-4">
    <div class="card stat">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/></svg></div>
        <div><span>Doanh thu tháng này</span><strong>${ph:tien(tq.doanhThuThang)}</strong></div>
    </div>
    <a class="card stat" href="${ctx}/wp-admin/don-hang?trangThai=CHO_XAC_NHAN">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><path d="M5 7h14l-1 13H6L5 7z"/><path d="M9 7a3 3 0 016 0"/></svg></div>
        <div><span>Đơn mới</span><strong>${tq.donMoi}</strong></div>
    </a>
    <%-- note code nhu nay nhe ban: Dat san chua lam, chi hien the va bam vao ra trang "Sap ra mat" --%>
    <a class="card stat soon" href="${ctx}/wp-admin/dat-san">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/></svg></div>
        <div><span>Lượt đặt sân hôm nay</span><strong>Sắp ra mắt</strong></div>
    </a>
    <a class="card stat" href="${ctx}/wp-admin/khach-hang">
        <div class="ico"><svg class="ic" viewBox="0 0 24 24"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 20c.5-3.5 3-5.5 6.5-5.5s6 2 6.5 5.5"/></svg></div>
        <div><span>Khách hàng</span><strong>${tq.soKhachHang}</strong></div>
    </a>
</div>

<div class="grid-2">
    <%-- Bieu do doanh thu: chi cao cot = phanTram tinh san o ThongKeService --%>
    <div class="card">
        <div class="chart-head">
            <h3>Doanh thu ${tq.kieuBieuDo == 'thang' ? '6 tháng' : '7 ngày'} (triệu đồng)</h3>
            <div class="tabs">
                <a class="${tq.kieuBieuDo == 'ngay' ? 'on' : ''}" href="${ctx}/wp-admin?kieu=ngay">7 ngày</a>
                <a class="${tq.kieuBieuDo == 'thang' ? 'on' : ''}" href="${ctx}/wp-admin?kieu=thang">6 tháng</a>
            </div>
        </div>
        <div class="bars">
            <c:forEach items="${tq.bieuDo}" var="cot">
                <div class="bar-col">
                    <div class="bar-val">${ph:trieu(cot.giaTri)}</div>
                    <div class="bar ${cot.homNay ? 'now' : ''}" style="height: ${cot.phanTram}%"></div>
                    <div class="bar-label">${cot.nhan}</div>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="card">
        <h3>Cần xử lý</h3>
        <ul class="todo">
            <li><a href="${ctx}/wp-admin/don-hang?trangThai=CHO_XAC_NHAN">Đơn mới cần xác nhận <span class="count ${tq.donChoXacNhan == 0 ? 'zero' : ''}">${tq.donChoXacNhan}</span></a></li>
            <li><a href="${ctx}/wp-admin/don-hang?trangThai=DANG_GIAO">Đơn đang giao <span class="count ${tq.donDangGiao == 0 ? 'zero' : ''}">${tq.donDangGiao}</span></a></li>
            <li><a href="${ctx}/wp-admin/ton-kho?canhBao=1">Sản phẩm sắp hết hoặc hết hàng <span class="count ${tq.tonKhoCanhBao == 0 ? 'zero' : ''}">${tq.tonKhoCanhBao}</span></a></li>
            <li><a href="${ctx}/wp-admin/khuyen-mai?trangThai=SAP_CHAY">Khuyến mãi sắp chạy <span class="count ${tq.khuyenMaiSapChay == 0 ? 'zero' : ''}">${tq.khuyenMaiSapChay}</span></a></li>
        </ul>
    </div>
</div>

<div class="section-title">Đơn hàng gần đây</div>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th>Mã đơn</th><th>Khách hàng</th><th>Tổng tiền</th><th>Trạng thái</th></tr>
        <c:forEach items="${tq.donGanDay}" var="h">
            <tr>
                <td><a class="code" href="${ctx}/wp-admin/don-hang/chi-tiet?id=${h.maHoaDon}">#${h.maTraCuu}</a></td>
                <td>${h.khachHang.hoTen}</td>
                <td>${ph:tien(h.tongTien)}</td>
                <td><span class="badge ${h.trangThai}">${ph:nhan(h.trangThai)}</span></td>
            </tr>
        </c:forEach>
        <c:if test="${empty tq.donGanDay}"><tr><td colspan="4" class="empty">Chưa có đơn hàng</td></tr></c:if>
    </table>
</div>

<div class="grid-2e" style="margin-top:18px">
    <div class="card">
        <h3>Cảnh báo tồn kho</h3>
        <table class="tbl">
            <c:forEach items="${tq.tonKhoThap}" var="c">
                <tr><td>${c.sanPham.tenSanPham}<br><span class="muted">${c.maHang}</span></td>
                    <td class="num"><b>${c.soLuongTon}</b> / ${c.nguongCanhBaoTonKho}</td></tr>
            </c:forEach>
            <c:if test="${empty tq.tonKhoThap}"><tr><td class="empty">Tồn kho ổn định</td></tr></c:if>
        </table>
    </div>
    <div class="card">
        <h3>Sản phẩm bán chạy</h3>
        <table class="tbl">
            <c:forEach items="${tq.topBanChay}" var="dong">
                <tr><td>${dong[0]}</td><td class="num"><b>${dong[1]}</b> đã bán</td></tr>
            </c:forEach>
            <c:if test="${empty tq.topBanChay}"><tr><td class="empty">Chưa có dữ liệu</td></tr></c:if>
        </table>
    </div>
</div>

<%-- note code nhu nay nhe ban: tong quan du lieu cua du 18 bang, bam vao nhay toi module tuong ung --%>
<div class="section-title">Tổng quan dữ liệu (18 bảng)</div>
<div class="tiles">
    <c:forEach items="${tq.thongKeBang}" var="o">
        <a class="tile" href="${ctx}${o.duongDan}"><b>${o.soDong}</b><span>${o.ten}</span></a>
    </c:forEach>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- Trang IN HOA DON. Servlet gui sang: hoaDon, dongHoaDon, thoiGian, phuongThuc, tuDong --%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hóa đơn ${hoaDon.maTraCuu}</title>
    <%-- note code nhu nay nhe ban: hoa don IN RA giay giu nguyen den tren trang, chi them bieu tuong tab --%>
    <link rel="icon" href="${ctx}/favicon.svg" type="image/svg+xml">
    <link rel="icon" href="${ctx}/favicon.ico" sizes="any">
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; background: #e5e7eb; font-family: "Courier New", Consolas, monospace; color: #000; }
        .thanh-nut { position: sticky; top: 0; background: #1e293b; padding: 10px; display: flex; gap: 8px; justify-content: center; }
        .thanh-nut button { border: 0; border-radius: 8px; padding: 9px 16px; font-weight: 700; cursor: pointer; background: #ccff00; color: #1e293b; }
        .thanh-nut button.phu { background: #fff; }
        .giay { background: #fff; margin: 14px auto; padding: 10px 12px 18px; width: 80mm; font-size: 12.5px; line-height: 1.35; box-shadow: 0 2px 10px rgba(0,0,0,.25); }
        body.a5 .giay { width: 148mm; font-size: 14px; padding: 16mm 14mm; }
        .giua { text-align: center; }
        .cua-hang { font-size: 17px; font-weight: 800; letter-spacing: .5px; }
        .gach { border-top: 1px dashed #000; margin: 7px 0; }
        table { width: 100%; border-collapse: collapse; }
        td, th { padding: 2px 0; vertical-align: top; }
        th { text-align: left; border-bottom: 1px solid #000; }
        .p { text-align: right; white-space: nowrap; }
        .bang-hang { table-layout: fixed; }
        .bang-hang .c-sl { width: 9mm; text-align: center; padding: 2px 4px; }
        .bang-hang .c-tien { width: 24mm; padding-left: 4px; }
        .bang-hang td:first-child, .bang-hang th:first-child { word-break: break-word; padding-right: 4px; }
        body.a5 .bang-hang .c-sl { width: 16mm; }
        body.a5 .bang-hang .c-tien { width: 34mm; }
        .phu-dong { font-size: 11px; color: #333; }
        .tong td { font-weight: 800; font-size: 14px; padding-top: 4px; }
        .cam-on { margin-top: 10px; font-size: 12px; }
        @page { size: 80mm auto; margin: 0; }
        @media print {
            body { background: #fff; }
            .thanh-nut { display: none; }
            .giay { margin: 0; box-shadow: none; width: 80mm; padding: 4mm 3mm 8mm; }
            body.a5 .giay { width: 148mm; padding: 12mm; }
            body.a5 { }
        }
    </style>
</head>
<body>
<div class="thanh-nut">
    <button type="button" onclick="window.print()">🖨 In (Ctrl+P)</button>
    <button type="button" class="phu" onclick="document.body.classList.toggle('a5')">Đổi khổ 80mm / A5</button>
    <button type="button" class="phu" onclick="window.close()">Đóng</button>
</div>
<div class="giay">
    <div class="giua">
        <div class="cua-hang">PICKHUB</div>
        <div>Vợt &amp; phụ kiện Pickleball chính hãng</div>
        <div>Hotline: 0969830606</div>
    </div>
    <div class="gach"></div>
    <div class="giua"><b>HÓA ĐƠN BÁN HÀNG</b></div>
    <div>Số HĐ : ${hoaDon.maTraCuu}</div>
    <div>Ngày  : ${thoiGian}</div>
    <div>Thu ngân: <c:out value="${hoaDon.nhanVienXacNhan.hoTen}"/></div>
    <c:if test="${hoaDon.tenNguoiNhan != 'Khách lẻ'}"><div>Khách : <c:out value="${hoaDon.tenNguoiNhan}"/><c:if test="${hoaDon.soDienThoaiNguoiNhan != '0000000000'}"> - ${hoaDon.soDienThoaiNguoiNhan}</c:if></div></c:if>
    <div class="gach"></div>
    <table class="bang-hang">
        <tr><th>Mặt hàng</th><th class="c-sl">SL</th><th class="p c-tien">T.Tiền</th></tr>
        <c:forEach items="${dongHoaDon}" var="d">
            <tr>
                <td><c:out value="${d.tenSanPham}"/>
                    <div class="phu-dong"><c:if test="${not empty d.moTaChiTiet}"><c:out value="${d.moTaChiTiet}"/> · </c:if>${d.maHang}</div>
                    <div class="phu-dong"><fmt:formatNumber value="${d.donGia}" pattern="#,##0"/><c:if test="${d.donGia < d.donGiaGoc}"> (gốc <fmt:formatNumber value="${d.donGiaGoc}" pattern="#,##0"/>)</c:if></div></td>
                <td class="c-sl">${d.soLuong}</td>
                <td class="p c-tien"><fmt:formatNumber value="${d.donGia * d.soLuong}" pattern="#,##0"/></td>
            </tr>
        </c:forEach>
    </table>
    <div class="gach"></div>
    <table>
        <tr><td>Tạm tính</td><td class="p"><fmt:formatNumber value="${hoaDon.tamTinh}" pattern="#,##0"/></td></tr>
        <c:if test="${hoaDon.soTienGiam.signum() > 0}">
            <tr><td>Giảm giá<c:if test="${not empty hoaDon.phieuGiamGia}"> (${hoaDon.phieuGiamGia.maGiamGia})</c:if></td><td class="p">- <fmt:formatNumber value="${hoaDon.soTienGiam}" pattern="#,##0"/></td></tr>
        </c:if>
        <tr class="tong"><td>TỔNG CỘNG</td><td class="p"><fmt:formatNumber value="${hoaDon.tongTien}" pattern="#,##0"/> đ</td></tr>
        <tr><td>Hình thức</td><td class="p">${phuongThuc}</td></tr>
        <c:if test="${hoaDon.phuongThucThanhToan == 'TIEN_MAT'}">
            <tr><td>Khách đưa</td><td class="p"><fmt:formatNumber value="${hoaDon.tienKhachDua}" pattern="#,##0"/></td></tr>
            <tr><td>Tiền thừa</td><td class="p"><fmt:formatNumber value="${hoaDon.tienThua}" pattern="#,##0"/></td></tr>
        </c:if>
    </table>
    <c:if test="${not empty hoaDon.ghiChu}"><div class="gach"></div><div>Ghi chú: <c:out value="${hoaDon.ghiChu}"/></div></c:if>
    <div class="gach"></div>
    <div class="giua cam-on">Cảm ơn quý khách - Hẹn gặp lại!<br>Đổi trả trong 7 ngày với hóa đơn này nếu lỗi nhà sản xuất.</div>
</div>
<c:if test="${tuDong}"><script>window.addEventListener('load', function () { setTimeout(function () { window.print(); }, 300); });</script></c:if>
</body>
</html>

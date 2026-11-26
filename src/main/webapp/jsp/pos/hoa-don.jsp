<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- Hoa don da ban tai quay. Servlet gui sang: danhSach (HoaDonQuayDongDTO), tongKet, tuNgay, denNgay, laQuanLy, xemTatCa --%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hóa đơn đã bán - PickHub POS</title>
    <link rel="stylesheet" href="${ctx}/assets/pos.css">
    <%-- note code nhu nay nhe ban: giao dien kinh mo xanh la (sang/toi). Nap SAU pos.css --%>
    <%@ include file="/WEB-INF/theme-head.jspf" %>
    <link rel="stylesheet" href="${ctx}/assets/pos-glass.css">
</head>
<body style="height:auto">
<header class="pos-top">
    <div class="logo">PICK<span>HUB</span> <span class="pos-tag">POS</span></div>
    <div class="phai">
        <span>👤 <b><c:out value="${sessionScope.nhanVien.hoTen}"/></b></span>
        <a href="${ctx}/pos">← Về màn hình bán hàng</a>
        <a href="${ctx}/dang-xuat">Đăng xuất</a>
    </div>
</header>
<div class="trang">
    <h1 style="margin-top:0">Hóa đơn đã bán tại quầy</h1>
    <form class="the-trang loc-hd" method="get" action="${ctx}/pos/hoa-don">
        <div style="flex:1"><label>Tìm mã hóa đơn / tên khách / SĐT</label><input type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>"></div>
        <div><label>Từ ngày</label><input type="date" name="tuNgay" value="${tuNgay}"></div>
        <div><label>Đến ngày</label><input type="date" name="denNgay" value="${denNgay}"></div>
        <c:if test="${laQuanLy}">
            <div><label>&nbsp;</label><label class="chk-tat-ca"><input type="checkbox" name="tatCa" value="1" style="width:auto" ${xemTatCa ? 'checked' : ''}> Tất cả nhân viên</label></div>
        </c:if>
        <div style="min-width:auto"><label>&nbsp;</label><button class="btn lime" type="submit">Xem</button></div>
    </form>
    <div class="thong-ke">
        <div class="o"><span class="muted">Số hóa đơn</span><b>${tongKet.soHoaDon}</b></div>
        <div class="o"><span class="muted">Doanh thu</span><b><fmt:formatNumber value="${tongKet.doanhThu}" pattern="#,##0"/> đ</b></div>
    </div>
    <div class="the-trang" style="padding:0;overflow-x:auto">
        <table class="bang">
            <tr><th>Mã hóa đơn</th><th>Thời gian</th><th>Thu ngân</th><th>Khách</th><th>Thanh toán</th><th class="so">Mặt hàng</th><th class="so">Tổng tiền</th><th></th></tr>
            <c:forEach items="${danhSach}" var="d">
                <tr>
                    <td><b>${d.maTraCuu}</b></td>
                    <td>${d.thoiGian}</td>
                    <td><c:out value="${d.nhanVien}"/></td>
                    <td><c:out value="${d.khachHang}"/></td>
                    <td>${d.phuongThuc}</td>
                    <td class="so">${d.soMatHang}</td>
                    <td class="so"><b><fmt:formatNumber value="${d.tongTien}" pattern="#,##0"/> đ</b></td>
                    <td class="so"><a class="btn sm line" target="_blank" href="${ctx}/pos/in-hoa-don?id=${d.maHoaDon}">Xem / In lại</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty danhSach}"><tr><td colspan="8" class="muted" style="text-align:center;padding:30px">Không có hóa đơn nào trong khoảng thời gian này</td></tr></c:if>
        </table>
    </div>
</div>
</body>
</html>

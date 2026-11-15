<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-khach/layout/header.jspf" %>
<h1>Giỏ hàng</h1>
<c:forEach items="${gio.canhBao}" var="cb"><div class="alert warn"><c:out value="${cb}"/></div></c:forEach>

<c:choose>
<c:when test="${gio.rong}">
    <div class="card" style="text-align:center;padding:50px">
        <p class="muted">Giỏ hàng của bạn đang trống.</p>
        <a class="btn lime" href="${ctx}/cua-hang">Tiếp tục mua sắm</a>
    </div>
</c:when>
<c:otherwise>
<div class="layout">
    <div class="card">
        <table class="tbl">
            <tr><th>Sản phẩm</th><th class="num">Đơn giá</th><th>Số lượng</th><th class="num">Thành tiền</th><th></th></tr>
            <c:forEach items="${gio.dong}" var="d">
                <tr class="${empty d.loi ? '' : 'dong-loi'}">
                    <td>
                        <div class="sp-cell">
                            <c:if test="${not empty d.anh}"><img class="thumb" src="${ctx}${d.anh}" alt="" onerror="this.style.display='none'"></c:if>
                            <div><b>${d.tenSanPham}</b><br><span class="muted" style="font-size:13px">${d.moTaChiTiet}</span>
                                <c:if test="${not empty d.loi}"><br><span class="loi-text"><c:out value="${d.loi}"/></span></c:if></div>
                        </div>
                    </td>
                    <td class="num">${ph:tien(d.giaHienTai)}<c:if test="${d.coGiamGia}"><br><s class="muted" style="font-size:13px">${ph:tien(d.giaGoc)}</s></c:if></td>
                    <td>
                        <form class="qty" method="post" action="${ctx}/gio-hang">
                            <input type="hidden" name="hanhDong" value="cap-nhat"><input type="hidden" name="maDong" value="${d.maHoaDonChiTiet}">
                            <input type="number" name="soLuong" value="${d.soLuong}" min="0" max="${d.tonKho}">
                            <button class="btn sm line" type="submit">Cập nhật</button>
                        </form>
                    </td>
                    <td class="num"><b>${ph:tien(d.thanhTien)}</b></td>
                    <td>
                        <form class="inline" method="post" action="${ctx}/gio-hang">
                            <input type="hidden" name="hanhDong" value="xoa-dong"><input type="hidden" name="maDong" value="${d.maHoaDonChiTiet}">
                            <button class="btn sm danger" type="submit">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form method="post" action="${ctx}/gio-hang" style="margin-top:12px" onsubmit="return confirm('Xóa toàn bộ giỏ hàng?')">
            <input type="hidden" name="hanhDong" value="xoa-gio"><button class="btn sm line" type="submit">Xóa tất cả</button>
        </form>
    </div>

    <div class="card tom-tat">
        <b>Tóm tắt đơn hàng</b>
        <%-- note code nhu nay nhe ban: o nhap ma giam gia. Dang ap ma thi hien ma + nut bo ma --%>
        <c:choose>
            <c:when test="${not empty gio.maPhieu}">
                <div class="phieu-ok"><span>Mã: ${gio.maPhieu}</span>
                    <form class="inline" method="post" action="${ctx}/gio-hang"><input type="hidden" name="hanhDong" value="bo-phieu"><button class="btn sm line" type="submit">Bỏ mã</button></form></div>
            </c:when>
            <c:otherwise>
                <form class="phieu" method="post" action="${ctx}/gio-hang">
                    <input type="hidden" name="hanhDong" value="ap-phieu">
                    <input type="text" name="maPhieu" placeholder="Mã giảm giá" maxlength="30">
                    <button class="btn sm lime" type="submit">Áp dụng</button>
                </form>
            </c:otherwise>
        </c:choose>
        <div class="hang"><span>Tạm tính (${gio.tongSoLuong} sản phẩm)</span><span>${ph:tien(gio.tamTinh)}</span></div>
        <div class="hang"><span>Giảm giá</span><span>- ${ph:tien(gio.soTienGiam)}</span></div>
        <div class="hang"><span>Phí vận chuyển</span><span>Miễn phí</span></div>
        <div class="hang tong"><span>Tổng cộng</span><span>${ph:tien(gio.tongTien)}</span></div>
        <c:choose>
            <c:when test="${gio.coLoi}"><button class="btn lime" style="width:100%;margin-top:12px" disabled>Xử lý sản phẩm lỗi để đặt hàng</button></c:when>
            <c:otherwise><a class="btn lime" style="width:100%;margin-top:12px;text-align:center" href="${ctx}/dat-hang">Tiến hành đặt hàng</a></c:otherwise>
        </c:choose>
    </div>
</div>
</c:otherwise>
</c:choose>
<%@ include file="/trang-chu-khach/layout/footer.jspf" %>

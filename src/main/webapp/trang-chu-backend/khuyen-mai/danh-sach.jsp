<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<%-- CHUONG TRINH KHUYEN MAI - LAM LAI THEO ANH MAU (popup tich chon san pham).
     KhuyenMaiServlet gui sang:
       pt (PhanTrang<ChuongTrinhKhuyenMai>), soSanPham (Map ma chuong trinh -> so san pham)
       Khi bam "Them" / "Sua" (?hanhDong=form) co them: moPopup=true, ct, dsDong (List<DongKhuyenMaiDTO>), khoaSanPham, loi
     note code nhu nay nhe ban: "Them" va "Sua" deu la LINK sang ?hanhDong=form, server ve lai trang nay kem popup mo san
     (khong can JS de do du lieu vao o nhap). Dong popup = bam X / Huy / Esc (tro ve danh sach). --%>

<div class="page-actions">
    <span class="muted">Có <b>${pt.tongSo}</b> chương trình khuyến mãi</span>
    <a class="btn lime" href="${ctx}/wp-admin/khuyen-mai?hanhDong=form">+ Thêm chương trình</a>
</div>

<%-- ===================== THANH TIM KIEM ===================== --%>
<form class="card km-loc" method="get" action="${ctx}/wp-admin/khuyen-mai">
    <div class="km-o-tim">
        <input type="text" name="tuKhoa" value="<c:out value='${param.tuKhoa}'/>" placeholder="Tìm theo tên chương trình..." autocomplete="off">
    </div>
    <select name="trangThai">
        <option value="">Mọi trạng thái</option>
        <c:forTokens items="SAP_CHAY,DANG_CHAY,TAM_DUNG,KET_THUC" delims="," var="t">
            <option value="${t}" ${param.trangThai == t ? 'selected' : ''}>${ph:nhan(t)}</option>
        </c:forTokens>
    </select>
    <button class="btn" type="submit">Tìm kiếm</button>
    <c:if test="${not empty param.tuKhoa or not empty param.trangThai}"><a class="btn line" href="${ctx}/wp-admin/khuyen-mai">Xóa lọc</a></c:if>
</form>

<%-- ===================== BANG CHUONG TRINH ===================== --%>
<div class="card table-wrap" style="padding:0">
    <table class="tbl">
        <tr><th class="num">STT</th><th>Chương trình</th><th>Thời gian</th><th class="num">Số SP</th><th>Trạng thái</th><th>Thao tác</th></tr>
        <c:forEach items="${pt.danhSach}" var="c" varStatus="st">
            <tr>
                <td class="num">${(pt.trangHienTai - 1) * 10 + st.count}</td>
                <td>
                    <b><c:out value="${c.tenChuongTrinh}"/></b>
                    <c:if test="${not empty c.moTa}"><br><span class="muted"><c:out value="${c.moTa}"/></span></c:if>
                </td>
                <td class="nowrap">
                    <span class="muted">Từ</span> ${ph:ngayGio(c.ngayBatDau)}<br>
                    <span class="muted">Đến</span> ${ph:ngayGio(c.ngayKetThuc)}
                </td>
                <td class="num"><b>${empty soSanPham[c.maChuongTrinhKhuyenMai] ? 0 : soSanPham[c.maChuongTrinhKhuyenMai]}</b></td>
                <td><span class="badge ${c.trangThai}">${ph:nhan(c.trangThai)}</span></td>
                <td>
                    <div class="row-actions">
                        <a class="btn sm line" href="${ctx}/wp-admin/khuyen-mai/chi-tiet?id=${c.maChuongTrinhKhuyenMai}">Chi tiết</a>
                        <a class="btn sm line" href="${ctx}/wp-admin/khuyen-mai?hanhDong=form&amp;id=${c.maChuongTrinhKhuyenMai}">Sửa</a>
                        <c:if test="${c.trangThai != 'KET_THUC'}">
                            <form method="post" action="${ctx}/wp-admin/khuyen-mai">
                                <input type="hidden" name="hanhDong" value="doi-trang-thai">
                                <input type="hidden" name="id" value="${c.maChuongTrinhKhuyenMai}">
                                <button class="btn sm warn" type="submit">${c.trangThai == 'TAM_DUNG' ? 'Chạy lại' : 'Tạm dừng'}</button>
                            </form>
                        </c:if>
                        <form method="post" action="${ctx}/wp-admin/khuyen-mai" onsubmit="return confirm('Xóa chương trình &quot;${fn:escapeXml(c.tenChuongTrinh)}&quot;? Không thể hoàn tác.')">
                            <input type="hidden" name="hanhDong" value="xoa">
                            <input type="hidden" name="id" value="${c.maChuongTrinhKhuyenMai}">
                            <button class="btn sm danger" type="submit">Xóa</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pt.danhSach}"><tr><td colspan="6" class="empty">Không có chương trình khuyến mãi</td></tr></c:if>
    </table>
</div>
<%@ include file="/trang-chu-backend/layout/phan-trang.jspf" %>

<%-- ===================== POPUP THEM / SUA (theo anh mau) ===================== --%>
<c:if test="${moPopup}">
<div class="km-nen" id="km-nen" data-dong="${ctx}/wp-admin/khuyen-mai">
    <form class="km-hop" id="km-form" method="post" action="${ctx}/wp-admin/khuyen-mai" data-tim="${ctx}/wp-admin/khuyen-mai/tim-san-pham" autocomplete="off">
        <input type="hidden" name="hanhDong" value="luu">
        <input type="hidden" name="id" value="${ct.maChuongTrinhKhuyenMai}">
        <%-- co suaSanPham chi gui khi popup HIEN bang san pham (xem ghi chu o KhuyenMaiServlet) --%>
        <c:if test="${!khoaSanPham}"><input type="hidden" name="suaSanPham" value="1"></c:if>

        <div class="km-dau">
            <h2>${empty ct.maChuongTrinhKhuyenMai ? 'Thêm chương trình khuyến mãi' : 'Sửa chương trình khuyến mãi'}</h2>
            <a class="km-x" href="${ctx}/wp-admin/khuyen-mai" title="Đóng (Esc)">✕</a>
        </div>

        <div class="km-than">
            <div class="alert loi" id="km-loi" ${empty loi ? 'hidden' : ''}><c:out value="${loi}"/></div>

            <div class="km-luoi">
                <div class="fld km-r2">
                    <label for="km-ten">Tên chương trình <span class="req">*</span></label>
                    <input id="km-ten" type="text" name="tenChuongTrinh" maxlength="150" required value="<c:out value='${ct.tenChuongTrinh}'/>" placeholder="VD: Siêu sale 11.11">
                </div>
                <div class="fld km-r2">
                    <label for="km-mota">Mô tả</label>
                    <input id="km-mota" type="text" name="moTa" maxlength="500" value="<c:out value='${ct.moTa}'/>" placeholder="VD: Giảm sâu nhiều mẫu vợt và bóng">
                </div>
                <div class="fld">
                    <label for="km-bd">Bắt đầu <span class="req">*</span></label>
                    <input id="km-bd" type="datetime-local" name="ngayBatDau" required value="${ct.ngayBatDau}">
                </div>
                <div class="fld">
                    <label for="km-kt">Kết thúc <span class="req">*</span></label>
                    <input id="km-kt" type="datetime-local" name="ngayKetThuc" required value="${ct.ngayKetThuc}">
                </div>
            </div>
            <p class="hint">Trạng thái (Sắp chạy → Đang chạy → Kết thúc) tự đổi theo thời gian.</p>

            <c:choose>
                <%-- chuong trinh da ket thuc: khong sua san pham, chi sua thong tin --%>
                <c:when test="${khoaSanPham}">
                    <div class="alert km-khoa">Chương trình đã <b>kết thúc</b> nên không thể sửa danh sách sản phẩm. Muốn sửa, hãy kéo dài <b>Ngày kết thúc</b>, lưu lại rồi mở sửa lần nữa.</div>
                </c:when>
                <c:otherwise>
                    <hr class="km-gach">

                    <%-- Muc giam mac dinh (giong o "Muc giam gia" trong anh mau) --%>
                    <div class="km-muc-giam">
                        <div class="fld">
                            <label for="km-loai-md">Mức giảm mặc định</label>
                            <select id="km-loai-md">
                                <option value="PHAN_TRAM">Giảm theo phần trăm (%)</option>
                                <option value="SO_TIEN">Giảm số tiền cố định (đ)</option>
                            </select>
                        </div>
                        <div class="fld">
                            <label for="km-gia-tri-md">Giá trị giảm</label>
                            <input id="km-gia-tri-md" type="number" step="any" min="0" placeholder="VD: 10">
                        </div>
                        <button type="button" class="btn line" id="km-ap-dung">Áp dụng cho sản phẩm đã chọn</button>
                    </div>
                    <p class="hint">Tích chọn sản phẩm sẽ tự điền mức giảm này, bạn vẫn sửa riêng được từng dòng.</p>

                    <div class="km-tieu-de">
                        <label>Tích chọn sản phẩm áp dụng khuyến mãi <span class="req">*</span></label>
                        <div class="km-tieu-de-phai">
                            <label class="km-chi-chon"><input type="checkbox" id="km-chi-da-chon"> Chỉ hiện đã chọn (<b id="km-dem">0</b>)</label>
                            <a class="btn sm line" target="_blank" href="${ctx}/wp-admin/san-pham?hanhDong=them">+ Thêm sản phẩm mới</a>
                        </div>
                    </div>

                    <%-- THANH TIM KIEM san pham --%>
                    <div class="km-o-tim km-o-tim-day">
                        <input id="km-tim" type="text" placeholder="Tìm mã hoặc tên sản phẩm..." autocomplete="off">
                    </div>

                    <div class="km-bang-wrap">
                        <table class="km-bang">
                            <thead>
                                <tr>
                                    <th class="km-c-chon"><input type="checkbox" id="km-chon-tat-ca" title="Chọn tất cả sản phẩm đang hiển thị" aria-label="Chọn tất cả"></th>
                                    <th>Chọn / Sản phẩm</th><th class="num">Giá gốc</th><th>Loại giảm</th><th>Mức giảm</th><th>SL tối đa</th><th class="num">Giá sau giảm</th>
                                </tr>
                            </thead>
                            <tbody id="km-tbody">
                                <c:forEach items="${dsDong}" var="d"><%@ include file="/trang-chu-backend/khuyen-mai/dong-san-pham.jspf" %></c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="km-ghi-chu" id="km-ghi-chu"></div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="km-chan">
            <div class="km-tong" id="km-tong">${khoaSanPham ? '' : 'Đã chọn: 0 sản phẩm'}</div>
            <div class="km-nut">
                <a class="btn line" href="${ctx}/wp-admin/khuyen-mai">Hủy</a>
                <button class="btn lime" type="submit">Lưu chương trình</button>
            </div>
        </div>
    </form>
</div>
<script src="${ctx}/assets/khuyen-mai.js"></script>
</c:if>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-khach/layout/header.jspf" %>
<h1>Cửa hàng</h1>
<p class="muted">Trang demo để thử giỏ hàng. Storefront đầy đủ (lọc, tìm kiếm, chi tiết sản phẩm) thuộc module sản phẩm.</p>
<div class="grid">
    <c:forEach items="${dsBienThe}" var="b">
        <div class="card sp">
            <c:if test="${b.coGiamGia}"><span class="tag">ĐANG GIẢM GIÁ</span></c:if>
            <h3>${b.tenSanPham}</h3>
            <div class="mota">${b.moTa}<br>Mã: ${b.maHang} · Còn ${b.soLuongTon}</div>
            <div class="gia">${ph:tien(b.giaSauGiam)}<c:if test="${b.coGiamGia}"><s>${ph:tien(b.giaGoc)}</s></c:if></div>
            <button class="btn lime" type="button" onclick="themVaoGio(${b.maChiTietSanPham})">Thêm vào giỏ</button>
        </div>
    </c:forEach>
</div>
<script>
    var CTX = '${ctx}';
    function hienToast(noiDung, loi) {
        var t = document.getElementById('toast');
        t.textContent = noiDung; t.className = loi ? 'loi' : ''; t.style.display = 'block';
        setTimeout(function () { t.style.display = 'none'; }, 2500);
    }
    // note code nhu nay nhe ban: AJAX them vao gio. Chua dang nhap (HTTP 401) thi chuyen sang /dang-nhap va nho san pham dang chon (them=id)
    function themVaoGio(id) {
        fetch(CTX + '/api/gio-hang/them', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'maChiTietSanPham=' + id + '&soLuong=1'
        }).then(function (r) { return r.json().then(function (j) { return {status: r.status, j: j}; }); })
          .then(function (x) {
              if (x.status === 401) { location.href = CTX + '/dang-nhap?quayLai=/cua-hang&them=' + id; return; }
              hienToast(x.j.thongBao, !x.j.ok);
              if (x.j.ok) document.getElementById('badge').textContent = x.j.soLuong;
          });
    }
</script>
<%@ include file="/trang-chu-khach/layout/footer.jspf" %>

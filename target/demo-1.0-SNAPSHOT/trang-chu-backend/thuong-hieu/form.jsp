<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/thuong-hieu">← Danh sách thương hiệu</a>
<div class="card">
    <c:if test="${not empty loi}"><div class="alert loi"><c:out value="${loi}"/></div></c:if>
    <form class="form-grid" method="post" action="${ctx}/wp-admin/thuong-hieu">
        <input type="hidden" name="hanhDong" value="luu">
        <input type="hidden" name="id" value="${th.maThuongHieu}">
        <div><label>Tên thương hiệu *</label><input type="text" name="tenThuongHieu" maxlength="100" value="<c:out value='${th.tenThuongHieu}'/>" required></div>
        <div><label>Đường dẫn logo</label><input type="text" name="duongDanLogo" maxlength="500" value="<c:out value='${th.duongDanLogo}'/>" placeholder="/uploads/brands/ten.png"></div>
        <p class="muted" style="margin:0">Đường dẫn (slug) được tự sinh từ tên thương hiệu.</p>
        <div><button class="btn lime" type="submit">Lưu</button> <a class="btn line" href="${ctx}/wp-admin/thuong-hieu">Hủy</a></div>
    </form>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

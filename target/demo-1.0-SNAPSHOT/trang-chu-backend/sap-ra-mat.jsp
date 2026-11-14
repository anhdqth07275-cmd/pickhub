<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<div class="card coming">
    <div class="big">🚧</div>
    <span class="pill">Sắp ra mắt</span>
    <h2>${tenChucNang}</h2>
    <p class="muted">${moTaChucNang}<br>Chức năng này đang được phát triển và sẽ sớm có mặt trong PickHub.</p>
    <a class="btn lime" href="${ctx}/wp-admin">Về Dashboard</a>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

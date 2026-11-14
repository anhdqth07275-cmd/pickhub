<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-khach/layout/header.jspf" %>
<form class="card login-box" method="post" action="${ctx}/dang-nhap">
    <h1 style="font-size:24px">Đăng nhập</h1>
    <c:if test="${not empty loi}"><div class="alert loi"><c:out value="${loi}"/></div></c:if>
    <input type="hidden" name="quayLai" value="<c:out value='${quayLai}'/>">
    <input type="hidden" name="them" value="<c:out value='${them}'/>">
    <input type="email" name="email" placeholder="Email" value="<c:out value='${email}'/>" required autofocus>
    <input type="password" name="matKhau" placeholder="Mật khẩu" required>
    <button class="btn lime" style="width:100%" type="submit">Đăng nhập</button>
    <p class="muted" style="font-size:13px">Tài khoản mẫu: tuankiet.do@example.com / PickHub@123</p>
</form>
<%@ include file="/trang-chu-khach/layout/footer.jspf" %>

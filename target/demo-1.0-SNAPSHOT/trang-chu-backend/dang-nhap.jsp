<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập - PickHub Admin</title>
    <link rel="stylesheet" href="${ctx}/assets/admin.css">
</head>
<body class="login-page">
<form class="login-box" method="post" action="${ctx}/wp-admin/dang-nhap">
    <div class="logo">PICK<span>HUB</span></div>
    <p class="muted" style="margin-top:0">Đăng nhập khu vực quản trị</p>
    <c:if test="${not empty loi}"><div class="alert loi"><c:out value="${loi}"/></div></c:if>
    <input type="email" name="email" placeholder="Email" value="<c:out value='${email}'/>" required autofocus>
    <input type="password" name="matKhau" placeholder="Mật khẩu" required>
    <button class="btn lime" style="width:100%" type="submit">Đăng nhập</button>
    <p class="muted" style="font-size:13px">Tài khoản mẫu: admin@pickhub.vn / PickHub@123</p>
</form>
</body>
</html>

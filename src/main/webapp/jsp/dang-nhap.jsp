<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập - PickHub POS</title>
    <link rel="stylesheet" href="${ctx}/assets/pos.css">
</head>
<body>
<div class="dang-nhap">
    <form method="post" action="${ctx}/dang-nhap">
        <div class="logo" style="font-size:28px;margin-bottom:6px;color:#1e293b">PICK<span>HUB</span></div>
        <h1 style="font-size:20px">Đăng nhập bán hàng tại quầy</h1>
        <c:if test="${not empty loi}"><div class="loi-box"><c:out value="${loi}"/></div></c:if>
        <input type="email" name="email" placeholder="Email nhân viên" value="<c:out value='${email}'/>" required autofocus>
        <input type="password" name="matKhau" placeholder="Mật khẩu" required>
        <button class="btn lime" type="submit" style="width:100%;padding:13px;font-size:16px">Đăng nhập</button>
        <p class="muted" style="font-size:12.5px;margin-bottom:0">Tài khoản nhân viên do quản trị viên cấp. Không có chức năng tự đăng ký.</p>
    </form>
</div>
</body>
</html>

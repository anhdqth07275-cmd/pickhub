<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- note code nhu nay nhe ban: TRANG DANG NHAP dung giao dien chia doi (login.css + login.js). Cac o name="email" / name="matKhau", method va action GIU NGUYEN, chi doi vo giao dien. --%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập - PickHub</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/theme-head.jspf" %>
    <link rel="stylesheet" href="${ctx}/assets/login.css">
    <script src="${ctx}/assets/login.js" defer></script>
</head>
<body class="pl-page">
<div class="pl-shell">
    <section class="pl-brand">
        <span class="pl-dots" aria-hidden="true"></span>
        <div class="pl-brand-inner">
            <div class="pl-logo">PICK<span>HUB</span></div>
            <h2>Cửa hàng Pickleball</h2>
            <p>Đăng nhập để thêm sản phẩm vào giỏ hàng và đặt hàng.</p>
            <i class="pl-sep" aria-hidden="true"></i>
            <ul class="pl-feats">
                <li><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 4h2l2.4 10.2a2 2 0 002 1.6h7.4a2 2 0 002-1.5L20.5 8H6"/><circle cx="10" cy="20" r="1.4"/><circle cx="17" cy="20" r="1.4"/></svg><span>Giỏ hàng nhanh</span></li>
                <li><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 6h11v10H3zM14 9h4l3 3v4h-7"/><circle cx="7.5" cy="17.5" r="1.6"/><circle cx="17.5" cy="17.5" r="1.6"/></svg><span>Theo dõi đơn hàng</span></li>
                <li><svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8" r="4"/><path d="M4 21c.7-4.5 4-7 8-7s7.3 2.5 8 7"/></svg><span>Tài khoản riêng</span></li>
            </ul>
        </div>
        <svg class="pl-wave" viewBox="0 0 600 150" preserveAspectRatio="none" aria-hidden="true"><path d="M0 70C120 20 240 120 360 80S540 30 600 60V150H0Z"/><path d="M0 105C140 60 260 150 400 110S560 80 600 100V150H0Z"/></svg>
    </section>
    <main class="pl-side">
        <div class="pl-corner" data-ph-switch>
            <a href="${ctx}/cua-hang">Cửa hàng</a>
            <a href="${ctx}/gio-hang">Giỏ hàng</a>
        </div>
        <form class="pl-card" method="post" action="${ctx}/dang-nhap">
        <h1 class="pl-title">Đăng nhập</h1>
        <c:if test="${not empty loi}"><div class="pl-alert pl-bad" role="alert"><c:out value="${loi}"/></div></c:if>
        <input type="hidden" name="quayLai" value="<c:out value='${quayLai}'/>">
        <input type="hidden" name="them" value="<c:out value='${them}'/>">
        <label class="pl-label" for="pl-email">Email</label>
        <div class="pl-field">
            <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="3"/><path d="M3.5 7.5l8.5 6 8.5-6"/></svg>
            <input id="pl-email" type="email" name="email" placeholder="Email" value="<c:out value='${email}'/>" required autofocus>
        </div>
        <label class="pl-label" for="pl-pass">Mật khẩu</label>
        <div class="pl-field">
            <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="4" y="10" width="16" height="10" rx="3"/><path d="M8 10V7a4 4 0 018 0v3"/></svg>
            <input id="pl-pass" type="password" name="matKhau" placeholder="Mật khẩu" required>
            <button class="pl-eye" type="button" data-pl-eye="pl-pass" aria-pressed="false" aria-label="Hiện mật khẩu"><svg class="i-on" viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/></svg><svg class="i-off" viewBox="0 0 24 24" aria-hidden="true"><path d="M3 3l18 18"/><path d="M10.6 5.1A10 10 0 0112 5c6.5 0 10 7 10 7a17 17 0 01-3.2 4.1M6.2 6.7A17 17 0 002 12s3.5 7 10 7a9.7 9.7 0 004.1-.9"/><path d="M9.9 9.9a3 3 0 004.2 4.2"/></svg></button>
        </div>
        <button class="pl-btn" type="submit">Đăng nhập</button>
        <p class="pl-hint">Tài khoản mẫu: tuankiet.do@example.com / PickHub@123</p>
        </form>
    </main>
</div>
</body>
</html>

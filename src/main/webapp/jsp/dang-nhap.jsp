<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- note code nhu nay nhe ban: TRANG DANG NHAP dung giao dien chia doi (login.css + login.js). Cac o name="email" / name="matKhau", method va action GIU NGUYEN, chi doi vo giao dien. --%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập - PickHub POS</title>
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
            <h2>Bán hàng tại quầy</h2>
            <p>Quét mã, tính tiền và in hóa đơn ngay tại quầy.</p>
            <i class="pl-sep" aria-hidden="true"></i>
            <ul class="pl-feats">
                <li><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M13 2L4 14h7l-1 8 9-12h-7l1-8z"/></svg><span>Quét mã nhanh</span></li>
                <li><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 3h12v18l-3-2-3 2-3-2-3 2V3z"/><path d="M9 8h6M9 12h6"/></svg><span>Nhiều hóa đơn cùng lúc</span></li>
                <li><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6l8-3z"/><path d="M9 12l2 2 4-4"/></svg><span>Tài khoản nhân viên</span></li>
            </ul>
        </div>
        <svg class="pl-wave" viewBox="0 0 600 150" preserveAspectRatio="none" aria-hidden="true"><path d="M0 70C120 20 240 120 360 80S540 30 600 60V150H0Z"/><path d="M0 105C140 60 260 150 400 110S560 80 600 100V150H0Z"/></svg>
    </section>
    <main class="pl-side">
        <div class="pl-corner" data-ph-switch></div>
        <form class="pl-card" method="post" action="${ctx}/dang-nhap">
        <h1 class="pl-title">Đăng nhập bán hàng tại quầy</h1>
        <c:if test="${not empty loi}"><div class="pl-alert pl-bad" role="alert"><c:out value="${loi}"/></div></c:if>
        <label class="pl-label" for="pl-email">Email</label>
        <div class="pl-field">
            <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="3"/><path d="M3.5 7.5l8.5 6 8.5-6"/></svg>
            <input id="pl-email" type="email" name="email" placeholder="Email nhân viên" value="<c:out value='${email}'/>" required autofocus>
        </div>
        <label class="pl-label" for="pl-pass">Mật khẩu</label>
        <div class="pl-field">
            <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="4" y="10" width="16" height="10" rx="3"/><path d="M8 10V7a4 4 0 018 0v3"/></svg>
            <input id="pl-pass" type="password" name="matKhau" placeholder="Mật khẩu" required>
            <button class="pl-eye" type="button" data-pl-eye="pl-pass" aria-pressed="false" aria-label="Hiện mật khẩu"><svg class="i-on" viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/></svg><svg class="i-off" viewBox="0 0 24 24" aria-hidden="true"><path d="M3 3l18 18"/><path d="M10.6 5.1A10 10 0 0112 5c6.5 0 10 7 10 7a17 17 0 01-3.2 4.1M6.2 6.7A17 17 0 002 12s3.5 7 10 7a9.7 9.7 0 004.1-.9"/><path d="M9.9 9.9a3 3 0 004.2 4.2"/></svg></button>
        </div>
        <button class="pl-btn" type="submit">Đăng nhập</button>
        <p class="pl-hint">Tài khoản nhân viên do quản trị viên cấp. Không có chức năng tự đăng ký.</p>
        </form>
    </main>
</div>
</body>
</html>

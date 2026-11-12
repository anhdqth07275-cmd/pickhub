<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="vi">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <title>PICKHUB Admin – Quản trị hệ thống</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap"
        rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/trang-chu/assets/css/pickhub-admin.css">
</head>

<body>
    <section id="lgn">
        <div class="box">
            <div class="lg">PICK<i>HUB</i></div>
            <h1>Đăng nhập tài khoản</h1>
            <p class="h">Dùng thử nhanh (mật khẩu 123456):</p>
            <div class="demo"><button data-a="demo" data-i="admin@pickhub.vn">Admin</button><button data-a="demo"
                    data-i="lan@pickhub.vn">Nhân viên</button><button data-a="demo" data-i="an@gmail.com">Khách
                    hàng</button></div>
            <input id="le" type="email" placeholder="Email" autocomplete="username" aria-label="Email"><input id="lp"
                type="password" placeholder="Mật khẩu" autocomplete="current-password" aria-label="Mật khẩu">
            <p class="er" id="ler" role="alert"></p><button class="btn" data-a="login">Đăng nhập</button><button
                class="lk" data-a="reg">Chưa có tài khoản? Đăng ký khách hàng</button>
        </div>
    </section>
    <div id="app" hidden>
        <aside>
            <div class="lg">PICK<i>HUB</i></div>
            <nav id="nv"></nav>
            <div class="me">
                <div class="av" id="av"></div>
                <div><b id="mn"></b><small id="mr"></small><button data-a="out">Đăng xuất</button></div>
            </div>
        </aside>
        <main id="mn2"></main>
    </div>
    <div class="md" id="md">
        <div class="mc" id="mb" role="dialog" aria-modal="true"></div>
    </div>
    <div id="tt" role="status"></div>

    <%-- JavaScript để ở file riêng để JSP không hiểu nhầm template literal của JS là biểu thức EL --%>
    <script src="${pageContext.request.contextPath}/trang-chu/assets/js/pickhub-admin.js"></script>
</body>

</html>

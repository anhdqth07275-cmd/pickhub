<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-frontend/layout.jspf" %>
<section class="auth"><h1>Chào mừng đến PickHub</h1><p>Nền tảng mua sắm dụng cụ Pickleball. Đăng nhập hoặc tạo tài khoản để sử dụng các chức năng dành cho khách hàng.</p><c:choose><c:when test="${not empty sessionScope.khachHang}"><p>Xin chào <b><c:out value="${sessionScope.khachHang.hoTen}"/></b>.</p><a href="${ctx}/tai-khoan/ho-so">Quản lý tài khoản</a></c:when><c:otherwise><p><a href="${ctx}/tai-khoan/dang-nhap">Đăng nhập</a> · <a href="${ctx}/tai-khoan/dang-ky">Đăng ký</a></p></c:otherwise></c:choose><hr><p><a href="${ctx}/wp-admin/dang-nhap">Đăng nhập nhân viên / quản trị</a></p></section>
<%@ include file="/trang-chu-frontend/footer.jspf" %>

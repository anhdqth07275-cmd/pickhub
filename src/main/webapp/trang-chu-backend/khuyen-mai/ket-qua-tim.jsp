<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ph" uri="http://pickhub.vn/functions" %>
<%-- KET QUA TIM SAN PHAM cho popup khuyen mai: chi la cac dong <tr>, KHONG co header/footer cua trang.
     KhuyenMaiTimSanPhamServlet gui sang: dsDong (List<DongKhuyenMaiDTO>). --%>
<c:forEach items="${dsDong}" var="d"><%@ include file="/trang-chu-backend/khuyen-mai/dong-san-pham.jspf" %></c:forEach>

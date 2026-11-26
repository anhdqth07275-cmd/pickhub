<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Lỗi 403</title><meta name="viewport" content="width=device-width, initial-scale=1">
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
<%@ include file="/WEB-INF/theme-head.jspf" %></head>
<%-- note code nhu nay nhe ban: style inline cu duoc doi thanh class .ph-err (xem theme.css) de ho tro che do toi --%>
<body class="ph-err"><h1>403</h1><p>Bạn không có quyền truy cập trang này</p>
<a class="ph-err-btn" href="${ctx}/pos">Về màn hình bán hàng</a></body></html>

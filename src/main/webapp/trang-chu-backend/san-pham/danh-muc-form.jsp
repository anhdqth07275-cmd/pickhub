<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/trang-chu-backend/layout/header.jspf" %>
<a class="back" href="${ctx}/wp-admin/danh-muc">← Danh sách danh mục</a>
<div class="card">
    <c:if test="${not empty loi}"><div class="alert loi"><c:out value="${loi}"/></div></c:if>
    <form class="form-grid" method="post" action="${ctx}/wp-admin/danh-muc">
        <input type="hidden" name="hanhDong" value="luu">
        <input type="hidden" name="id" value="${dm.maDanhMuc}">
        <div><label>Tên danh mục *</label><input type="text" name="tenDanhMuc" maxlength="100" value="<c:out value='${dm.tenDanhMuc}'/>" required></div>
        <div>
            <label>Danh mục cha</label>
            <select name="maDanhMucCha">
                <option value="">— Không có (danh mục gốc) —</option>
                <c:forEach items="${dsCha}" var="c">
                    <option value="${c.maDanhMuc}" ${dm.danhMucCha.maDanhMuc == c.maDanhMuc ? 'selected' : ''}><c:out value="${c.tenDanhMuc}"/></option>
                </c:forEach>
            </select>
        </div>
        <div><label>Mô tả</label><textarea name="moTa" rows="3" maxlength="500"><c:out value="${dm.moTa}"/></textarea></div>
        <p class="muted" style="margin:0">Cây danh mục chỉ có 2 cấp: chỉ chọn được danh mục gốc làm cha.</p>
        <div><button class="btn lime" type="submit">Lưu</button> <a class="btn line" href="${ctx}/wp-admin/danh-muc">Hủy</a></div>
    </form>
</div>
<%@ include file="/trang-chu-backend/layout/footer.jspf" %>

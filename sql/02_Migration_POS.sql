/* =====================================================================
   PICKHUB POS - Script bo sung cho BAN HANG TAI QUAY (chay SAU 01_PickHub_SQLServer.sql, chay lai nhieu lan van an toan)

   Vi sao can sua CSDL: hoa don hien chi co du lieu cho ban online. Ban tai quay can biet
   (1) hoa don thuoc kenh nao, (2) khach tra bang hinh thuc gi, (3) khach dua bao nhieu / thua bao nhieu.
   Script chi THEM cot moi (khong doi / xoa cot cu), hoa don online cu tu dong mang gia tri kenhBan = 'ONLINE'.
   ===================================================================== */
USE PickHub;
GO

-- 1. Them 4 cot moi cho bang HoaDon
IF COL_LENGTH('dbo.HoaDon', 'kenhBan') IS NULL
    ALTER TABLE dbo.HoaDon ADD kenhBan VARCHAR(10) NOT NULL CONSTRAINT DF_HoaDon_kenhBan DEFAULT 'ONLINE';
IF COL_LENGTH('dbo.HoaDon', 'phuongThucThanhToan') IS NULL
    ALTER TABLE dbo.HoaDon ADD phuongThucThanhToan VARCHAR(20) NULL;
IF COL_LENGTH('dbo.HoaDon', 'tienKhachDua') IS NULL
    ALTER TABLE dbo.HoaDon ADD tienKhachDua DECIMAL(12,0) NULL;
IF COL_LENGTH('dbo.HoaDon', 'tienThua') IS NULL
    ALTER TABLE dbo.HoaDon ADD tienThua DECIMAL(12,0) NULL;
GO

-- 2. Rang buoc du lieu (nam sau GO de cot da ton tai)
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_HoaDon_kenhBan')
    ALTER TABLE dbo.HoaDon ADD CONSTRAINT CK_HoaDon_kenhBan CHECK (kenhBan IN ('ONLINE', 'TAI_QUAY'));
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_HoaDon_phuongThucThanhToan')
    ALTER TABLE dbo.HoaDon ADD CONSTRAINT CK_HoaDon_phuongThucThanhToan CHECK (phuongThucThanhToan IS NULL OR phuongThucThanhToan IN ('TIEN_MAT', 'CHUYEN_KHOAN', 'THE'));
-- hoa don tai quay bat buoc co du thong tin thanh toan va tien thua = tien khach dua - tong tien
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_HoaDon_thanhToanTaiQuay')
    ALTER TABLE dbo.HoaDon ADD CONSTRAINT CK_HoaDon_thanhToanTaiQuay CHECK (
        kenhBan <> 'TAI_QUAY'
        OR (phuongThucThanhToan IS NOT NULL AND tienKhachDua IS NOT NULL AND tienThua IS NOT NULL
            AND tienKhachDua >= tongTien AND tienThua = tienKhachDua - tongTien));
GO

-- 3. Chi muc tang toc man hinh "Hoa don da ban"
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_HoaDon_kenhBan_ngayDatHang')
    CREATE INDEX IX_HoaDon_kenhBan_ngayDatHang ON dbo.HoaDon (kenhBan, ngayDatHang);
GO

-- 4. Tai khoan he thong "Khach le tai quay": moi hoa don tai quay gan vao khach nay (HoaDon.maKhachHang bat buoc co).
--    Dat trang thai KHOA nen khong the dang nhap. Phone 0000000000 chi la gia tri giu cho.
IF NOT EXISTS (SELECT 1 FROM dbo.KhachHang WHERE thuDienTu = 'khachle@pickhub.local')
    INSERT INTO dbo.KhachHang (hoTen, thuDienTu, soDienThoai, matKhauMaHoa, trangThai, lyDoKhoa, ngayKhoa)
    VALUES (N'Khách lẻ tại quầy', 'khachle@pickhub.local', '0000000000', N'$2b$10$4a/amJFDiKl.iPD6q2MyButgbTNzvKa93Di6PVcf68BZMGuJK.kxu', 'KHOA', N'Tài khoản hệ thống dùng cho bán tại quầy', SYSDATETIME());
GO

PRINT N'PICKHUB POS: da bo sung cot cho HoaDon va tao khach le.';
GO

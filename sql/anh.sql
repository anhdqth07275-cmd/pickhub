

DELETE FROM HinhAnhSanPham;
DBCC CHECKIDENT ('HinhAnhSanPham', RESEED, 0);

select * from HinhAnhSanPham
INSERT INTO HinhAnhSanPham (maSanPham, maChiTietSanPham, duongDanAnh, loaiTep, dungLuongKb, laAnhChinh, thuTuSapXep)
VALUES
-- ==================== SP001: Vợt Selkirk Vanguard Power Air ====================
-- Ảnh chung
(1, NULL, '/assets/products/SP001/common/main.png', 'PNG', 21, 1, 1),
(1, NULL, '/assets/products/SP001/common/gallery/01-detail.png', 'PNG', 21, 0, 2),
(1, NULL, '/assets/products/SP001/common/gallery/02-detail.png', 'PNG', 93, 0, 3),
-- VAR001 -> Màu xanh BL (maChiTietSanPham = 1)
(1, 1, '/assets/products/SP001/variants/VAR001/main.png', 'PNG', 10, 0, 4),
(1, 1, '/assets/products/SP001/variants/VAR001/gallery/01-detail.png', 'PNG', 10, 0, 5),
(1, 1, '/assets/products/SP001/variants/VAR001/gallery/02-detail.png', 'PNG', 11, 0, 6),
-- VAR002 -> Màu đen BK (gắn cho cả 2 biến thể màu đen: maChiTietSanPham = 2 và 3)
(1, 2, '/assets/products/SP001/variants/VAR002/main.png', 'PNG', 15, 0, 7),
(1, 2, '/assets/products/SP001/variants/VAR002/gallery/01-detail.png', 'PNG', 15, 0, 8),
(1, 2, '/assets/products/SP001/variants/VAR002/gallery/02-detail.png', 'PNG', 14, 0, 9),
(1, 3, '/assets/products/SP001/variants/VAR002/main.png', 'PNG', 15, 0, 10),
(1, 3, '/assets/products/SP001/variants/VAR002/gallery/01-detail.png', 'PNG', 15, 0, 11),
(1, 3, '/assets/products/SP001/variants/VAR002/gallery/02-detail.png', 'PNG', 14, 0, 12),

-- ==================== SP002: Vợt JOOLA Perseus 3 16mm ====================
(2, NULL, '/assets/products/SP002/common/main.png', 'PNG', 5, 1, 1),
(2, NULL, '/assets/products/SP002/common/gallery/01-detail.png', 'PNG', 5, 0, 2),
(2, NULL, '/assets/products/SP002/common/gallery/02-detail.png', 'PNG', 5, 0, 3),

-- ==================== SP003: Vợt CRBN 1X Power Series ====================
(3, NULL, '/assets/products/SP003/common/main.png', 'PNG', 8, 1, 1),
(3, NULL, '/assets/products/SP003/common/gallery/01-detail.png', 'PNG', 8, 0, 2),
(3, NULL, '/assets/products/SP003/common/gallery/02-detail.png', 'PNG', 12, 0, 3),
-- VAR001 -> Màu đen BK (maChiTietSanPham = 7)
(3, 7, '/assets/products/SP003/variants/VAR001/main.png', 'PNG', 7, 0, 4),
(3, 7, '/assets/products/SP003/variants/VAR001/gallery/01-detail.png', 'PNG', 7, 0, 5),
(3, 7, '/assets/products/SP003/variants/VAR001/gallery/02-detail.png', 'PNG', 123, 0, 6),

-- ==================== SP004: Vợt Paddletek Bantam TS-5 Pro ====================
(4, NULL, '/assets/products/SP004/common/main.png', 'PNG', 11, 1, 1),
(4, NULL, '/assets/products/SP004/common/gallery/01-detail.png', 'PNG', 11, 0, 2),
-- VAR001 -> Màu đỏ RD (maChiTietSanPham = 10)
(4, 10, '/assets/products/SP004/variants/VAR001/main.png', 'PNG', 11, 0, 3),
(4, 10, '/assets/products/SP004/variants/VAR001/gallery/01-detail.png', 'PNG', 11, 0, 4),
-- VAR002 -> Màu xanh BL (maChiTietSanPham = 11)
(4, 11, '/assets/products/SP004/variants/VAR002/main.png', 'PNG', 8, 0, 5),
(4, 11, '/assets/products/SP004/variants/VAR002/gallery/01-detail.png', 'PNG', 8, 0, 6),

-- ==================== SP005: Vợt Engage Pursuit MX 6.0 ====================
(5, NULL, '/assets/products/SP005/common/main.png', 'PNG', 9, 1, 1),
(5, NULL, '/assets/products/SP005/common/gallery/01-detail.png', 'PNG', 9, 0, 2),
-- VAR001 -> Màu đen BK (maChiTietSanPham = 12)
(5, 12, '/assets/products/SP005/variants/VAR001/main.png', 'PNG', 10, 0, 3),
(5, 12, '/assets/products/SP005/variants/VAR001/gallery/01-detail.png', 'PNG', 10, 0, 4),
-- VAR002 -> Màu bạc SV (maChiTietSanPham = 13)
(5, 13, '/assets/products/SP005/variants/VAR002/main.png', 'PNG', 9, 0, 5),
(5, 13, '/assets/products/SP005/variants/VAR002/gallery/01-detail.png', 'PNG', 9, 0, 6),
-- VAR003 -> Màu cam OR (maChiTietSanPham = 14)
(5, 14, '/assets/products/SP005/variants/VAR003/main.png', 'PNG', 184, 0, 7),
(5, 14, '/assets/products/SP005/variants/VAR003/gallery/01-detail.png', 'PNG', 184, 0, 8),

-- ==================== SP006: Vợt Onix Z5 Graphite ====================
(6, NULL, '/assets/products/SP006/common/main.png', 'PNG', 65, 1, 1),
(6, NULL, '/assets/products/SP006/common/gallery/01-detail.png', 'PNG', 65, 0, 2),
(6, NULL, '/assets/products/SP006/common/gallery/02-detail.png', 'PNG', 53, 0, 3),
-- VAR001 -> Màu xanh lá GR (maChiTietSanPham = 15)
(6, 15, '/assets/products/SP006/variants/VAR001/main.png', 'PNG', 38, 0, 4),
(6, 15, '/assets/products/SP006/variants/VAR001/gallery/01-detail.png', 'PNG', 38, 0, 5),
(6, 15, '/assets/products/SP006/variants/VAR001/gallery/02-detail.png', 'PNG', 31, 0, 6),
-- VAR002 -> Màu đỏ RD (maChiTietSanPham = 16)
(6, 16, '/assets/products/SP006/variants/VAR002/main.png', 'PNG', 38, 0, 7),
(6, 16, '/assets/products/SP006/variants/VAR002/gallery/01-detail.png', 'PNG', 38, 0, 8),
(6, 16, '/assets/products/SP006/variants/VAR002/gallery/02-detail.png', 'PNG', 31, 0, 9),

-- ==================== SP007: Vợt Gearbox Pro Power Elongated ====================
(7, NULL, '/assets/products/SP007/common/main.png', 'PNG', 17, 1, 1),
(7, NULL, '/assets/products/SP007/common/gallery/01-detail.png', 'PNG', 17, 0, 2),
(7, NULL, '/assets/products/SP007/common/gallery/02-detail.png', 'PNG', 17, 0, 3),
-- VAR001 -> Màu đen BK (maChiTietSanPham = 17)
(7, 17, '/assets/products/SP007/variants/VAR001/main.png', 'PNG', 13, 0, 4),
(7, 17, '/assets/products/SP007/variants/VAR001/gallery/01-detail.png', 'PNG', 13, 0, 5),
(7, 17, '/assets/products/SP007/variants/VAR001/gallery/02-detail.png', 'PNG', 23, 0, 6),

-- ==================== SP008: Bóng thi đấu Franklin X-40 Outdoor ====================
(8, NULL, '/assets/products/SP008/common/main.png', 'PNG', 28, 1, 1),
(8, NULL, '/assets/products/SP008/common/gallery/01-detail.png', 'PNG', 28, 0, 2),
(8, NULL, '/assets/products/SP008/common/gallery/02-detail.png', 'PNG', 20, 0, 3),

-- ==================== SP009: Bóng tập Onix Fuse Indoor ====================
(9, NULL, '/assets/products/SP009/common/main.png', 'PNG', 63, 1, 1),

(9, NULL, '/assets/products/SP009/common/gallery/01-detail.png', 'PNG', 63, 0, 2),
(9, NULL, '/assets/products/SP009/common/gallery/02-detail.png', 'PNG', 7, 0, 3),

-- ==================== SP010: Quấn cán Vulcan V100 Cushion ====================
(10, NULL, '/assets/products/SP010/common/main.png', 'PNG', 278, 1, 1),
(10, NULL, '/assets/products/SP010/common/gallery/01-detail.png', 'PNG', 278, 0, 2),
-- VAR001 -> Màu đen BK (maChiTietSanPham = 24)
(10, 24, '/assets/products/SP010/variants/VAR001/main.png', 'PNG', 4, 0, 3),
(10, 24, '/assets/products/SP010/variants/VAR001/gallery/01-detail.png', 'PNG', 4, 0, 4),

-- ==================== SP011: Lưới Pickleball di động Franklin 22ft ====================
(11, NULL, '/assets/products/SP011/common/main.png', 'PNG', 14, 1, 1),
(11, NULL, '/assets/products/SP011/common/gallery/01-detail.png', 'PNG', 14, 0, 2),
(11, NULL, '/assets/products/SP011/common/gallery/02-detail.png', 'PNG', 12, 0, 3),

-- ==================== SP012: Túi vợt Head Tour Team ====================
(12, NULL, '/assets/products/SP012/common/main.png', 'PNG', 170, 1, 1),
(12, NULL, '/assets/products/SP012/common/gallery/01-detail.png', 'PNG', 170, 0, 2),
-- VAR001 -> Màu đen BK (maChiTietSanPham = 28)
(12, 28, '/assets/products/SP012/variants/VAR001/main.png', 'PNG', 13, 0, 3),
(12, 28, '/assets/products/SP012/variants/VAR001/gallery/01-detail.png', 'PNG', 13, 0, 4),
(12, 28, '/assets/products/SP012/variants/VAR001/gallery/02-detail.png', 'PNG', 329, 0, 5),
-- VAR002 -> Màu xanh BL (maChiTietSanPham = 29)
(12, 29, '/assets/products/SP012/variants/VAR002/main.png', 'PNG', 40, 0, 6),
(12, 29, '/assets/products/SP012/variants/VAR002/gallery/01-detail.png', 'PNG', 40, 0, 7),
(12, 29, '/assets/products/SP012/variants/VAR002/gallery/02-detail.png', 'PNG', 9, 0, 8);
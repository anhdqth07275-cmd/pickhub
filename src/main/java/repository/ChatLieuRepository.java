package repository;

import entity.ChatLieu;

import java.util.List;

/**
 * Repository cua bang ChatLieu. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class ChatLieuRepository extends BaseRepository<ChatLieu> {

    public ChatLieuRepository() {
        super(ChatLieu.class, "maChatLieu");
    }

    // note code nhu nay nhe ban: chi lay muc HOAT_DONG de do vao dropdown form san pham (muc NGUNG khong cho chon nua)
    public List<ChatLieu> layDangHoatDong() {
        return layCoDieuKien("x.trangThai = 'HOAT_DONG'", "x.tenChatLieu asc", 1000);
    }
}

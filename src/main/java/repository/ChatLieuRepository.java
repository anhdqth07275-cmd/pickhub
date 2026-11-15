package repository;

import entity.ChatLieu;


/**
 * Repository cua bang ChatLieu. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class ChatLieuRepository extends BaseRepository<ChatLieu> {

    public ChatLieuRepository() {
        super(ChatLieu.class, "maChatLieu");
    }
}

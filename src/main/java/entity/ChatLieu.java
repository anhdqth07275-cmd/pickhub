package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ChatLieu")
@Getter
@Setter
@NoArgsConstructor
public class ChatLieu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChatLieu;

    private String tenChatLieu;

    private String trangThai;
}

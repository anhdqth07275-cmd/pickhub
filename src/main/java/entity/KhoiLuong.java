package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "KhoiLuong")
@Getter
@Setter
@NoArgsConstructor
public class KhoiLuong extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKhoiLuong;

    private Integer khoiLuongGram;

    private String trangThai;
}

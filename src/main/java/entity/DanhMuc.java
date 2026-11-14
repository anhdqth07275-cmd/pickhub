package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "DanhMuc")
@Getter
@Setter
@NoArgsConstructor
public class DanhMuc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDanhMuc;

    @ManyToOne
    @JoinColumn(name = "maDanhMucCha")
    private DanhMuc danhMucCha;

    private String tenDanhMuc;

    private String moTa;

    private String trangThai;
}

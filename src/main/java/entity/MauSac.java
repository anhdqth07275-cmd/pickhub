package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "MauSac")
@Getter
@Setter
@NoArgsConstructor
public class MauSac extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maMauSac;

    private String tenMauSac;

    private String trangThai;
}

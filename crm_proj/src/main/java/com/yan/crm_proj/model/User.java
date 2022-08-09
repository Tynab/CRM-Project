package com.yan.crm_proj.model;

import javax.persistence.*;

import lombok.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Entity(name = "nguoi_dung")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "mat_khau")
    private String password;

    @Column(name = "ho_ten")
    private String name;

    @Column(name = "dia_chi")
    private String address;

    @Column(name = "so_dien_thoai")
    private String phone;

    @NonNull
    @JoinColumn(name = "id_loai_thanh_vien", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = EAGER)
    private Role role;
}

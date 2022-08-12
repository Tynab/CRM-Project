package com.yan.crm_proj.model;

import java.util.*;

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

    @NonNull
    @Column(name = "ho_ten")
    private String name;

    @NonNull
    @Column(name = "hinh_anh")
    private String image;

    @Column(name = "dia_chi")
    private String address;

    @Column(name = "so_dien_thoai")
    private String phone;

    @Column(name = "id_loai_thanh_vien")
    private int roleId;

    @JoinColumn(name = "id_loai_thanh_vien", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = EAGER)
    private Role role;

    @OneToMany(mappedBy = "doer")
    private Set<Task> tasks;
}

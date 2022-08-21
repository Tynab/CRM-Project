package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;

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
    @ManyToOne(fetch = LAZY)
    private Role role;

    @OneToMany(mappedBy = "originator", fetch = LAZY)
    private List<Project> projects;

    @OneToMany(mappedBy = "doer", fetch = LAZY)
    private List<Task> tasks;

    @OneToMany(mappedBy = "doer", fetch = EAGER)
    @Where(clause = "id_trang_thai_cong_viec = 1")
    private List<Task> tasksNotStarted;

    @OneToMany(mappedBy = "doer", fetch = EAGER)
    @Where(clause = "id_trang_thai_cong_viec = 2")
    private List<Task> tasksInProgress;

    @OneToMany(mappedBy = "doer", fetch = EAGER)
    @Where(clause = "id_trang_thai_cong_viec = 3")
    private List<Task> tasksCompleted;
}

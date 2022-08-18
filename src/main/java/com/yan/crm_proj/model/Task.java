package com.yan.crm_proj.model;

import java.util.*;

import javax.persistence.*;

import org.springframework.format.annotation.*;

import lombok.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Entity(name = "cong_viec")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "ten")
    private String name;

    @Column(name = "mo_ta")
    private String description;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_bat_dau")
    private Date startDate;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_ket_thuc")
    private Date endDate;

    @Column(name = "id_nguoi_thuc_hien")
    private int doerId;

    @JoinColumn(name = "id_nguoi_thuc_hien", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne
    private User doer;

    @Column(name = "id_du_an")
    private int projectId;

    @JoinColumn(name = "id_du_an", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = EAGER)
    private Project project;

    @Column(name = "id_trang_thai_cong_viec")
    private int statusId;

    @JoinColumn(name = "id_trang_thai_cong_viec", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY)
    private TaskStatus status;
}

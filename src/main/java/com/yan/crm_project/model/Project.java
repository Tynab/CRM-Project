package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;

import org.springframework.format.annotation.*;

import lombok.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Entity(name = "du_an")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
/**
 * Tom tat: Entity du_an, gom thong tin thoi gian, nguoi tao va danh sach cong
 * viec thuoc du an.
 */
public class Project {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "id")
    private int id;

    @NonNull
    @ToString.Include
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

    @Column(name = "id_nguoi_tao")
    private int originatorId;

    @JoinColumn(name = "id_nguoi_tao", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY)
    private User originator;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;
}

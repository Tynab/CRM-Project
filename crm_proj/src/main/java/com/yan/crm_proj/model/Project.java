package com.yan.crm_proj.model;

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
public class Project {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ten")
    private String name;

    @Column(name = "mo_ta")
    private String description;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "ngay_bat_dau")
    private Date startDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "ngay_ket_thuc")
    private Date endDate;

    @NonNull
    @JoinColumn(name = "id_nguoi_tao", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = EAGER)
    private User originator;
}

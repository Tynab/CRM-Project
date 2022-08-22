package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

import static javax.persistence.GenerationType.*;

@Entity(name = "loai_thanh_vien")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "ten")
    private String name;

    @Column(name = "mo_ta")
    private String description;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}

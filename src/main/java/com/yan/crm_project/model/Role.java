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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
/**
 * Tom tat: Entity loai_thanh_vien, luu vai tro phan quyen cua nguoi dung.
 */
public class Role {
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

    @OneToMany(mappedBy = "role")
    private List<User> users;
}

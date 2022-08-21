package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Entity(name = "trang_thai_cong_viec")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "ten")
    private String name;

    @OneToMany(mappedBy = "status", fetch = LAZY)
    private Set<Task> tasks;
}

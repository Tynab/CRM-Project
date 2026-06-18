package com.yan.crm_project.model;

import javax.persistence.*;

import lombok.*;

import static javax.persistence.GenerationType.*;

@Entity(name = "trang_thai_cong_viec")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
/**
 * Tom tat: Entity trang_thai_cong_viec, danh muc trang thai cho task.
 */
public class TaskStatus {
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
}

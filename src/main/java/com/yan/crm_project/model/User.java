package com.yan.crm_project.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Entity(name = "nguoi_dung")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
/**
 * Tom tat: Entity nguoi_dung, dai dien tai khoan dang nhap va cac quan he role,
 * du an tao, cong viec duoc giao.
 */
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "id")
    private int id;

    @NonNull
    @ToString.Include
    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "mat_khau")
    private String password;

    @NonNull
    @ToString.Include
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

    @OneToMany(mappedBy = "originator")
    private List<Project> projects;

    @OneToMany(mappedBy = "doer")
    private List<Task> tasks;

    /**
     * Lay cac cong viec chua bat dau cua user.
     */
    public List<Task> getTasksNotStarted() {
        return Optional.ofNullable(tasks).orElseGet(Collections::emptyList).stream()
                .filter(task -> task.getStatus() != null && task.getStatus().getId() == NOT_STARTED).collect(toList());
    }

    /**
     * Lay cac cong viec dang thuc hien cua user.
     */
    public List<Task> getTasksInProgress() {
        return Optional.ofNullable(tasks).orElseGet(Collections::emptyList).stream()
                .filter(task -> task.getStatus() != null && task.getStatus().getId() == IN_PROGRESS).collect(toList());
    }

    /**
     * Lay cac cong viec da hoan thanh cua user.
     */
    public List<Task> getTasksCompleted() {
        return Optional.ofNullable(tasks).orElseGet(Collections::emptyList).stream()
                .filter(task -> task.getStatus() != null && task.getStatus().getId() == COMPLETED).collect(toList());
    }
}

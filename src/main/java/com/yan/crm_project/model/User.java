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

    @OneToMany(mappedBy = "originator")
    private List<Project> projects;

    @OneToMany(mappedBy = "doer")
    private List<Task> tasks;

    // Get all tasks with status not started
    public List<Task> getTasksNotStarted() {
        return tasks.stream().filter(task -> task.getStatus().getId() == NOT_STARTED).collect(toList());
    }

    // Get all tasks with status in progress
    public List<Task> getTasksInProgress() {
        return tasks.stream().filter(task -> task.getStatus().getId() == IN_PROGRESS).collect(toList());
    }

    // Get all tasks with status completed
    public List<Task> getTasksCompleted() {
        return tasks.stream().filter(task -> task.getStatus().getId() == COMPLETED).collect(toList());
    }
}

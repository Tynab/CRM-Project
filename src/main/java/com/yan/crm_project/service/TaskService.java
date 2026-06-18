package com.yan.crm_project.service;

import java.util.*;

import com.yan.crm_project.model.*;

/**
 * Tom tat: Khai bao nghiep vu CRUD cong viec.
 */
public interface TaskService {
    List<Task> getTasks();

    Task getTask(int id);

    Task saveTask(Task task);

    void deleteTask(int id);
}

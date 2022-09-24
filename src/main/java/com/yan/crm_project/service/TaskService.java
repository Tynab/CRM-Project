package com.yan.crm_project.service;

import java.util.*;

import com.yan.crm_project.model.*;

public interface TaskService {
    public List<Task> getTasks();

    public Task getTask(int id);

    public Task saveTask(Task task);

    public void deleteTask(int id);
}

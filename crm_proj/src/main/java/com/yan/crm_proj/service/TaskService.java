package com.yan.crm_proj.service;

import com.yan.crm_proj.model.*;

public interface TaskService {
    public Iterable<Task> getTasks();

    public Task getTask(int id);

    public Task saveTask(Task task);

    public void deleteTask(int id);
}

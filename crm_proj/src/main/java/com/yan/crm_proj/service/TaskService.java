package com.yan.crm_proj.service;

import java.util.*;

import com.yan.crm_proj.model.*;

public interface TaskService {
    public List<Task> getTasks();

    public Task getTask(int id);

    public Task saveTask(Task task);

    public void deleteTask(int id);

    public List<Task> getTasksByDoer(String doerEmail);

    public List<Task> getTasksByStatus(String statusName);

    public List<Task> getTasksByDoerAndStatus(String doerEmail, String statusName);
}

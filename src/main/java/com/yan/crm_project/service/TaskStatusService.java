package com.yan.crm_project.service;

import com.yan.crm_project.model.*;

public interface TaskStatusService {
    public Iterable<TaskStatus> getTaskStatuses();

    public TaskStatus getTaskStatus(int id);

    public TaskStatus getTaskStatus(String name);

    public TaskStatus saveTaskStatus(TaskStatus taskStatus);

    public void deleteTaskStatus(int id);
}

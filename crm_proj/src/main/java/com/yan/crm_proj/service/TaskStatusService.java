package com.yan.crm_proj.service;

import com.yan.crm_proj.model.*;

public interface TaskStatusService {
    public Iterable<TaskStatus> getTaskStatuses();

    public TaskStatus getTaskStatus(String name);

    public TaskStatus saveTaskStatus(TaskStatus taskStatus);

    public void deleteTaskStatus(int id);
}

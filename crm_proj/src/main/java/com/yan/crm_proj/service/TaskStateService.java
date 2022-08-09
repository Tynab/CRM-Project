package com.yan.crm_proj.service;

import com.yan.crm_proj.model.*;

public interface TaskStateService {
    public Iterable<TaskState> getTaskStates();

    public TaskState getTaskState(int id);

    public TaskState saveTaskState(TaskState taskState);

    public void deleteTaskState(int id);
}

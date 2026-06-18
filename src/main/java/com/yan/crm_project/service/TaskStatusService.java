package com.yan.crm_project.service;

import com.yan.crm_project.model.*;

/**
 * Tom tat: Doc danh muc trang thai cong viec cho form cap nhat.
 */
public interface TaskStatusService {
    Iterable<TaskStatus> getTaskStatuses();
}

package com.yan.crm_project.util;

import java.util.*;

import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

import static java.util.stream.Collectors.*;

@Component
public class ApplicationUtil {
    // Get all tasks with status not started
    public List<Task> splitTasksByStatus(List<Task> tasks, int statusId) {
        return tasks.stream().filter(task -> task.getStatus().getId() == statusId).collect(toList());
    }
}

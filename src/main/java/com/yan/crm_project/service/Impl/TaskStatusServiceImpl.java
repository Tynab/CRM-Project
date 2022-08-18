package com.yan.crm_project.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.repository.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.extern.slf4j.*;

@Service
@Transactional
@Slf4j
public class TaskStatusServiceImpl implements TaskStatusService {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private StringUtil stringUtil;

    @Override
    public Iterable<TaskStatus> getTaskStatuses() {
        log.info("Fetching all task statuses");
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus getTaskStatus(int id) {
        log.info("Fetching task status with id {}", id);
        return taskStatusRepository.findById(id).orElse(null);
    }

    @Override
    public TaskStatus getTaskStatus(String name) {
        log.info("Fetching task status with name {}", name);
        return taskStatusRepository.findByName(name);
    }

    @Override
    public TaskStatus saveTaskStatus(TaskStatus taskStatus) {
        taskStatus.setName(stringUtil.sentenceCase(stringUtil.removeNumAndWhiteSpaceBeginAndEnd(taskStatus.getName())));
        log.info("Saving task status with name: {}", taskStatus.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void deleteTaskStatus(int id) {
        log.info("Deleting task status with id: {}", id);
        taskStatusRepository.deleteById(id);
    }
}

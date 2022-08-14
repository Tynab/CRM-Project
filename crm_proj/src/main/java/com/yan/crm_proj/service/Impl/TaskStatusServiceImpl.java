package com.yan.crm_proj.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.repository.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    @Autowired
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    private final StringUtil stringUtil;

    @Override
    public Iterable<TaskStatus> getTaskStatuses() {
        log.info("Fetching all task statuses");
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus getTaskStatus(String name) {
        log.info("Fetching status with name: {}", name);
        return taskStatusRepository.findByName(name);
    }

    @Override
    public TaskStatus saveTaskStatus(TaskStatus taskStatus) {
        taskStatus.setName(stringUtil.titleCase(stringUtil.removeSpCharsBeginAndEnd(taskStatus.getName())));
        log.info("Saving task status with name: {}", taskStatus.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void deleteTaskStatus(int id) {
        log.info("Deleting task status with id: {}", id);
        taskStatusRepository.deleteById(id);
    }
}

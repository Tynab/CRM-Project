package com.yan.crm_project.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.repository.*;
import com.yan.crm_project.service.*;

import lombok.extern.slf4j.*;

@Service
@Transactional
@Slf4j
public class TaskStatusServiceImpl implements TaskStatusService {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Override
    public Iterable<TaskStatus> getTaskStatuses() {
        log.info("Fetching all task statuses");
        return taskStatusRepository.findAll();
    }
}

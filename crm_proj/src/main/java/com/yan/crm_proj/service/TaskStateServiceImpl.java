package com.yan.crm_proj.service;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.repository.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskStateServiceImpl implements TaskStateService {
    @Autowired
    private final TaskStateRepository taskStateRepository;

    @Override
    public Iterable<TaskState> getTaskStates() {
        log.info("Fetching all task states");
        return taskStateRepository.findAll();
    }

    @Override
    public TaskState getTaskState(int id) {
        log.info("Fetching task state with id: {}", id);
        return taskStateRepository.findById(id).orElse(null);
    }

    @Override
    public TaskState saveTaskState(TaskState taskState) {
        log.info("Saving task state with id: {}", taskState.getId());
        return taskStateRepository.save(taskState);
    }

    @Override
    public void deleteTaskState(int id) {
        log.info("Deleting task state with id: {}", id);
        taskStateRepository.deleteById(id);
    }
}

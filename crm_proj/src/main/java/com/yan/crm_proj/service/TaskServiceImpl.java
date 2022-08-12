package com.yan.crm_proj.service;

import java.util.*;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.repository.*;
import com.yan.crm_proj.specification.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private final TaskRepository taskRepository;

    @Override
    public Iterable<Task> getTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll();
    }

    @Override
    public Task getTask(int id) {
        log.info("Fetching task with id: {}", id);
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task saveTask(Task task) {
        log.info("Saving task with id: {}", task.getId());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getTasksByDoer(int doerId) {
        log.info("Fetching tasks by doer: {}", doerId);
        return taskRepository.findAllByDoerId(doerId);
    }

    @Override
    public List<Task> getTasksByDoerAndTaskStatus(int doerId, String taskStatusName) {
        log.info("Fetching tasks by doer: {} and task status: {}", doerId, taskStatusName);
        return taskRepository.findAll(Specification.where(TaskSpecification.findByDoerIdAndTaskStatusName(doerId, taskStatusName)));
    }
}

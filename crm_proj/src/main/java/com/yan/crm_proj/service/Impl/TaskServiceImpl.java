package com.yan.crm_proj.service.Impl;

import java.util.*;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.repository.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.*;
import lombok.extern.slf4j.*;

import static com.yan.crm_proj.spec.TaskSpec.*;
import static org.springframework.data.jpa.domain.Specification.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final StringUtil stringUtil;

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
        task.setName(stringUtil.titleCase(stringUtil.removeSpCharsBeginAndEnd(task.getName())));
        task.setDescription(stringUtil.sentenceCase(stringUtil.removeNumAndWhiteSpaceBeginAndEnd(task.getDescription())));
        log.info("Saving task with name: {}", task.getName());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getTasksByDoer(String email) {
        log.info("Fetching tasks by doer: {}", email);
        return taskRepository.findAllByDoerEmail(email);
    }

    @Override
    public List<Task> getTasksByDoerAndTaskStatus(String email, String taskStatusName) {
        log.info("Fetching tasks by doer: {} and task status: {}", email, taskStatusName);
        return taskRepository.findAll(where(findByDoerEmailAndTaskStatusName(email, taskStatusName)));
    }
}

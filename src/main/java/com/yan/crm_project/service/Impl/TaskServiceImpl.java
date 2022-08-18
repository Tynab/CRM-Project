package com.yan.crm_project.service.Impl;

import java.util.*;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.repository.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.extern.slf4j.*;

import static org.springframework.util.StringUtils.*;

@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private TextUtil textUtil;

    @Override
    public List<Task> getTasks() {
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
        task.setName(capitalize(stringUtil.removeSpCharsBeginAndEnd(task.getName())));
        task.setDescription(textUtil.parseToLegalText(task.getDescription()));
        log.info("Saving task with name: {}", task.getName());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getTasksByDoer(String doerEmail) {
        log.info("Fetching tasks by doer: {}", doerEmail);
        return taskRepository.findAllByDoerEmail(doerEmail);
    }

    @Override
    public List<Task> getTasksByStatus(String statusName) {
        log.info("Fetching tasks by task status: {}", statusName);
        return taskRepository.findAllByStatusName(statusName);
    }

    @Override
    public List<Task> getTasksByDoerAndStatus(String doerEmail, String statusName) {
        log.info("Fetching tasks by doer: {} and task status: {}", doerEmail, statusName);
        return taskRepository.findAllByDoerEmailAndStatusName(doerEmail, statusName);
    }
}

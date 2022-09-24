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
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TextUtil textUtil;

    @Override
    public Iterable<Project> getProjects() {
        log.info("Fetching all projects");
        return projectRepository.findAll();
    }

    @Override
    public Project getProject(int id) {
        log.info("Fetching project with id: {}", id);
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public Project saveProject(Project project) {
        project.setName(textUtil.parseToLegalText(project.getName()));
        project.setDescription(textUtil.parseToLegalText(project.getDescription()));
        log.info("Saving project with name: {}", project.getName());
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(int id) {
        log.info("Deleting project with id: {}", id);
        projectRepository.deleteById(id);
    }
}

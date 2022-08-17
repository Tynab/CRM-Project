package com.yan.crm_proj.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.repository.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.extern.slf4j.*;

import static org.springframework.util.StringUtils.*;

@Service
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StringUtil stringUtil;

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
        project.setName(capitalize(stringUtil.removeSpCharsBeginAndEnd(project.getName())));
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

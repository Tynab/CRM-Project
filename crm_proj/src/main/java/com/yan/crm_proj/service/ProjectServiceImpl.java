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
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private final ProjectRepository projectRepository;

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
        log.info("Saving project with id: {}", project.getId());
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(int id) {
        log.info("Deleting project with id: {}", id);
        projectRepository.deleteById(id);
    }
}

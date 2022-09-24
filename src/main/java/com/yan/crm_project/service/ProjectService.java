package com.yan.crm_project.service;

import com.yan.crm_project.model.*;

public interface ProjectService {
    public Iterable<Project> getProjects();

    public Project getProject(int id);

    public Project saveProject(Project project);

    public void deleteProject(int id);
}

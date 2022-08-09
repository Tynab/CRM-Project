package com.yan.crm_proj.service;

import com.yan.crm_proj.model.*;

public interface ProjectService {
    public Iterable<Project> getProjects();

    public Project getProject(int id);

    public Project saveProject(Project project);

    public void deleteProject(int id);
}

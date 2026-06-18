package com.yan.crm_project.service;

import com.yan.crm_project.model.*;

/**
 * Tom tat: Khai bao nghiep vu CRUD du an.
 */
public interface ProjectService {
    Iterable<Project> getProjects();

    Project getProject(int id);

    Project saveProject(Project project);

    void deleteProject(int id);
}

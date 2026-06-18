package com.yan.crm_project.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
/**
 * Tom tat: Repository JPA cho bang du_an.
 */
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}

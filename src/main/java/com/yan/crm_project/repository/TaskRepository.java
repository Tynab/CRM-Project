package com.yan.crm_project.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task> {
}

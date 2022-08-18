package com.yan.crm_project.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task> {
    public List<Task> findAllByDoerEmail(String doerEmail);

    public List<Task> findAllByStatusName(String statusName);

    public List<Task> findAllByDoerEmailAndStatusName(String doerEmail, String statusName);
}

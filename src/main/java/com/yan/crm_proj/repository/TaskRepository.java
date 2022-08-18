package com.yan.crm_proj.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task> {
    public List<Task> findAllByDoerEmail(String doerEmail);

    public List<Task> findAllByStatusName(String statusName);

    public List<Task> findAllByDoerEmailAndStatusName(String doerEmail, String statusName);
}

package com.yan.crm_proj.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
}

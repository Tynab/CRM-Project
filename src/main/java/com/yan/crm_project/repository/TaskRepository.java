package com.yan.crm_project.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
/**
 * Tom tat: Repository JPA cho cong viec, mo san JpaSpecificationExecutor neu
 * can loc dong trong cac man hinh thong ke sau nay.
 */
public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task> {
}

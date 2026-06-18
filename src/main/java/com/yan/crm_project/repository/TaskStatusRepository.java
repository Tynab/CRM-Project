package com.yan.crm_project.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
/**
 * Tom tat: Repository doc danh muc trang thai cong viec.
 */
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {
}

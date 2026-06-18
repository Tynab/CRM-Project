package com.yan.crm_project.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
/**
 * Tom tat: Truy van role theo ten de kiem tra trung lap khi tao moi.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}

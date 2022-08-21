package com.yan.crm_project.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public List<User> findAllByName(String name);

    public User findByEmail(String email);
}

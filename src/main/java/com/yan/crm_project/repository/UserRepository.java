package com.yan.crm_project.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;

@Repository
/**
 * Tom tat: Truy van nguoi dung theo ID, email va ten hien thi cho man hinh
 * quan ly/thanh tim kiem.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByNameContainingIgnoreCase(String name);

    User findByEmail(String email);
}

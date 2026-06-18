package com.yan.crm_project.service;

import java.util.*;

import com.yan.crm_project.model.*;

/**
 * Tom tat: Khai bao nghiep vu quan ly tai khoan va adapter UserDetailsService.
 */
public interface UserService {
    Iterable<User> getUsers();

    List<User> getUsers(String name);

    User getUser(int id);

    User getUser(String email);

    User saveUser(User user);

    User saveUserWithoutPassword(User user);

    void deleteUser(int id);
}

package com.yan.crm_project.service;

import java.util.*;

import com.yan.crm_project.model.*;

public interface UserService {
    public Iterable<User> getUsers();

    public List<User> getUsers(String name);

    public User getUser(int id);

    public User getUser(String email);

    public User saveUser(User user);

    public User saveUserWithoutPassword(User user);

    public void deleteUser(int id);
}

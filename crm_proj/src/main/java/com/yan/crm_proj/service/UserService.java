package com.yan.crm_proj.service;

import com.yan.crm_proj.model.*;

public interface UserService {
    public Iterable<User> getUsers();

    public User getUser(String email);

    public User saveUser(User user);

    public User saveUserWithoutPassword(User user);

    public void deleteUser(int id);
}

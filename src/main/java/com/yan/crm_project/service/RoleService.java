package com.yan.crm_project.service;

import com.yan.crm_project.model.*;

/**
 * Tom tat: Khai bao nghiep vu CRUD vai tro nguoi dung.
 */
public interface RoleService {
    Iterable<Role> getRoles();

    Role getRole(int id);

    Role getRole(String name);

    Role saveRole(Role role);

    void deleteRole(int id);
}

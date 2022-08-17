package com.yan.crm_proj.service;

import com.yan.crm_proj.model.*;

public interface RoleService {
    public Iterable<Role> getRoles();

    public Role getRole(int id);

    public Role getRole(String name);

    public Role saveRole(Role role);

    public void deleteRole(int id);
}

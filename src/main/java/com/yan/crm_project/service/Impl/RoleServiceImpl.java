package com.yan.crm_project.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.repository.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.extern.slf4j.*;

@Service
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private TextUtil textUtil;

    @Override
    public Iterable<Role> getRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(int id) {
        log.info("Fetching role with id {}", id);
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role getRole(String name) {
        name = stringUtil.parseName(name);
        log.info("Fetching role with name: {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        role.setName(stringUtil.parseName(role.getName()));
        role.setDescription(textUtil.parseToLegalText(role.getDescription()));
        log.info("Saving role with name: {}", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(int id) {
        log.info("Deleting role with id: {}", id);
        roleRepository.deleteById(id);
    }
}

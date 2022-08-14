package com.yan.crm_proj.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.repository.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final StringUtil stringUtil;

    @Override
    public Iterable<Role> getRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(String name) {
        log.info("Fetching role with name: {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        role.setName(stringUtil.titleCase(stringUtil.removeSpCharsBeginAndEnd(role.getName())));
        role.setDescription(stringUtil.sentenceCase(stringUtil.removeNumAndWhiteSpaceBeginAndEnd(role.getDescription())));
        log.info("Saving role with name: {}", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(int id) {
        log.info("Deleting role with id: {}", id);
        roleRepository.deleteById(id);
    }
}

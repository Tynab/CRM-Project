package com.yan.crm_project.service.Impl;

import java.util.*;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.User;
import com.yan.crm_project.repository.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.extern.slf4j.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.util.Collections.*;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private AddressUtil addressUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username);
        // check user exists
        if (user == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        } else {
            log.info("User found: {}", username);
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                    singleton(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole().getName().toUpperCase())));
        }
    }

    @Override
    public Iterable<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsers(String name) {
        log.info("Fetching all users by name: {}", name);
        return userRepository.findAllByName(name);
    }

    @Override
    public User getUser(int id) {
        log.info("Fetching user with id {}", id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUser(String email) {
        email = stringUtil.parseEmail(email);
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        user.setEmail(stringUtil.parseEmail(user.getEmail()));
        user.setPassword(passwordEncoder.encode(stringUtil.removeWhiteSpaceBeginAndEnd(user.getPassword())));
        user.setName(stringUtil.parseName(user.getName()));
        user.setAddress(addressUtil.parseToLegalAddress(user.getAddress()));
        log.info("Saving user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User saveUserWithoutPassword(User user) {
        user.setPassword(getUser(user.getId()).getPassword());
        user.setName(stringUtil.parseName(user.getName()));
        user.setAddress(addressUtil.parseToLegalAddress(user.getAddress()));
        log.info("Saving user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}

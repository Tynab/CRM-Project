package com.yan.crm_proj.service.Impl;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.User;
import com.yan.crm_proj.repository.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.*;
import lombok.extern.slf4j.*;

import static com.yan.crm_proj.constant.AttributeConstant.*;
import static java.util.Collections.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final StringUtil stringUtil;

    @Autowired
    private final AddressUtil addressUtil;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepository.findByEmail(username);
        // check user exists
        if (user == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        } else {
            log.info("User found: {}", username);
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                    singleton(new SimpleGrantedAuthority(ROLE_KEY + user.getRole().getName().toUpperCase())));
        }
    }

    @Override
    public Iterable<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public User getUser(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        user.setEmail(stringUtil.removeSpCharsBeginAndEnd(user.getEmail()).toLowerCase());
        user.setPassword(passwordEncoder.encode(stringUtil.removeWhiteSpaceBeginAndEnd(user.getPassword())));
        user.setName(stringUtil.titleCase(stringUtil.removeNumAndWhiteSpaceBeginAndEnd(user.getName())));
        user.setAddress(addressUtil.reparseToLegalAddress(user.getAddress()));
        log.info("Saving user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User saveUserWithoutPassword(User user) {
        user.setPassword(getUser(user.getEmail()).getPassword());
        user.setName(stringUtil.titleCase(stringUtil.removeNumAndWhiteSpaceBeginAndEnd(user.getName())));
        user.setAddress(addressUtil.reparseToLegalAddress(user.getAddress()));
        log.info("Saving user with id: {}", user.getId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}

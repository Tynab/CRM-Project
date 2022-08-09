package com.yan.crm_proj.service;

import javax.transaction.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.User;
import com.yan.crm_proj.repository.*;

import lombok.*;
import lombok.extern.slf4j.*;

import static java.util.Collections.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImp implements UserService, UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepository.findByEmail(username);
        if (user == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        } else {
            log.info("User found: {}", username);
            var authorities = singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase()));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                    authorities);
        }
    }

    @Override
    public Iterable<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public User getUser(String email) {
        log.info("Fetching user with id: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving user with id: {}", user.getId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}

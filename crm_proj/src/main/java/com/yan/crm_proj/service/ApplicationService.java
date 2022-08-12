package com.yan.crm_proj.service;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;

import lombok.*;

import static org.springframework.security.core.context.SecurityContextHolder.*;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    @Autowired
    private final UserService userService;

    public User getAccount() {
        final var authentication = getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return userService.getUser(authentication.getName());
        } else {
            return null;
        }
    }
}

package com.yan.crm_proj.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;

import static org.springframework.security.core.context.SecurityContextHolder.*;

@Component
public class UserUtil {
    @Autowired
    private UserService userService;

    // Get current user account from SecurityContextHolder
    public User getAccount() {
        var authentication = getContext().getAuthentication();
        // check authentication exists
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return userService.getUser(authentication.getName());
        } else {
            return null;
        }
    }
}

package com.yan.crm_project.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;

import static org.springframework.security.core.context.SecurityContextHolder.*;

@Component
/**
 * Tom tat: Doc nguoi dung hien tai tu SecurityContext va tra ve entity User
 * tu database neu principal con hop le.
 */
public class AuthenticationUtil {
    @Autowired
    private UserService userService;

    /**
     * Lay account dang dang nhap; tra ve null khi chua xac thuc hoac session het
     * hieu luc.
     */
    public User getAccount() {
        var authentication = getContext().getAuthentication();
        return authentication == null || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated() ? null
                : userService.getUser(authentication.getName());
    }
}

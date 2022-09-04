package com.yan.crm_project.config;

import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.savedrequest.*;

@Configuration
public class ApplicationConfig {
    // Passord encode BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Request cache
    @Bean
    public HttpSessionRequestCache getHttpSessionRequestCache() {
        var httpSessionRequestCache = new HttpSessionRequestCache();
        httpSessionRequestCache.setCreateSessionAllowed(false);
        return httpSessionRequestCache;
    }
}

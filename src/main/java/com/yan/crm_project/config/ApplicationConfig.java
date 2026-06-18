package com.yan.crm_project.config;

import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.savedrequest.*;

@Configuration
/**
 * Tom tat: Khai bao cac bean ha tang dung chung cho bao mat va request cache.
 */
public class ApplicationConfig {
    /**
     * Ma hoa mat khau nguoi dung bang BCrypt truoc khi luu vao database.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Khong tao session moi chi de luu request truoc dang nhap.
     */
    @Bean
    public HttpSessionRequestCache getHttpSessionRequestCache() {
        var httpSessionRequestCache = new HttpSessionRequestCache();
        httpSessionRequestCache.setCreateSessionAllowed(false);
        return httpSessionRequestCache;
    }
}

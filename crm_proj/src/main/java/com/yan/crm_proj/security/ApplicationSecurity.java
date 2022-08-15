package com.yan.crm_proj.security;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.savedrequest.*;

import com.yan.crm_proj.filter.*;
import com.yan.crm_proj.util.*;

import static com.yan.crm_proj.constant.ApplicationConstant.Role.*;
import static com.yan.crm_proj.constant.ViewConstant.*;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSessionRequestCache httpSessionRequestCache;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var jwtAuthenFilter = new JwtAuthenFilter(authenticationManagerBean());
        jwtAuthenFilter.setFilterProcessesUrl(API_VIEW + LOGIN_VIEW);
        http.csrf().disable().requestCache().requestCache(httpSessionRequestCache).and().authorizeRequests() // replace
                                                                                                             // stateless
                .antMatchers("/api/login/**", "/user/refresh/**", "/js/script.js", "/css/login.css").permitAll()
                .antMatchers("/index/**", "/profile/**").hasAnyRole("MEMBER", "LEADER", "ADMIN")
                .antMatchers("/project/**", "/role/**", "/task/**", "/user/**")
                .hasRole(ADMIN).anyRequest()
                .authenticated()
                .and().formLogin().loginPage(LOGIN_VIEW).loginProcessingUrl(LOGIN_VIEW)
                .defaultSuccessUrl(INDEX_VIEW)
                .failureUrl(LOGIN_VIEW + applicationUtil.sendMsgUrl("Tài khoản chưa chính xác!")).permitAll().and()
                .logout()
                .invalidateHttpSession(true).clearAuthentication(true).logoutSuccessUrl(LOGIN_VIEW).permitAll().and()
                .exceptionHandling().accessDeniedPage(FORBIDDEN_VIEW).and().addFilter(jwtAuthenFilter)
                .addFilterBefore(new JwtAuthorFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

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

import lombok.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final HttpSessionRequestCache httpSessionRequestCache;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var jwtAuthenFilter = new JwtAuthenFilter(authenticationManagerBean());
        jwtAuthenFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable().requestCache().requestCache(httpSessionRequestCache).and().authorizeRequests() // replace stateless
                .antMatchers("/api/login/**", "/user/refresh/**").permitAll()
                .antMatchers("/index/**", "/profile/**").hasAnyRole("MEMBER", "LEADER", "ADMIN")
                .antMatchers("/project/**", "/role/**", "/task/**", "/user/**")
                .hasRole("ADMIN").anyRequest()
                .authenticated()
                .and().formLogin().loginPage("/login").loginProcessingUrl("/login")
                .defaultSuccessUrl("/index").failureUrl("/login?error=true").permitAll().and().logout()
                .invalidateHttpSession(true).clearAuthentication(true).logoutSuccessUrl("/login").permitAll().and()
                .exceptionHandling().accessDeniedPage("/403").and().addFilter(jwtAuthenFilter)
                .addFilterBefore(new JwtAuthorFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

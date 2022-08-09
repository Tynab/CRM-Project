package com.yan.crm_proj.security;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.savedrequest.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import com.yan.crm_proj.filter.*;

import lombok.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var jwtAuthenFilter = new JwtAuthenFilter(authenticationManagerBean());
        jwtAuthenFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable().requestCache().requestCache(getHttpSessionRequestCache()).and().authorizeRequests()
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

        // http.csrf().disable();
        // http.sessionManagement().sessionCreationPolicy(STATELESS);
        // http.authorizeRequests().antMatchers("/api/login/**",
        // "/user/refresh/**").permitAll();
        // http.authorizeRequests().antMatchers("/index").hasAnyAuthority("ROLE_MEMBER");
        // http.authorizeRequests().antMatchers("/index/**", "/profile/**",
        // "/project/**", "/role/**", "/task/**",
        // "/user/**").hasAnyAuthority("ROLE_ADMIN");
        // http.authorizeRequests().anyRequest().authenticated();
        // http.formLogin().loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/profile").failureUrl("/login?error=true").permitAll();
        // http.logout().invalidateHttpSession(true).clearAuthentication(true).logoutSuccessUrl("/login").permitAll();
        // http.exceptionHandling().accessDeniedPage("/403");
        // http.addFilter(jwtAuthenFilter);
        // http.addFilterBefore(new JwtAuthorFilter(),
        // UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public HttpSessionRequestCache getHttpSessionRequestCache() {
        var httpSessionRequestCache = new HttpSessionRequestCache();
        httpSessionRequestCache.setCreateSessionAllowed(false);
        return httpSessionRequestCache;
    }
}

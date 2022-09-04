package com.yan.crm_project.filter;

import java.io.*;
import java.util.*;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.*;
import org.springframework.web.filter.*;

import com.fasterxml.jackson.databind.*;

import lombok.extern.slf4j.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static java.util.Arrays.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.core.context.SecurityContextHolder.*;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var servletPath = request.getServletPath();
        // Pass api
        if (servletPath.equals(API_VIEW + LOGIN_VIEW) || servletPath.equals(API_VIEW + TOKEN_VIEW + REFRESH_VIEW)) {
            filterChain.doFilter(request, response);
            return;
        } else {
            var header = request.getHeader(AUTHORIZATION);
            // check header
            if (header != null && header.startsWith(TOKEN_PREFIX)) {
                try {
                    var decodedJwt = require(HMAC256(SECRET_KEY.getBytes())).build()
                            .verify(header.substring(TOKEN_PREFIX.length()));
                    var authorities = new ArrayList<SimpleGrantedAuthority>();
                    stream(decodedJwt.getClaim(ROLE_CLAIM_KEY).asArray(String.class))
                            .forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                    getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(decodedJwt.getSubject(), null, authorities));
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    var errorMsg = e.getMessage();
                    log.error("Error logging in: {}", errorMsg);
                    response.setHeader(ERROR_HEADER_KEY, errorMsg);
                    response.setStatus(FORBIDDEN.value());
                    var error = new HashMap<>();
                    error.put(ERROR_MESSAGE_KEY, errorMsg);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}

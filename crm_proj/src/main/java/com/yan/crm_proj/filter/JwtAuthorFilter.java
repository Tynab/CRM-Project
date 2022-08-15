package com.yan.crm_proj.filter;

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
import static com.yan.crm_proj.constant.ApplicationConstant.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;
import static java.util.Arrays.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.core.context.SecurityContextHolder.*;

@Slf4j
public class JwtAuthorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().equals(API_VIEW + LOGIN_VIEW)
                || request.getServletPath().equals(PROFILE_VIEW + REFRESH_VIEW)) {
            filterChain.doFilter(request, response);
            return;
        } else {
            var header = request.getHeader(AUTHORIZATION);
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
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader(ERROR_HEADER_KEY, e.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    var error = new HashMap<>();
                    error.put(ERROR_MESSAGE_KEY, e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}

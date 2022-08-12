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
import static com.yan.crm_proj.common.ApplicationConstant.*;
import static com.yan.crm_proj.common.AttributeConstant.*;
import static com.yan.crm_proj.common.ViewConstant.*;
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
        if (request.getServletPath().equals("/api/login")
                || request.getServletPath().equals(USER_VIEW + REFRESH_VIEW)) {
            filterChain.doFilter(request, response);
            return;
        } else {
            var authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
                try {
                    final var decodedJwt = require(HMAC256(SECRET_KEY.getBytes())).build()
                            .verify(authorizationHeader.substring(TOKEN_PREFIX.length()));
                    final var roles = decodedJwt.getClaim(ROLE_CLAIM_KEY).asArray(String.class);
                    var authorities = new ArrayList<SimpleGrantedAuthority>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
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

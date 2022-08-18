package com.yan.crm_proj.controller;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.exc.*;
import com.fasterxml.jackson.databind.*;
import com.yan.crm_proj.model.Role;
import com.yan.crm_proj.service.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_proj.constant.AppConstant.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping(API_VIEW)
public class ApiController {
    @Autowired
    private UserService userService;

    // Refresh token
    @GetMapping(TOKEN_VIEW + REFRESH_VIEW)
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        var header = request.getHeader(AUTHORIZATION);
        // check token format in authorization header
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            // get token from authorization header
            try {
                var refreshToken = header.substring(TOKEN_PREFIX.length());
                var algorithm = HMAC256(SECRET_KEY.getBytes());
                var user = userService.getUser(require(algorithm).build().verify(refreshToken).getSubject());
                var tokens = new HashMap<>();
                tokens.put(ACCESS_TOKEN_KEY,
                        create().withSubject(user.getEmail())
                                .withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME))
                                .withIssuer(request.getRequestURL().toString())
                                .withClaim(ROLE_CLAIM_KEY,
                                        singleton(new Role(ROLE_PREFIX + user.getRole().getName().toUpperCase()))
                                                .stream().map(Role::getName).collect(toList()))
                                .sign(algorithm));
                tokens.put(REFRESH_TOKEN_KEY, refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.setHeader(ERROR_HEADER_KEY, e.getMessage());
                response.setStatus(FORBIDDEN.value());
                var error = new HashMap<>();
                error.put(ERROR_MESSAGE_KEY, e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
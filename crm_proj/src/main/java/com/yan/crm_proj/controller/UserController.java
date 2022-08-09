package com.yan.crm_proj.controller;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.fasterxml.jackson.core.exc.*;
import com.fasterxml.jackson.databind.*;
import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;

import lombok.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_proj.common.Constant.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("")
    public ModelAndView user() {
        var mav = new ModelAndView("user");
        return mav;
    }

    @GetMapping("/add")
    public ModelAndView userAdd() {
        var mav = new ModelAndView("user-add");
        return mav;
    }

    @GetMapping("/details")
    public ModelAndView userDetails() {
        var mav = new ModelAndView("user-details");
        return mav;
    }

    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        var authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            try {
                var refreshToken = authorizationHeader.substring(TOKEN_PREFIX.length());
                var algorithm = HMAC256(SECRET.getBytes());
                var user = userService.getUser(require(algorithm).build().verify(refreshToken).getSubject());
                var roles = singleton(new Role("ROLE_" + user.getRole().getName().toUpperCase()));
                var accessToken = create().withSubject(user.getEmail())
                        .withExpiresAt(new Date(currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", roles.stream().map(Role::getName).collect(toList())).sign(algorithm);
                var tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                var error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}

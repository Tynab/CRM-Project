package com.yan.crm_project.filter;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;

import com.fasterxml.jackson.databind.*;

import lombok.*;
import lombok.extern.slf4j.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.lang.System.*;
import static java.util.stream.Collectors.*;
import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    // Fields
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        var username = request.getParameter("username");
        var password = request.getParameter("password");
        log.info("Email: {}", username);
        log.info("Password: {}", password);
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        var user = (User) authResult.getPrincipal();
        var algorithm = HMAC256(SECRET_KEY.getBytes());
        var tokens = new HashMap<>();
        var userName = user.getUsername();
        var requestUrl = request.getRequestURL().toString();
        tokens.put(ACCESS_TOKEN_KEY,
                create().withSubject(userName).withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME))
                        .withIssuer(requestUrl)
                        .withClaim(ROLE_CLAIM_KEY,
                                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
                        .sign(algorithm));
        tokens.put(REFRESH_TOKEN_KEY,
                create().withSubject(userName).withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME * 24))
                        .withIssuer(requestUrl).sign(algorithm));
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}

package com.yan.crm_proj.filter;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;

import com.fasterxml.jackson.databind.*;

import lombok.*;
import lombok.extern.slf4j.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_proj.common.AttributeConstant.*;
import static com.yan.crm_proj.common.ApplicationConstant.*;
import static java.lang.System.*;
import static java.util.stream.Collectors.*;
import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenFilter extends UsernamePasswordAuthenticationFilter {
	@Autowired
	private final AuthenticationManager authenticationManager;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		final var username = request.getParameter("username");
		final var password = request.getParameter("password");
		log.info("Email: {}", username);
		log.info("Password: {}", password);
		final var authToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		final var user = (User) authResult.getPrincipal();
		final var algorithm = HMAC256(SECRET_KEY.getBytes());
		final var accessToken = create().withSubject(user.getUsername())
				.withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME / 2))
				.withIssuer(request.getRequestURL().toString())
				.withClaim(ROLE_CLAIM_KEY,
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
				.sign(algorithm);
		final var refreshToken = create().withSubject(user.getUsername())
				.withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME))
				.withIssuer(request.getRequestURL().toString()).sign(algorithm);
		var tokens = new HashMap<>();
		tokens.put(ACCESS_TOKEN_KEY, accessToken);
		tokens.put(REFRESH_TOKEN_KEY, refreshToken);
		response.setContentType(APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}
}

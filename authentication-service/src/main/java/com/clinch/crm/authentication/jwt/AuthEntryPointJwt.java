package com.clinch.crm.authentication.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Decides what the client gets when Spring Security reports an authentication failure:
 * 401 "Error: Unauthorized".
 *
 * <p>In practice this is what a wrong email or password at {@code POST /auth/signin} produces:
 * the {@code BadCredentialsException} thrown by the login check ends up here.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	/**
	 * Logs the failure and sends 401.
	 *
	 * @param request       the request that failed authentication
	 * @param response      the response to write the 401 to
	 * @param authException why authentication failed
	 * @throws IOException      if writing the response fails
	 * @throws ServletException never thrown by this implementation
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("Unauthorized error: {}", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
	}

}

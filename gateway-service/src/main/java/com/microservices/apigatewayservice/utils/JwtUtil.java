package com.microservices.apigatewayservice.utils;

import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	private final Key signKey;

	/**
	 * The secret comes from the JWT_SECRET environment variable (see application.properties)
	 * and must be the same Base64 value authentication-service signs tokens with.
	 */
	public JwtUtil(@Value("${jwtSecret}") String jwtSecret) {
		if (jwtSecret == null || jwtSecret.isBlank()) {
			throw new IllegalStateException("JWT_SECRET environment variable is not set");
		}
		this.signKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret.trim()));
	}

	/**
	 * Verifies signature and expiry and returns the token's claims.
	 *
	 * @throws JwtException             if the token is invalid, expired or tampered with
	 * @throws IllegalArgumentException if the token is null or empty
	 */
	public Claims parseClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token).getBody();
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			logger.warn("Invalid JWT: {}", e.getMessage());
			return false;
		}
	}
}

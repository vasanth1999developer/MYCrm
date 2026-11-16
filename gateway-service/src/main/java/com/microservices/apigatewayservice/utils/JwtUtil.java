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

/**
 * Verifies JWTs issued by authentication-service (HS256, jjwt 0.11).
 *
 * <p>HS256 is symmetric: the same secret signs and verifies. The gateway therefore needs exactly
 * the same {@code JWT_SECRET} as authentication-service; with a different value every token fails
 * with a signature error and every secured request gets 401.
 *
 * <p>The gateway only verifies tokens, it never creates them.
 */
@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	/** HMAC-SHA key built once at startup from {@code JWT_SECRET}, reused for every request. */
	private final Key signKey;

	/**
	 * The secret comes from the JWT_SECRET environment variable (see application.properties)
	 * and must be the same Base64 value authentication-service signs tokens with.
	 *
	 * @param jwtSecret Base64-encoded secret of at least 32 bytes (256 bits)
	 * @throws IllegalStateException if the secret is blank
	 * @throws io.jsonwebtoken.security.WeakKeyException if the decoded key is shorter than 256 bits,
	 *         so a weak secret fails at startup rather than on the first request
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
	 * <p>{@code parseClaimsJws} requires a signed token, so an unsigned ({@code alg: none}) token
	 * is rejected. The returned claims used by the gateway are {@code userId}, {@code email},
	 * {@code username}, {@code roles} and {@code privileges}.
	 *
	 * @param token the compact JWT ({@code header.payload.signature}), without the "Bearer " prefix
	 * @return the verified payload as a map of claim name to value
	 * @throws JwtException             if the token is invalid, expired or tampered with
	 * @throws IllegalArgumentException if the token is null or empty
	 */
	public Claims parseClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Convenience check that never throws: {@code true} if {@link #parseClaims} succeeds.
	 *
	 * <p>{@link com.microservices.apigatewayservice.filter.AuthenticationFilter} doesn't use it,
	 * because it needs the claims anyway and calls {@link #parseClaims} directly.
	 *
	 * @param token the compact JWT, without the "Bearer " prefix
	 * @return {@code true} if the token is validly signed and not expired; {@code false} otherwise
	 *         (the reason is logged at WARN)
	 */
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

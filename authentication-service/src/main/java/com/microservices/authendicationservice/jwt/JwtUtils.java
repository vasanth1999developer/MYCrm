package com.microservices.authendicationservice.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.microservices.authendicationservice.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Creates (and, in legacy methods, validates) the JWTs the whole platform uses.
 *
 * <p>Tokens are signed with HS256 using {@code JWT_SECRET}. The gateway verifies them with the same
 * secret, so both services must have the same value. Only {@link #generateToken} is used today;
 * the other public methods are leftovers from an earlier design.
 */
@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	/** Base64 HMAC secret from {@code JWT_SECRET} (at least 32 bytes once decoded). */
	@Value("${jwtSecret}")
	private String jwtSecret;

	/** Token lifetime in milliseconds ({@code jwtExpirationMs}, 900000 = 15 min). */
	@Value("${jwtExpirationMs}")
	private int jwtExpirationMs;

	/**
	 * Legacy, unused: builds a token from a Spring {@link Authentication}.
	 *
	 * <p>Not compatible with the gateway: {@code userId} is a String, there's no {@code privileges}
	 * claim, and {@code roles} contains authority objects ({@code [{"authority":"..."}]}) instead
	 * of plain names. {@code setId} is called twice; the random UUID wins.
	 *
	 * @param authentication a successful authentication whose principal is a {@link UserDetailsImpl}
	 * @return the compact signed JWT
	 */
	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();



		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setId(String.valueOf((userPrincipal.getId())))

				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
//				.setSubject.setSubject(String.valueOf((userPrincipal.getId())))
				.setIssuer("mycrm-auth")
				.setId(UUID.randomUUID().toString())          // jti
				.claim("userId", String.valueOf((userPrincipal.getId())))
				.claim("email", userPrincipal.getEmail())
				.claim("username", userPrincipal.getUsername())
				.claim("roles", userPrincipal.getAuthorities())
//				.claim("privileges", privileges)

				.compact();

	}

	/**
	 * Builds the signed access token returned by {@code POST /auth/signin}.
	 *
	 * <p>Standard claims: {@code sub} (user id), {@code iss} ("mycrm-auth"), {@code jti} (random
	 * UUID, unique per token), {@code iat}, {@code exp} (now + {@link #jwtExpirationMs}).
	 * Custom claims, which the gateway copies into {@code X-User-*} headers: {@code userId}
	 * (number), {@code email}, {@code username}, {@code roles} and {@code privileges} (JSON arrays).
	 *
	 * @param userId     the user's database id
	 * @param email      the user's email (login key)
	 * @param username   the display/login name
	 * @param roles      role names, e.g. {@code [SALES_REP]}
	 * @param privileges privilege names granted by those roles
	 * @return the compact JWT ({@code header.payload.signature})
	 */
	public String generateToken(Long userId, String email, String username,
								Set<String> roles, Set<String> privileges) {

		Date now = new Date();

		return Jwts.builder()
				.setSubject(String.valueOf(userId))
				.setIssuer("mycrm-auth")
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + jwtExpirationMs))
				.claim("userId", userId)              // Long, not String
				.claim("email", email)
				.claim("username", username)
				.claim("roles", roles)                // Set<String> → ["SALES_REP"]
				.claim("privileges", privileges)
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Legacy, unused: verifies a token and returns its {@code sub} claim.
	 *
	 * <p>With tokens from {@link #generateToken} the subject is the user <b>id</b>, not the
	 * username, despite this method's name.
	 *
	 * @param token the compact JWT
	 * @return the subject claim
	 * @throws JwtException if the token is invalid or expired
	 */
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Legacy, unused: checks signature and expiry, logging the specific reason on failure.
	 * Token verification for requests happens in the gateway, not here.
	 *
	 * @param authToken the compact JWT
	 * @return {@code true} if valid, {@code false} otherwise
	 */
	public boolean validateJwtToken(String authToken) {
		try {
			 Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	/**
	 * Decodes {@link #jwtSecret} from Base64 and builds the HMAC-SHA key. Called on every sign or
	 * verify (the key isn't cached).
	 *
	 * @return the signing key
	 * @throws io.jsonwebtoken.security.WeakKeyException if the key is shorter than 256 bits
	 */
	 private Key getSignKey() {
	        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
}

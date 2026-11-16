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

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${jwtSecret}")
	private String jwtSecret;

	@Value("${jwtExpirationMs}")
	private int jwtExpirationMs;

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

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

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
	
	 private Key getSignKey() {
	        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
}

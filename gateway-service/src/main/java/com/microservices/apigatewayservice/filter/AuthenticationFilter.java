package com.microservices.apigatewayservice.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.microservices.apigatewayservice.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    /** Identity headers set by the gateway from the verified JWT. Never trusted from clients. */
    public static final String USER_HEADER_PREFIX = "X-User-";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_USER_NAME = "X-User-Name";
    public static final String HEADER_USER_ROLES = "X-User-Roles";
    public static final String HEADER_USER_PRIVILEGES = "X-User-Privileges";
    /** Legacy header older services read as the caller's user id (e.g. createdBy). */
    public static final String LEGACY_ID_HEADER = "id";
    /** Proves to downstream services (their GatewayAuthFilter) that the request passed this filter. */
    public static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";

    @Value("${gateway.secret}")
    private String gatewaySecret;

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (!validator.isSecured.test(exchange.getRequest())) {
                // Public endpoint: forward, but never let a client pass identity headers through.
                ServerHttpRequest request = stripIdentityHeaders(exchange.getRequest()).mutate()
                        .headers(h -> h.set(GATEWAY_SECRET_HEADER, gatewaySecret))
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                return unauthorized(exchange, "missing or malformed Authorization header");
            }

            String token = authHeader.substring(BEARER_PREFIX.length()).trim();
            if (token.isEmpty()) {
                return unauthorized(exchange, "empty bearer token");
            }

            Claims claims;
            try {
                claims = jwtUtil.parseClaims(token);
            } catch (JwtException | IllegalArgumentException e) {
                logger.warn("JWT validation failed: {}", e.getMessage());
                return unauthorized(exchange, "invalid token");
            }

            String userId = claimAsString(claims.get("userId"));
            if (userId.isEmpty()) {
                return unauthorized(exchange, "token has no userId claim");
            }

            ServerHttpRequest request = stripIdentityHeaders(exchange.getRequest()).mutate()
                    .headers(h -> {
                        h.set(HEADER_USER_ID, userId);
                        h.set(LEGACY_ID_HEADER, userId);
                        h.set(HEADER_USER_EMAIL, claimAsString(claims.get("email")));
                        h.set(HEADER_USER_NAME, claimAsString(claims.get("username")));
                        h.set(HEADER_USER_ROLES, claimAsString(claims.get("roles")));
                        h.set(HEADER_USER_PRIVILEGES, claimAsString(claims.get("privileges")));
                        h.set(GATEWAY_SECRET_HEADER, gatewaySecret);
                    })
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        });
    }

    /** Removes any client-supplied X-User-* and legacy "id" headers. */
    private ServerHttpRequest stripIdentityHeaders(ServerHttpRequest request) {
        String prefix = USER_HEADER_PREFIX.toLowerCase(Locale.ROOT);
        return request.mutate()
                .headers(h -> {
                    List<String> toRemove = new ArrayList<>();
                    for (String name : h.keySet()) {
                        if (name.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                            toRemove.add(name);
                        }
                    }
                    toRemove.add(LEGACY_ID_HEADER);
                    toRemove.add(GATEWAY_SECRET_HEADER);
                    toRemove.forEach(h::remove);
                })
                .build();
    }

    /** Scalars become their string form; collections (roles, privileges) become comma-separated. */
    private static String claimAsString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Collection) {
            return String.join(",", ((Collection<?>) value).stream().map(String::valueOf).toArray(String[]::new));
        }
        return String.valueOf(value);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
        logger.warn("Rejecting {} {}: {}", exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath(), reason);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        return response.setComplete();
    }

    public static class Config {

    }
}

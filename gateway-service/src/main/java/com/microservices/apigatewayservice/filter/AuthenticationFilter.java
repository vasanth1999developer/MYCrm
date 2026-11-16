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

/**
 * Route filter that authenticates every request passing through the gateway.
 *
 * <p>Attached to each route with {@code spring.cloud.gateway.routes[N].filters=AuthenticationFilter}.
 * Spring Cloud Gateway names a filter factory after its class (minus any
 * {@code GatewayFilterFactory} suffix), which is how that property finds this bean.
 *
 * <p>For every request it:
 * <ol>
 *   <li>asks {@link RouteValidator} whether the path is public;</li>
 *   <li>removes client-sent {@code X-User-*}, {@code id} and {@code X-Gateway-Secret} headers, so
 *       nobody can fake an identity or the secret;</li>
 *   <li>on a secured path, requires {@code Authorization: Bearer <jwt>} and verifies it with
 *       {@link JwtUtil#parseClaims}; any failure ends the request with 401 and it is not forwarded;</li>
 *   <li>sets the trusted identity headers from the token's claims;</li>
 *   <li>adds {@code X-Gateway-Secret}, which each downstream {@code GatewayAuthFilter} requires,
 *       and forwards the request.</li>
 * </ol>
 *
 * <p>Downstream services must read the caller's identity only from these headers.
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    /** Expected start of the Authorization header value (note the trailing space). */
    private static final String BEARER_PREFIX = "Bearer ";

    /** Identity headers set by the gateway from the verified JWT. Never trusted from clients. */
    public static final String USER_HEADER_PREFIX = "X-User-";
    /** The caller's user id, from the {@code userId} claim. */
    public static final String HEADER_USER_ID = "X-User-Id";
    /** The caller's email, from the {@code email} claim. */
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    /** The caller's login name, from the {@code username} claim. */
    public static final String HEADER_USER_NAME = "X-User-Name";
    /** The caller's roles, comma-separated, from the {@code roles} claim. */
    public static final String HEADER_USER_ROLES = "X-User-Roles";
    /** The caller's privileges, comma-separated, from the {@code privileges} claim. */
    public static final String HEADER_USER_PRIVILEGES = "X-User-Privileges";
    /** Legacy header older services read as the caller's user id (e.g. createdBy). */
    public static final String LEGACY_ID_HEADER = "id";
    /** Proves to downstream services (their GatewayAuthFilter) that the request passed this filter. */
    public static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";

    /** Shared secret from {@code GATEWAY_SECRET}; must equal {@code gateway.secret} in every service. */
    @Value("${gateway.secret}")
    private String gatewaySecret;

    /** Decides which paths are public. */
    @Autowired
    private RouteValidator validator;

    /** Verifies the token and extracts its claims. */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Tells the parent factory which class holds this filter's per-route arguments
     * (none here, see {@link Config}).
     */
    public AuthenticationFilter() {
        super(Config.class);
    }

    /**
     * Builds the filter instance for one route. Called once per route at startup; the returned
     * lambda then runs for every request on that route.
     *
     * <p>Inside the lambda, {@code chain.filter(...)} passes the (modified) request on towards the
     * downstream service, while returning {@link #unauthorized} instead stops it with 401. WebFlux
     * requests are immutable, so changes are made on copies via {@code mutate()}.
     *
     * @param config per-route options (unused, the class is empty)
     * @return a filter that authenticates the request and rewrites its headers
     */
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

            // set() replaces any existing value, so each header ends up with exactly one value.
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

    /**
     * Removes any client-supplied X-User-* and legacy "id" headers.
     *
     * <p>Also removes {@code X-Gateway-Secret}, so a client can't send its own guess; the real value
     * is added afterwards. The prefix check ignores case, so {@code x-user-privileges} is caught
     * too. Names are collected first and removed after the loop, because removing while iterating
     * {@code keySet()} would throw {@code ConcurrentModificationException}.
     *
     * @param request the incoming request
     * @return a copy of the request without those headers
     */
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

    /**
     * Scalars become their string form; collections (roles, privileges) become comma-separated.
     *
     * <p>Examples: {@code null} → {@code ""}; {@code 42L} → {@code "42"};
     * {@code ["LEAD_READ","LEAD_WRITE"]} → {@code "LEAD_READ,LEAD_WRITE"}.
     *
     * @param value a claim value from the token, possibly {@code null} if the claim is missing
     * @return a header-safe string, never {@code null}
     */
    private static String claimAsString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Collection) {
            return String.join(",", ((Collection<?>) value).stream().map(String::valueOf).toArray(String[]::new));
        }
        return String.valueOf(value);
    }

    /**
     * Ends the request with 401 Unauthorized without forwarding it.
     *
     * <p>Sets {@code WWW-Authenticate: Bearer} (the standard hint that a Bearer token is needed)
     * and an empty body. The reason is only logged, not sent to the client, so a caller doesn't
     * learn why their token failed.
     *
     * @param exchange the current request/response pair
     * @param reason   why the request was rejected, for the WARN log
     * @return a {@link Mono} that completes when the response has been sent
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
        logger.warn("Rejecting {} {}: {}", exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath(), reason);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        return response.setComplete();
    }

    /**
     * Per-route configuration for this filter. Empty, because the filter takes no arguments.
     * Fields added here could be set per route, e.g. {@code filters=AuthenticationFilter=...}.
     */
    public static class Config {

    }
}

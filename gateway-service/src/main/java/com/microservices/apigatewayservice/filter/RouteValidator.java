package com.microservices.apigatewayservice.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Decides which request paths are public (no JWT needed) and which are secured.
 *
 * <p>Used by {@link AuthenticationFilter} on every routed request. This is the <b>only</b> place
 * to make an endpoint public: add it to {@link #openExactPaths} or {@link #openPathPrefixes}.
 *
 * <p>Matching is exact or by prefix, never "contains". An earlier version used
 * {@code path.contains(...)}, so {@code /employee/x/auth/signin} was treated as public and
 * skipped authentication.
 */
@Component
public class RouteValidator {

    /** Paths that skip JWT validation only when the request path matches exactly. */
    public static final Set<String> openExactPaths = Set.of(
            "/auth/signin",
            "/auth/signup",
            "/eureka"
    );

    /**
     * Paths that skip JWT validation for anything underneath them. Each entry must end
     * with "/" so that "/eureka/" does not also match "/eurekaXYZ".
     */
    public static final List<String> openPathPrefixes = List.of(
            "/auth/signupcheckEmailId/",
            "/eureka/"
    );

    /**
     * Tests whether a request needs a valid JWT.
     *
     * <p>Returns {@code true} (secured) unless the path equals one of {@link #openExactPaths} or
     * starts with one of {@link #openPathPrefixes}. Only the path is checked; the HTTP method and
     * query string are ignored. Examples:
     * <ul>
     *   <li>{@code /auth/signin} → {@code false} (public)</li>
     *   <li>{@code /auth/signin/} → {@code true} (trailing slash, so not an exact match)</li>
     *   <li>{@code /auth/signupcheckEmailId/a@b.com} → {@code false}</li>
     *   <li>{@code /employee/x/auth/signin} → {@code true}</li>
     * </ul>
     */
    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        return !openExactPaths.contains(path)
                && openPathPrefixes.stream().noneMatch(path::startsWith);
    };

}

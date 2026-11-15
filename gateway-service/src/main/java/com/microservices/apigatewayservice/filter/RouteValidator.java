package com.microservices.apigatewayservice.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        return !openExactPaths.contains(path)
                && openPathPrefixes.stream().noneMatch(path::startsWith);
    };

}

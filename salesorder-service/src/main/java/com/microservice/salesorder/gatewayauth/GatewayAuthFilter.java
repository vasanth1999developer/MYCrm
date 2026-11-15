package com.microservice.salesorder.gatewayauth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Rejects (403) any request that did not come through the API gateway or another trusted
 * service. Both of those send {@value #HEADER} with the shared GATEWAY_SECRET; a client that
 * calls this service's port directly cannot.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Gateway-Secret";

    private static final Logger logger = LoggerFactory.getLogger(GatewayAuthFilter.class);

    private final byte[] secret;

    public GatewayAuthFilter(@Value("${gateway.secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("GATEWAY_SECRET environment variable is not set");
        }
        this.secret = secret.trim().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String provided = request.getHeader(HEADER);
        if (provided == null
                || !MessageDigest.isEqual(secret, provided.getBytes(StandardCharsets.UTF_8))) {
            logger.warn("Rejected direct call {} {} from {}", request.getMethod(),
                    request.getRequestURI(), request.getRemoteAddr());
            response.sendError(HttpStatus.FORBIDDEN.value(), "Requests must go through the API gateway");
            return;
        }
        chain.doFilter(request, response);
    }
}

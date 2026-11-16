package com.clinch.crm.authentication.gatewayauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Adds the shared gateway secret to every outgoing Feign call so the callee's GatewayAuthFilter accepts it.
 *
 * <p>Feign calls go straight to the other service via Eureka, not through the gateway, so without
 * this header authorization-service would answer 403 and sign-in would fail.
 */
@Component
public class GatewaySecretFeignInterceptor implements RequestInterceptor {

    /** Value of {@code gateway.secret}, trimmed. */
    private final String secret;

    /** @param secret value of {@code gateway.secret} ({@code GATEWAY_SECRET}) */
    public GatewaySecretFeignInterceptor(@Value("${gateway.secret}") String secret) {
        this.secret = secret.trim();
    }

    /**
     * Called by Feign before each request; sets {@code X-Gateway-Secret}.
     *
     * @param template the request being built
     */
    @Override
    public void apply(RequestTemplate template) {
        template.header(GatewayAuthFilter.HEADER, secret);
    }
}

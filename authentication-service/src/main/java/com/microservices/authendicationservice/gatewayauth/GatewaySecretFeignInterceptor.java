package com.microservices.authendicationservice.gatewayauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/** Adds the shared gateway secret to every outgoing Feign call so the callee's GatewayAuthFilter accepts it. */
@Component
public class GatewaySecretFeignInterceptor implements RequestInterceptor {

    private final String secret;

    public GatewaySecretFeignInterceptor(@Value("${gateway.secret}") String secret) {
        this.secret = secret.trim();
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(GatewayAuthFilter.HEADER, secret);
    }
}

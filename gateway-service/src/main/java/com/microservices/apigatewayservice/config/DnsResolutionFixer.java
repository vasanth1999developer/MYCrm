package com.microservices.apigatewayservice.config;

import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Configuration;
import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;

@Configuration
public class DnsResolutionFixer implements HttpClientCustomizer {

	@Override
	public HttpClient customize(HttpClient httpClient) {
		return httpClient.resolver(DefaultAddressResolverGroup.INSTANCE);
	}
}
package com.clinch.crm.gateway.config;

import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Configuration;
import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;

/**
 * Makes the gateway resolve downstream host names with the operating system's DNS resolver.
 *
 * <p>Spring Cloud Gateway forwards requests with a Reactor Netty {@link HttpClient}, which by
 * default uses Netty's own asynchronous DNS resolver. That resolver can fail on Windows, behind
 * VPNs, or for names that exist only in the hosts file ({@code UnknownHostException},
 * "Failed to resolve"). With the JVM/OS resolver, names resolve exactly as {@code ping} would.
 */
@Configuration
public class DnsResolutionFixer implements HttpClientCustomizer {

	/**
	 * Called by Spring Cloud Gateway while it builds the shared downstream {@link HttpClient}.
	 *
	 * @param httpClient the client as configured so far
	 * @return the same client, set to use {@link DefaultAddressResolverGroup#INSTANCE} (JVM/OS DNS)
	 */
	@Override
	public HttpClient customize(HttpClient httpClient) {
		return httpClient.resolver(DefaultAddressResolverGroup.INSTANCE);
	}
}

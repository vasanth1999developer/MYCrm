package com.microservices.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Entry point of the API gateway (Spring Cloud Gateway on WebFlux/Netty), listening on port 8556.
 *
 * <p>This is the single entry point for all client traffic. Routes are declared in
 * {@code application.properties} (one {@code Path=/<prefix>/**} route per service, prefixes not
 * stripped), and every route runs {@link com.microservices.apigatewayservice.filter.AuthenticationFilter},
 * which verifies the JWT, replaces the identity headers and adds {@code X-Gateway-Secret}.
 *
 * <p>Requires the {@code JWT_SECRET} and {@code GATEWAY_SECRET} environment variables, and a
 * running eureka-service. See {@code ENDPOINTS.md} for the full request flow.
 */
@EnableEurekaClient // registers as "api-gateway-service" and keeps a local registry copy for lb:// URIs
@SpringBootApplication
public class ApiGatewayServiceApplication {

	/**
	 * Starts the reactive Netty server on port 8556.
	 *
	 * <p>{@code spring.main.web-application-type=reactive} forces WebFlux, because
	 * {@code spring-boot-starter-web} (Servlet) is also on the classpath and the gateway cannot
	 * run on the Servlet stack.
	 *
	 * @param args command-line arguments, passed to Spring
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayServiceApplication.class, args);
	}

}

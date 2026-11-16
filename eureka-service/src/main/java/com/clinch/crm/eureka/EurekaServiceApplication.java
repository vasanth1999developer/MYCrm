package com.clinch.crm.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Entry point of the MyCrm service registry (Netflix Eureka server), listening on port 8555.
 *
 * <p>Every other service registers here under its {@code spring.application.name}, sends a
 * heartbeat every 30 s, and downloads the registry so that {@code lb://<name>} addresses (gateway
 * routes, Feign clients) can be resolved to real host:port pairs. The registry is kept in memory
 * only; after a restart, services re-register on their next heartbeat.
 *
 * <p>This is the only class in the module. All endpoints (the dashboard at {@code /} and the REST
 * API under {@code /eureka/*}) come from {@code spring-cloud-starter-netflix-eureka-server}.
 * Behaviour (no self-registration, self-preservation off, 5 s eviction) is set in
 * {@code application.yml}. See {@code ENDPOINTS.md} for every endpoint.
 *
 * <p>Start this service first, before the gateway and the business services.
 */
@EnableEurekaServer // activates EurekaServerAutoConfiguration: registry, REST API, dashboard, eviction timer
@SpringBootApplication
public class EurekaServiceApplication {

	/**
	 * Boots the embedded Tomcat on port 8555 and starts the Eureka registry.
	 *
	 * <p>{@link SpringApplication#run} returns once startup has finished, so the message is
	 * printed when the server is ready to accept registrations.
	 *
	 * @param args command-line arguments, passed to Spring (e.g. {@code --server.port=9000})
	 */
	public static void main(String[] args) {
		SpringApplication.run(EurekaServiceApplication.class, args);
		System.out.println("Eureka Server");
	}

}

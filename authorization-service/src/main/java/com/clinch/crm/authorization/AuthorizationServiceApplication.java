package com.clinch.crm.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Entry point of authorization-service (port 8778), the platform's role-based access control
 * (RBAC) store: <b>roles</b> → <b>privileges</b> → <b>access entries</b>.
 *
 * <p>authentication-service calls it at every sign-in to turn a user's roles into privileges for
 * the JWT, so start it before authentication-service. employee-service calls it to check that a
 * role exists. Needs MySQL ({@code ms_mybiz_authorization}), eureka-service and the
 * {@code GATEWAY_SECRET} environment variable.
 */
@EnableEurekaClient // registers as "authorization-service"
@SpringBootApplication
public class AuthorizationServiceApplication {

	/**
	 * Starts the embedded Tomcat on port 8778.
	 *
	 * @param args command-line arguments, passed to Spring
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServiceApplication.class, args);
	}

}

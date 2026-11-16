package com.microservices.authendicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point of authentication-service (port 8557): owns login users and issues the JWTs that
 * the gateway verifies on every request.
 *
 * <p>Needs MySQL ({@code ms_mybiz_authendication}), eureka-service, and the {@code JWT_SECRET} and
 * {@code GATEWAY_SECRET} environment variables. At sign-in it calls authorization-service, so that
 * must be running too. The package name keeps the historical spelling "authendication".
 */
@EnableEurekaClient // registers as "authentication-service"
@EnableFeignClients // creates implementations for @FeignClient interfaces such as AuthorizationClient
@SpringBootApplication
public class AuthendicationServiceApplication {

	/**
	 * Starts the embedded Tomcat on port 8557.
	 *
	 * @param args command-line arguments, passed to Spring
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthendicationServiceApplication.class, args);
	}

}

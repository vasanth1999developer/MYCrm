package com.microservices.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point of employee-service (port 8562): employees, departments and territories.
 *
 * <p>Creating an employee also validates the role in authorization-service and creates the login
 * user in authentication-service, through Feign. Needs MySQL ({@code ms_mybiz_employee}),
 * eureka-service, both of those services, and the {@code GATEWAY_SECRET} environment variable.
 */
@EnableEurekaClient // registers as "employee-service"
@EnableFeignClients // creates implementations for AuthClient and AuthorizationClient
@SpringBootApplication
public class EmployeeServiceApplication {

	/**
	 * Starts the embedded Tomcat on port 8562.
	 *
	 * @param args command-line arguments, passed to Spring
	 */
	public static void main(String[] args) {
		SpringApplication.run(EmployeeServiceApplication.class, args);
	}

}

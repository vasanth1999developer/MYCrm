package com.microservice.opportunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@EnableEurekaClient
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Opportunity-API",version="1.0",description = "Opportunity project for Spring Boot"))
public class UserOpportunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserOpportunityApplication.class, args);
	}

}

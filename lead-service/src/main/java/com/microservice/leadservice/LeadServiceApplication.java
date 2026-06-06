package com.microservice.leadservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@EnableEurekaClient
@SpringBootApplication
@EntityScan({"com.microservice.leadservice.models.entity"})
@OpenAPIDefinition(info = @Info(title = "LEAD-API",version="1.0",description = "LEAD API with MS"))
public class LeadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeadServiceApplication.class, args);
	}

}

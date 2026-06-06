package com.microservices.authendicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@EnableEurekaClient
@SpringBootApplication
public class AuthendicationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthendicationServiceApplication.class, args);
	}

}

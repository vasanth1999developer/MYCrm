package com.microservice.salesorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@EnableEurekaClient
public class SalesorderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesorderServiceApplication.class, args);
	}

}

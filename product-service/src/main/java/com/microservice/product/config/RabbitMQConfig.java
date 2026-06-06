package com.microservice.product.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String QUEUE_NAME = "FinalProductQueue";
	public static final String EXCHANGE_NAME = "productExchange";
	public static final String ROUTING_KEY = "productRoutingKey";

	@Bean
	public Queue exampleQueue() {
		return new Queue(QUEUE_NAME, true);
	}

	@Bean
	public TopicExchange exampleExchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}
}
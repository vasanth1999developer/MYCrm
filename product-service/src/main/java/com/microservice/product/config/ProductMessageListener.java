package com.microservice.product.config;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.product.service.ProductServiceImpl;

@Service
public class ProductMessageListener {

	@Autowired
	private ProductServiceImpl productService;

	private final ObjectMapper objectMapper;

	public ProductMessageListener(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
	public void receiveMessage(String message) {
		try {

			Map<String, Object> messagePayload = objectMapper.readValue(message, Map.class);

			Long productId = Long.valueOf(messagePayload.get("productId").toString());
			Integer orderedUnit = Integer.valueOf(messagePayload.get("orderedUnit").toString());

			System.out.println("====================================================");
			System.out.println(productId);
			System.out.println(orderedUnit);
			System.out.println("====================================================");
			boolean stockReduced = productService.reduceStock(productId, orderedUnit);

			if (!stockReduced) {

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}

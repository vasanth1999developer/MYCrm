package com.microservices.authorizationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that boots the full authorization-service context.
 *
 * <p>Needs MySQL with the {@code ms_mybiz_authorization} schema and the {@code GATEWAY_SECRET}
 * environment variable. It's the module's only test.
 */
@SpringBootTest
class AuthorizationServiceApplicationTests {

	/** Passes if the Spring context (JPA repositories, controllers, filter) starts without errors. */
	@Test
	void contextLoads() {
	}

}

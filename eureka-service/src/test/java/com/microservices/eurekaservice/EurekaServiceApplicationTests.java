package com.microservices.eurekaservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that boots the full Eureka server application context.
 *
 * <p>It needs no database, secrets or other services, but it does start the real server,
 * so it is slower than a unit test.
 */
@SpringBootTest
class EurekaServiceApplicationTests {

	/** Passes if the Spring context (registry, REST API, dashboard) starts without errors. */
	@Test
	void contextLoads() {
	}

}

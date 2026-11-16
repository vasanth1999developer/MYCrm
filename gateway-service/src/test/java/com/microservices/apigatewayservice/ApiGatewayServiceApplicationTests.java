package com.microservices.apigatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that boots the full gateway application context.
 *
 * <p>Needs the {@code JWT_SECRET} and {@code GATEWAY_SECRET} environment variables (otherwise the
 * placeholders can't be resolved) and ideally a running eureka-service. For tests without any
 * infrastructure, see {@code AuthenticationFilterTest}.
 */
@SpringBootTest
class ApiGatewayServiceApplicationTests {

	/** Passes if the Spring context (routes, filters, Eureka client) starts without errors. */
	@Test
	void contextLoads() {
	}

}

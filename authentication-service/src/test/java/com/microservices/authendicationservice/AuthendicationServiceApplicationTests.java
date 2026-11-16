package com.microservices.authendicationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that boots the full authentication-service context.
 *
 * <p>Needs MySQL with the {@code ms_mybiz_authendication} schema and the {@code JWT_SECRET} and
 * {@code GATEWAY_SECRET} environment variables. For a test without infrastructure, see
 * {@code UserJsonTest}.
 */
@SpringBootTest
class AuthendicationServiceApplicationTests {

	/** Passes if the Spring context (security, JPA, Feign, Eureka client) starts without errors. */
	@Test
	void contextLoads() {
	}

}

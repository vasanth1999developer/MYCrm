package com.microservices.accountservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Broken leftover copied from account-service: it sits in package
 * {@code com.microservices.accountservice}, where Spring finds no {@code @SpringBootApplication},
 * so it fails with "Unable to find a @SpringBootConfiguration". Move it to
 * {@code com.microservices.employee} or delete it.
 */
@SpringBootTest
class AccountServiceApplicationTests {

	/** Would pass if the application context started. */
	@Test
	void contextLoads() {
	}

}

package com.clinch.crm.employee;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: starts the whole employee-service application context.
 *
 * <p>Needs MySQL with the {@code ms_mybiz_employee} schema and the {@code GATEWAY_SECRET}
 * environment variable, so it isn't a unit test. (It used to sit in a package copied from
 * account-service, where it could never find the application class.)
 */
@SpringBootTest
class EmployeeServiceApplicationTests {

	/** Passes if the application context starts. */
	@Test
	void contextLoads() {
	}

}

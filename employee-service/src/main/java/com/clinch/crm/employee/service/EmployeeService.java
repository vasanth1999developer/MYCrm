package com.clinch.crm.employee.service;

import java.util.List;

import com.clinch.crm.employee.model.EmployeeBo;

/**
 * Business logic for employees. Implemented by {@link EmployeeServiceImpl}.
 */
public interface EmployeeService {

	/**
	 * Validates the role, creates the login user, then saves the employee under the same id.
	 *
	 * @param employee the new employee, including login credentials
	 * @return the same object, refreshed from the saved row
	 * @throws IllegalArgumentException if the role doesn't exist in authorization-service
	 * @throws feign.FeignException if authorization- or authentication-service fails or rejects
	 *         the call (e.g. email already used)
	 */
	EmployeeBo createEmployee(EmployeeBo employee);

	/**
	 * @return all employees that aren't soft-deleted
	 */
	List<EmployeeBo> getAllEmployees();



}

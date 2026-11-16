package com.microservices.employee.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.microservices.employee.SyncCall.AuthClient;
import com.microservices.employee.SyncCall.AuthorizationClient;
import com.microservices.employee.common.ApiResponse;
import com.microservices.employee.entity.EmployeeVo;
import com.microservices.employee.model.CreateUserRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.microservices.employee.model.EmployeeBo;
import com.microservices.employee.repository.EmployeeRepository;

/**
 * Default {@link EmployeeService}: the platform's main example of a call across services.
 *
 * <p>{@code @Transactional} on the class wraps each method in a database transaction, but only
 * for <b>this</b> service's database: the login user created in authentication-service is not
 * part of it and is never rolled back.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
	/** Feign client for authorization-service (role check). */
	private final AuthorizationClient authorizationClient;   // Feign → authorization-service
	/** Feign client for authentication-service (login user creation). */
	private final AuthClient authClient;
	/** Employee table access. Also set by the constructor; {@code @Autowired} on the field is redundant. */
	@Autowired
	private EmployeeRepository employeeRepository;

	/**
	 * Constructor injection of the dependencies.
	 *
	 * @param authorizationClient Feign client for authorization-service
	 * @param authClient          Feign client for authentication-service
	 * @param employeeRepository  employee table access
	 */
	public EmployeeServiceImpl(AuthorizationClient authorizationClient,
							   AuthClient authClient,
							   EmployeeRepository employeeRepository) {
		this.authorizationClient = authorizationClient;
		this.authClient = authClient;
		this.employeeRepository = employeeRepository;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <ol>
	 *   <li><b>Check the role</b> with authorization-service ({@code GET /authorization/role/exists}).
	 *       Done first because it's cheap and creates nothing. Note that check also accepts
	 *       inactive and deleted roles.</li>
	 *   <li><b>Create the login user</b> in authentication-service ({@code POST /auth/users}) with
	 *       username, email, password and the single role. Its response is the new user id.</li>
	 *   <li><b>Save the employee</b> with {@code employeeId} = that user id, so the employee and
	 *       the login share one id (the {@code userId} in the JWT).</li>
	 * </ol>
	 *
	 * <p>Step 3 has no compensation: whatever goes wrong, the login user from step 2 stays.
	 * Exceptions thrown inside {@code save()} are <b>swallowed</b> (only printed) and the client
	 * sees success. Because the id is assigned rather than generated, {@code save()} does a merge
	 * and the actual INSERT runs at commit, after this method; constraint violations found then
	 * (e.g. the email already exists in this table) propagate to the controller, which answers
	 * "Employee creation failed". Steps 1 and 2 throw normally.
	 */
	@Override
	public EmployeeBo createEmployee(EmployeeBo employee) {

		// 1. VALIDATE role first (authorization-service) — cheap check, fail fast before creating anything
		ApiResponse<Boolean> roleCheck = authorizationClient.roleExists(employee.getRole());
		if (roleCheck.getData() == null || !roleCheck.getData()) {
			throw new IllegalArgumentException("Invalid role: " + employee.getRole());
		}

		// 2. CREATE login (authentication-service) — returns the shared id
		CreateUserRequest userReq = new CreateUserRequest(
				employee.getUsername(),
				employee.getEmail(),
				employee.getPassword(),
				Set.of(employee.getRole())

		);
		Long userId = authClient.createUser(userReq).getData();

		try {

			EmployeeVo employeeVo = new EmployeeVo();
			BeanUtils.copyProperties(employee, employeeVo);
			employeeVo.setEmployeeId(userId);
			employeeVo.setDeleted(false);
			employeeVo = employeeRepository.save(employeeVo);
			BeanUtils.copyProperties(employeeVo, employee);




		} catch (Exception e) {
			e.printStackTrace();
		}
		return employee;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Copies each {@link EmployeeVo} into an {@link EmployeeBo} with {@link BeanUtils}; fields the
	 * Bo doesn't have (id, active flag, timestamps) are dropped.
	 */
	@Override
	public List<EmployeeBo> getAllEmployees() {

		List<EmployeeVo> employeeVo = employeeRepository.findAllByIsDeleted(false);

		List<EmployeeBo>  employeeBo = new ArrayList<EmployeeBo>();

		 for (EmployeeVo employeeVo1 : employeeVo) {
			 EmployeeBo  employeeBo1 = new EmployeeBo();
			 BeanUtils.copyProperties(employeeVo1, employeeBo1);
			 employeeBo.add(employeeBo1);
		 }

		return employeeBo;
	}


}

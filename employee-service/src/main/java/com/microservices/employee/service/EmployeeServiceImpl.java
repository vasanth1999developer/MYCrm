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

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
	private final AuthorizationClient authorizationClient;   // Feign → authorization-service
	private final AuthClient authClient;
	@Autowired
	private EmployeeRepository employeeRepository;

	public EmployeeServiceImpl(AuthorizationClient authorizationClient,
							   AuthClient authClient,
							   EmployeeRepository employeeRepository) {
		this.authorizationClient = authorizationClient;
		this.authClient = authClient;
		this.employeeRepository = employeeRepository;
	}

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



}

package com.microservices.employee.service;

import java.util.List;

import com.microservices.employee.model.EmployeeBo;

public interface EmployeeService {

	EmployeeBo createEmployee(EmployeeBo employee);

	List<EmployeeBo> getAllEmployee();

	EmployeeBo updateEmployee(EmployeeBo employeeBo);

	EmployeeBo getEmployeeById(long employeeId);

	boolean deleteemployee(long employeeId);
	
}

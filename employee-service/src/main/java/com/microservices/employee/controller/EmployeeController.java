package com.microservices.employee.controller;

import java.util.ArrayList;
import java.util.List;

import com.microservices.employee.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.microservices.employee.model.EmployeeBo;
import com.microservices.employee.service.EmployeeService;

/**
 * REST endpoints for employees, under {@code /employee} (JWT required at the gateway).
 *
 * <p>Responses use the {@link ApiResponse} envelope. Note the JSON returned for an employee
 * contains neither its id nor its role/manager (see {@link EmployeeBo}).
 */
@Validated
@RestController
@RequestMapping("employee")
//@CrossOrigin(origins = "*")
public class EmployeeController {

	/** Employee business logic (orchestrates authorization- and authentication-service). */
	@Autowired
	private EmployeeService employeeService;


	/**
	 * {@code POST /employee/create-employee}: creates the login user and the employee record.
	 *
	 * <p>Body: an {@link EmployeeBo} with at least {@code username}, {@code email}, {@code password},
	 * {@code firstName} and {@code role}. The flow is described in
	 * {@link com.microservices.employee.service.EmployeeServiceImpl#createEmployee}.
	 *
	 * <p>Watch out:
	 * <ul>
	 *   <li>There's no {@code @Valid}, so the constraints on {@link EmployeeBo} (password strength,
	 *       email format, …) are <b>not</b> checked here. authentication-service still checks its
	 *       own, looser rules (password 6-40 characters).</li>
	 *   <li>Every failure (unknown role, email already used, a service down) is answered with
	 *       <b>201</b>, {@code success: false} and the generic message "Employee creation failed".
	 *       The cause is only in this service's log.</li>
	 * </ul>
	 *
	 * @param employee the new employee, including the login credentials
	 * @return 201 with the saved employee (without id, role, manager, username or password),
	 *         or 201 with {@code success: false}
	 */
	@PostMapping("/create-employee")
	public ResponseEntity<ApiResponse<EmployeeBo>> createEmployee(@RequestBody  EmployeeBo employee){
		EmployeeBo	employeeResponse;
		try {
				employeeResponse = employeeService.createEmployee(employee);
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.error("Employee creation failed"));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Employee is Created",employeeResponse));

	}


	/**
	 * {@code GET /employee/list-employee}: all employees that aren't soft-deleted, unpaged.
	 *
	 * <p>Answers <b>201</b> with the message "Employee is Created" although nothing is created
	 * (copied from the create endpoint). Each item lacks the id, role and manager.
	 *
	 * @return 201 with the list; 400 with the exception message if the query fails
	 */
	@GetMapping("/list-employee")
	public ResponseEntity<ApiResponse<List<EmployeeBo>>> getAllEmployee(){


         List<EmployeeBo> employeeList = List.of();

		 try{

			 employeeList =  employeeService.getAllEmployees();


		 }
         catch (Exception e) {
			 e.printStackTrace();

			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
		 }

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Employee is Created",employeeList));
    }











}

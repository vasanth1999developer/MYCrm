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

@Validated
@RestController
@RequestMapping("employee")
//@CrossOrigin(origins = "*")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	

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

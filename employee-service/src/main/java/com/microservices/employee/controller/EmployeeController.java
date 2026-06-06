package com.microservices.employee.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<?> createEmployee(@RequestBody EmployeeBo employee){
		
		try {
			employee.setDelete(false);
			employee = employeeService.createEmployee(employee);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(employee);
		
	}
	@GetMapping("/get-all-employees")
	public ResponseEntity<?> getAllEmployee(){
		List<EmployeeBo> employeeList = new ArrayList<>();
		try {
			
			employeeList = employeeService.getAllEmployee();
			if(null!=employeeList && !employeeList.isEmpty() && employeeList.size()>0) {
				return ResponseEntity.ok(employeeList);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	@PutMapping("/update-employee")
	public ResponseEntity<?> updateEmployee(@RequestBody EmployeeBo employeeBo){
		
		try {
			//employeeBo.setEmployeeId(employeeId);
			if(null!=employeeBo) {
			employeeBo = employeeService.updateEmployee(employeeBo);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(employeeBo);
		
	}
	@GetMapping("/get-employeeById/{id}")
	public ResponseEntity<?> getemployeeById(@PathVariable("id") long employeeId){
		EmployeeBo employee =new EmployeeBo();
		try {
	
			employee = employeeService.getEmployeeById(employeeId);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(employee);
	
	}
	
	@DeleteMapping("/delete-employee/{id}")
	public ResponseEntity<Boolean> deleteEmployee(@PathVariable("id") long employeeId){
		boolean status = false;
		try {
			status = employeeService.deleteemployee(employeeId);
			if(status) {
				return ResponseEntity.ok(status);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(status);
		
	}
}

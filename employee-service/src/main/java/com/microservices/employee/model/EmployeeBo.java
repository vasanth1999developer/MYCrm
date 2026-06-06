package com.microservices.employee.model;

import lombok.Data;

@Data
public class EmployeeBo {

	private Long employeeId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private Long mobileNo;
	private String password;
	
	private boolean isDelete;
	private String address;
	private String state;
	private String country;
	private long sectionNo;
}

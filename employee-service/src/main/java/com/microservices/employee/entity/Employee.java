package com.microservices.employee.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="employee")
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long employeeId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private Long mobileNo;
	private String password;
	
	private String address;
	private String state;
	private String country;
	private boolean isDelete;
	

}

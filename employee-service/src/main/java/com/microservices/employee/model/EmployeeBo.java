package com.microservices.employee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class EmployeeBo {

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 20, message = "Username must be 3-20 characters")
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;


	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 100, message = "Password must be at least 8 characters")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
			message = "Password must contain upper, lower case and a digit"
	)
	private String password;

	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must not exceed 50 characters")
	private String firstName;

	@Size(max = 50, message = "Last name must not exceed 50 characters")
	private String lastName;

	@Pattern(
			regexp = "^$|^[0-9]{10}$",
			message = "Phone must be 10 digits"
	)
	private String phone;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "Role is required")
	private String role;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Positive(message = "Manager id must be greater than zero")
	private Long managerId;      // null allowed for top-level employees


	@Size(max = 50, message = "Department must not exceed 50 characters")
	private String department;

	@Size(max = 50, message = "Territory must not exceed 50 characters")
	private String territory;


	@Size(max = 50, message = "Designation must not exceed 50 characters")
	private String designation;

	@PastOrPresent(message = "Date of joining cannot be in the future")
	private LocalDate dateOfJoining;


}
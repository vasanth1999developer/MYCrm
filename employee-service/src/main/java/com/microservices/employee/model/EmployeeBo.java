package com.microservices.employee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Request and response body for employees.
 *
 * <p>Fields marked {@code WRITE_ONLY} are read from requests but never written to responses:
 * {@code username}, {@code password}, {@code role} and {@code managerId}. There is no
 * {@code employeeId} field, so responses don't identify the employee either.
 *
 * <p>The validation annotations describe the intended rules, but {@code create-employee} has no
 * {@code @Valid}, so they aren't enforced yet. Lombok {@code @Data} generates getters/setters.
 */
@Data
public class EmployeeBo {

	/** Login name for authentication-service, 3-20 characters. Write-only. */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 20, message = "Username must be 3-20 characters")
	private String username;

	/** Work email; also the login key for sign-in. At most 100 characters (authentication-service allows 50). */
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;


	/** Initial password, 8+ characters with upper case, lower case and a digit. Write-only, never stored here. */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 100, message = "Password must be at least 8 characters")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
			message = "Password must contain upper, lower case and a digit"
	)
	private String password;

	/** First name, required, at most 50 characters. */
	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must not exceed 50 characters")
	private String firstName;

	/** Last name, optional, at most 50 characters. */
	@Size(max = 50, message = "Last name must not exceed 50 characters")
	private String lastName;

	/** Phone: empty or exactly 10 digits. */
	@Pattern(
			regexp = "^$|^[0-9]{10}$",
			message = "Phone must be 10 digits"
	)
	private String phone;

	/** Role name, must exist in authorization-service (e.g. {@code SALES_EXECUTIVE}). Write-only. */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "Role is required")
	private String role;

	/** Id of this employee's manager (another employee); {@code null} for top-level employees. Write-only. Not checked to exist. */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Positive(message = "Manager id must be greater than zero")
	private Long managerId;      // null allowed for top-level employees


	/** Department <b>name</b> (free text, not linked to the {@code department} table). */
	@Size(max = 50, message = "Department must not exceed 50 characters")
	private String department;

	/** Territory <b>name</b> (free text, not linked to the {@code territory} table). */
	@Size(max = 50, message = "Territory must not exceed 50 characters")
	private String territory;


	/** Job title, at most 50 characters. */
	@Size(max = 50, message = "Designation must not exceed 50 characters")
	private String designation;

	/** Start date ({@code yyyy-MM-dd} in JSON), not in the future. */
	@PastOrPresent(message = "Date of joining cannot be in the future")
	private LocalDate dateOfJoining;


}

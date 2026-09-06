package com.microservices.employee.entity;

import javax.persistence.*;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Data
public class EmployeeVo {

	@Id
	@Column(name = "employee_id")
	private Long employeeId;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "phone")
	private String phone;

	@Column(name = "role", nullable = false)
	private String role;

	@Column(name = "manager_id")
	private Long managerId;

	@Column(name = "department")
	private String department;

	@Column(name = "territory")
	private String territory;

	@Column(name = "designation")
	private String designation;

	@Column(name = "date_of_joining")
	private LocalDate dateOfJoining;

	@Column(name = "is_active", nullable = false)
	private boolean isActive = true;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@PrePersist void onCreate() { createdAt = updatedAt = Instant.now(); }
	@PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}

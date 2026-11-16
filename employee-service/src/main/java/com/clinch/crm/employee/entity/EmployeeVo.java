package com.clinch.crm.employee.entity;

import javax.persistence.*;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA entity for the {@code employee} table.
 *
 * <p>The primary key is <b>not generated</b> here: it's the user id returned by
 * authentication-service, so an employee and its login share one id.
 */
@Entity
@Table(name = "employee")
@Data
public class EmployeeVo {

	/** Primary key ({@code employee_id}) = the login user's id in authentication-service. */
	@Id
	@Column(name = "employee_id")
	private Long employeeId;

	/** First name, required. */
	@Column(name = "first_name", nullable = false)
	private String firstName;

	/** Last name. */
	@Column(name = "last_name")
	private String lastName;

	/** Email, required and unique. */
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	/** Phone number. */
	@Column(name = "phone")
	private String phone;

	/** Role name, as given at creation; not updated if the user's roles change in authentication-service. */
	@Column(name = "role", nullable = false)
	private String role;

	/** Manager's employee id, or {@code null}. */
	@Column(name = "manager_id")
	private Long managerId;

	/** Department name (free text). */
	@Column(name = "department")
	private String department;

	/** Territory name (free text). */
	@Column(name = "territory")
	private String territory;

	/** Job title. */
	@Column(name = "designation")
	private String designation;

	/** Start date. */
	@Column(name = "date_of_joining")
	private LocalDate dateOfJoining;

	/** Active flag; defaults to {@code true} and nothing changes it yet. */
	@Column(name = "is_active", nullable = false)
	private boolean isActive = true;

	/** Soft-delete flag; defaults to {@code false} and nothing changes it yet. */
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	/** Set once on insert; {@code updatable = false} keeps it from changing. */
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	/** Set on insert and on every update. */
	@Column(name = "updated_at")
	private Instant updatedAt;

	/** JPA callback run just before the first insert: stamps both timestamps. */
	@PrePersist void onCreate() { createdAt = updatedAt = Instant.now(); }
	/** JPA callback run just before each update: refreshes {@link #updatedAt}. */
	@PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}

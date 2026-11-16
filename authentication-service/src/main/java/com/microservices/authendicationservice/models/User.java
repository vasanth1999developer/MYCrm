package com.microservices.authendicationservice.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A login user (table {@code users}), the account someone signs in with.
 *
 * <p>Created by {@code POST /auth/users} (usually from employee-service). Username and email are
 * each unique. Roles live in the separate table {@code user_roles} ({@code user_id},
 * {@code role_name}) as plain strings.
 *
 * <p>This entity is also returned as JSON by {@code GET /auth/get-loggedInUserById/{userId}};
 * its JSON keys are {@code id}, {@code username}, {@code email}, {@code roles} and
 * {@code isDelete}. The password is never included.
 */
@Entity
@Table(name = "users",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "username"),
				@UniqueConstraint(columnNames = "email")
		})
public class User {

	/** Auto-increment primary key; also the {@code userId} claim in the JWT. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Login/display name, at most 20 characters, unique. */
	@NotBlank
	@Size(max = 20)
	private String username;

	/** Email, the login key; at most 50 characters, unique. */
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	/** BCrypt hash. Never serialized: this entity is returned by GET /auth/get-loggedInUserById. */
	@JsonIgnore
	@NotBlank
	@Size(max = 120)
	private String password;

	/** Soft-delete flag: {@code true} means deactivated, and sign-in is refused. */
	private boolean isDelete;

	// ---- CHANGED: role names as plain strings, not a @ManyToMany to a Role table ----
	/** Role names, e.g. {@code SALES_REP}. Loaded eagerly because sign-in always needs them. */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role_name")
	private Set<String> roles = new HashSet<>();

	/** No-arg constructor required by JPA and Jackson. */
	public User() {
	}

	/**
	 * @param username login/display name
	 * @param email    email (login key)
	 * @param password the BCrypt <b>hash</b>, not the plain password
	 */
	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	/** @return the primary key */
	public Long getId() { return id; }
	/** @param id the primary key */
	public void setId(Long id) { this.id = id; }

	/** @return the login/display name */
	public String getUsername() { return username; }
	/** @param username the login/display name */
	public void setUsername(String username) { this.username = username; }

	/** @return the email (login key) */
	public String getEmail() { return email; }
	/** @param email the email (login key) */
	public void setEmail(String email) { this.email = email; }

	/** @return the BCrypt hash, for Java code only (hidden from JSON) */
	public String getPassword() { return password; }
	/** @param password the BCrypt hash */
	public void setPassword(String password) { this.password = password; }

	// ---- CHANGED: now Set<String> instead of Set<Role> ----
	/** @return the role names */
	public Set<String> getRoles() { return roles; }
	/** @param roles the role names */
	public void setRoles(Set<String> roles) { this.roles = roles; }

	/** @return {@code true} if the account is deactivated (JSON key {@code isDelete}) */
	public boolean getIsDelete() { return isDelete; }
	/** @param isDelete {@code true} to deactivate the account */
	public void setDelete(boolean isDelete) { this.isDelete = isDelete; }
}

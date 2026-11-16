package com.clinch.crm.authentication.models;

import javax.validation.constraints.NotBlank;

/**
 * Request body of {@code POST /auth/signin}: {@code {"username": "<email>", "password": "..."}}.
 *
 * <p>Both fields must be non-blank ({@code @Valid} in the controller turns violations into 400).
 */
public class LoginRequestDto {
	/** The user's <b>email</b> (the login key), despite the field name. */
	@NotBlank
	private String username;

	/** The plain-text password; compared against the stored BCrypt hash, never stored. */
	@NotBlank
	private String password;

	/** @return the email used to log in */
	public String getUsername() {
		return username;
	}

	/** @param username the email used to log in */
	public void setUsername(String username) {
		this.username = username;
	}

	/** @return the plain-text password */
	public String getPassword() {
		return password;
	}

	/** @param password the plain-text password */
	public void setPassword(String password) {
		this.password = password;
	}
}

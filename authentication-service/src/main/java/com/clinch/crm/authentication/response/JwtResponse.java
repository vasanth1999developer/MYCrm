package com.clinch.crm.authentication.response;

import java.util.List;



/**
 * The {@code data} part of a successful {@code POST /auth/signin}.
 *
 * <p>Jackson names JSON keys after the <b>getters</b>, not the fields, so the JSON is:
 * {@code {"id":1,"username":"...","email":"...","roles":["..."],"active":true,
 * "accessToken":"eyJ...","tokenType":"Bearer"}}.
 *
 * <p>Clients send {@code accessToken} back as {@code Authorization: Bearer <accessToken>}.
 */
public class JwtResponse {
	/** The signed JWT (JSON key {@code accessToken}). */
	private String token;
	/** Always "Bearer" (JSON key {@code tokenType}). */
	private String type = "Bearer";
	/** The user's id. */
	private Long id;
	/** Login/display name. */
	private String username;
	/** Email (login key). */
	private String email;
	/** Role names. */
	private List<String> roles;
	/** Always {@code true}: deactivated users never get this far (JSON key {@code active}). */
	private boolean isActive=true;

	/**
	 * @param accessToken the signed JWT
	 * @param id          the user's id
	 * @param username    login/display name
	 * @param email       email
	 * @param roles       role names
	 */
	public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}


	/** Empty constructor (e.g. for JSON deserialization). */
	public JwtResponse() {
		// TODO Auto-generated constructor stub
	}
	/** @return the signed JWT (JSON key {@code accessToken}) */
	public String getAccessToken() {
		return token;
	}

	/** @param accessToken the signed JWT */
	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	/** @return "Bearer" (JSON key {@code tokenType}) */
	public String getTokenType() {
		return type;
	}

	/** @param tokenType the token type */
	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	/** @return the user's id */
	public Long getId() {
		return id;
	}

	/** @param id the user's id */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return the email */
	public String getEmail() {
		return email;
	}

	/** @param email the email */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @return the login/display name */
	public String getUsername() {
		return username;
	}

	/** @param username the login/display name */
	public void setUsername(String username) {
		this.username = username;
	}

	/** @return the role names */
	public List<String> getRoles() {
		return roles;
	}

	/** @return {@code true} (JSON key {@code active}) */
	public boolean isActive() {
		return isActive;
	}

	/** @param isActive whether the account is active */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}



}

package com.clinch.crm.authentication.models;

import java.util.Set;

import javax.validation.constraints.*;

/**
 * Request body of {@code POST /auth/users}:
 * {@code {"username": "...", "email": "...", "password": "...", "role": ["SALES_REP"]}}.
 *
 * <p>Constraints are checked by {@code @Valid} in the controller (violations give 400). The
 * controller additionally requires at least one role.
 */
public class SignupRequestDto {
    /** Login/display name, 3-20 characters. */
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    /** Email (the future login key), valid format, at most 50 characters, must be unused. */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /** Role names (JSON key {@code role}, singular). Required by the controller, not by an annotation. */
    private Set<String> role;

    /** Plain-text password, 6-40 characters; hashed with BCrypt before saving. */
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    /** @return the login/display name */
    public String getUsername() {
        return username;
    }

    /** @param username the login/display name */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the email */
    public String getEmail() {
        return email;
    }

    /** @param email the email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the plain-text password */
    public String getPassword() {
        return password;
    }

    /** @param password the plain-text password */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return the role names */
    public Set<String> getRole() {
      return this.role;
    }

    /** @param role the role names */
    public void setRole(Set<String> role) {
      this.role = role;
    }
}

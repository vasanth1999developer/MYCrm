package com.microservices.employee.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Body sent to authentication-service's {@code POST /auth/users}:
 * {@code {"username": "...", "email": "...", "password": "...", "role": ["SALES_EXECUTIVE"]}}.
 *
 * <p>Mirrors authentication-service's {@code SignupRequestDto}, so the field names (including
 * the singular {@code role}) must stay the same. Lombok generates getters and setters.
 */
@Getter
@Setter
public class CreateUserRequest {
    /** Login name. */
    private String username;
    /** Email, the login key. */
    private String email;
    /** Plain-text password; authentication-service hashes it. */
    private String password;
    /** Role names (one, for employees). */
    private Set<String> role;

    /** Empty constructor, needed by Jackson. */
    public CreateUserRequest() {}

    /**
     * @param username login name
     * @param email    email
     * @param password plain-text password
     * @param role     role names
     */
    public CreateUserRequest(String username, String email, String password, Set<String> role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

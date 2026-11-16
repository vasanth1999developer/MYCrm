package com.microservices.employee.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> role;

    public CreateUserRequest() {}

    public CreateUserRequest(String username, String email, String password, Set<String> role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
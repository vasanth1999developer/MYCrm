package com.microservices.authendicationservice.common;

public class AuthorizationUnavailableException extends RuntimeException {
    public AuthorizationUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

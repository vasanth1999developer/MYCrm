package com.microservices.authendicationservice.common;

/**
 * Thrown at sign-in when the password is correct but the account is deactivated
 * ({@code isDelete = true}). No handler maps it yet, so the client currently gets 500.
 */
public class AccountDeactivatedException extends RuntimeException {
    /** @param message the error message */
    public AccountDeactivatedException(String message) { super(message); }
}

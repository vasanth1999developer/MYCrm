package com.microservices.authendicationservice.common;

public class AccountDeactivatedException extends RuntimeException {
    public AccountDeactivatedException(String message) { super(message); }
}

package com.microservices.authorizationservice.customexception;

/**
 * Thrown when an id doesn't exist (currently by {@code AccessServiceImpl.updateAccess}).
 *
 * <p>{@code GlobalExceptionHandler} has no dedicated handler for it, so the client gets
 * <b>500</b>, not 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    /** @param message the error message */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

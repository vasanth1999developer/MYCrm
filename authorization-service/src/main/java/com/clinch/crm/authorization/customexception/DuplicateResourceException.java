package com.clinch.crm.authorization.customexception;

/**
 * Thrown when creating something whose name is already taken (currently access entries).
 * {@code GlobalExceptionHandler} turns it into 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {
    /** @param message the error message sent to the client */
    public DuplicateResourceException(String message) {
        super(message);
    }
}




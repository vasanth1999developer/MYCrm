package com.clinch.crm.employee.common;

/**
 * Thrown when creating a department or territory whose name already exists.
 *
 * <p>This service has no exception handler, so the client currently gets Spring's default
 * <b>500</b> error, not 409.
 */
public class DuplicateResourceException extends RuntimeException {
    /** @param message the error message */
    public DuplicateResourceException(String message) {
        super(message);
    }
}

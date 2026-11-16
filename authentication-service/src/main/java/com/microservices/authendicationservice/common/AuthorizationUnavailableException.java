package com.microservices.authendicationservice.common;

/**
 * Thrown at sign-in when the user's privileges can't be resolved: authorization-service is down,
 * errored, or returned no privileges. Sign-in fails rather than issuing a token without
 * privileges. No handler maps it yet, so the client currently gets 500.
 */
public class AuthorizationUnavailableException extends RuntimeException {
    /**
     * @param message the error message
     * @param cause   the underlying failure (e.g. a {@code FeignException}), or {@code null}
     */
    public AuthorizationUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.clinch.crm.employee.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * The JSON envelope this service returns, and reads from authentication- and
 * authorization-service through Feign:
 * {@code {"success": true, "message": "...", "data": ..., "errors": {...}, "timestamp": "..."}}.
 *
 * <p>Immutable; create it with the static factories. {@code null} fields are left out of the JSON.
 *
 * @param <T> type of the {@code data} payload
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** {@code true} for success, {@code false} for an error. */
    private final boolean success;
    /** Human-readable summary. */
    private final String message;
    /** The payload, or {@code null}. */
    private final T data;
    /** Field name → error message, or {@code null}. */
    private final Map<String, String> errors;
    /** When the response was created. */
    private final Instant timestamp;

    /**
     * Single constructor, used by the factories and by Jackson when reading a Feign response.
     *
     * @param success   whether the call succeeded
     * @param message   summary
     * @param data      payload
     * @param errors    per-field errors
     * @param timestamp creation time; {@code null} means "now"
     */
    @JsonCreator
    private ApiResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("data") T data,
            @JsonProperty("errors") Map<String, String> errors,
            @JsonProperty("timestamp") Instant timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    /**
     * Success with data and the message "Success".
     *
     * @param data the payload
     * @param <T>  payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null, null);
    }

    /**
     * Success with a custom message and data.
     *
     * @param message summary
     * @param data    the payload
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    /**
     * Success with a message and no data.
     *
     * @param message summary
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null, null);
    }

    /**
     * Error with a message only.
     *
     * @param message what went wrong
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null, null);
    }

    /**
     * Error with a message and per-field errors.
     *
     * @param message what went wrong
     * @param errors  field name → error message
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {
        return new ApiResponse<>(false, message, null, errors, null);
    }
}

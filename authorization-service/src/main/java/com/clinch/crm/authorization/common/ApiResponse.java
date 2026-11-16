package com.clinch.crm.authorization.common;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * The JSON envelope used by most of this service's endpoints:
 * {@code {"success": true, "message": "...", "data": ..., "timestamp": "..."}}.
 *
 * <p>Each service has its own copy; authentication-service reads this one through Feign.
 * Immutable; create it with the static factories. {@code null} fields are left out of the JSON.
 * Lombok {@code @Getter} generates the getters Jackson writes from.
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
    /** Field name → error message. Currently always {@code null} (see {@link #error(String, Map)}). */
    private final Map<String, String> errors;
    /** When the response was created. */
    private final Instant timestamp;

    /**
     * Single constructor, used by the factories and by Jackson when reading JSON.
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

    // factory methods — update to pass the extra timestamp arg as null
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
     * <p><b>Bug:</b> {@code errors} is ignored (the constructor gets {@code null}), so the JSON
     * never contains it. Every caller passes {@code null} today, so nothing is lost yet.
     *
     * @param message what went wrong
     * @param errors  field name → error message (currently dropped)
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {
        return new ApiResponse<>(false, message, null, null, null);
    }
}

package com.clinch.crm.authentication.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * The JSON envelope every MyCrm endpoint returns:
 * {@code {"success": true, "message": "...", "data": ..., "errors": {...}, "timestamp": "..."}}.
 *
 * <p>Each service has its own copy of this class. This one is used both to <b>send</b> responses
 * and to <b>read</b> authorization-service's responses through Feign.
 *
 * <ul>
 *   <li>Immutable; create it with the static {@code success}/{@code error} factories.</li>
 *   <li>{@code null} fields are left out of the JSON ({@code NON_NULL}).</li>
 *   <li>Unknown JSON fields are ignored when reading, so the other side can add fields safely.</li>
 *   <li>Lombok {@code @Getter} generates the getters Jackson uses to write the JSON.</li>
 * </ul>
 *
 * @param <T> type of the {@code data} payload
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {

    /** {@code true} for success, {@code false} for an error. */
    private final boolean success;
    /** Human-readable summary. */
    private final String message;
    /** The payload, or {@code null}. */
    private final T data;
    /** Field name → error message, for validation errors; usually {@code null}. */
    private final Map<String, String> errors;
    /** When the response was created (ISO-8601 in JSON). */
    private final Instant timestamp;

    // used by the factories below — stamps "now"
    /**
     * Used by the factories; stamps the current time.
     *
     * @param success whether the call succeeded
     * @param message summary
     * @param data    payload
     * @param errors  per-field errors
     */
    private ApiResponse(boolean success, String message, T data, Map<String, String> errors) {
        this(success, message, data, errors, Instant.now());
    }

    // used by Jackson when reading JSON — keeps the sender's timestamp
    /**
     * Used by Jackson when reading JSON (e.g. a Feign response); keeps the sender's timestamp,
     * or uses "now" if it's missing.
     *
     * @param success   whether the call succeeded
     * @param message   summary
     * @param data      payload
     * @param errors    per-field errors
     * @param timestamp the sender's timestamp, may be {@code null}
     */
    @JsonCreator
    private ApiResponse(
            @JsonProperty("success")   boolean success,
            @JsonProperty("message")   String message,
            @JsonProperty("data")      T data,
            @JsonProperty("errors")    Map<String, String> errors,
            @JsonProperty("timestamp") Instant timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }


    // ---------- success ----------

    /**
     * Success with data and the message "Success".
     *
     * @param data the payload
     * @param <T>  payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null);
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
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * Success with a message and no data.
     *
     * @param message summary
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    // ---------- error ----------

    /**
     * Error with a message only.
     *
     * @param message what went wrong
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    /**
     * Error with a message and per-field errors.
     *
     * @param message what went wrong
     * @param errors  field name → error message, may be {@code null}
     * @param <T>     payload type
     * @return the envelope
     */
    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }
}

package com.clinch.crm.authorization.common;

import com.clinch.crm.authorization.customexception.DuplicateResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * Turns exceptions that escape a controller into {@link ApiResponse} error bodies.
 *
 * <p>{@code @RestControllerAdvice} applies it to every controller. Spring picks the handler with
 * the most specific matching exception type, so {@link #handleGlobal} only catches what the
 * others don't.
 *
 * <table>
 *   <caption>Mapping</caption>
 *   <tr><th>Exception</th><th>Status</th></tr>
 *   <tr><td>{@code MethodArgumentNotValidException} (bad {@code @Valid} body)</td><td>400</td></tr>
 *   <tr><td>{@code ConstraintViolationException} (bad path/query parameter)</td><td>400</td></tr>
 *   <tr><td>{@code IllegalArgumentException}</td><td>400</td></tr>
 *   <tr><td>{@code DuplicateResourceException}</td><td>409</td></tr>
 *   <tr><td>anything else, including {@code ResourceNotFoundException}</td><td>500</td></tr>
 * </table>
 *
 * <p>Many endpoints catch exceptions themselves, so they never reach this class.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * A request body failed {@code @Valid}. Only the <b>first</b> field error is reported, as
     * {@code "field: message"}.
     *
     * @param ex the validation failure
     * @return 400 with the message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ApiResponse error =  ApiResponse.error(

                message,
                null
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Something with the same name already exists.
     *
     * @param ex the exception
     * @return 409 with its message
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate(
            DuplicateResourceException ex) {

        ApiResponse<Object> response =  ApiResponse.error(

                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * A caller passed an invalid argument (e.g. an empty privilege name).
     *
     * @param ex the exception
     * @return 400 with its message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            IllegalArgumentException ex) {

        ApiResponse<Object> response = ApiResponse.error(

                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback for every other exception. The details aren't sent to the client, and they aren't
     * logged here either.
     *
     * @param ex the exception
     * @return 500 "Internal server error"
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobal(Exception ex) {

        ApiResponse error =  ApiResponse.error(

                "Internal server error",
                null
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * A path or query parameter failed a constraint such as {@code @Min} (needs {@code @Validated}
     * on the controller class).
     *
     * @param ex the exception
     * @return 400 with a message like {@code "getAccess.accessId: Access id should be greater than zero"}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(
            ConstraintViolationException ex) {

        String message = ex.getMessage();

        ApiResponse response =  ApiResponse.error(

                message,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

package com.example.tickets_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the whole API.
 * <p>
 * Each method maps a specific exception type to a consistent {@link ErrorResponse}
 * body and HTTP status. Spring selects the handler whose exception type most
 * closely matches the thrown exception in the class hierarchy (not the order in
 * which methods are declared), so the most specific handler always wins. The
 * {@link Exception} handler acts as the catch-all safety net for anything not
 * explicitly mapped.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests for a ticket that does not exist.
     *
     * @param ex the not-found exception carrying the missing ticket id
     * @return a 404 Not Found response
     */
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(TicketNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),   // 404
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles an update whose requested status is not reachable from the ticket's
     * current status (e.g. reopening a CLOSED ticket).
     *
     * @param ex the transition failure, carrying both the current and requested status
     * @return a 409 Conflict response
     */
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),   // 409
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles bean-validation failures on a request body (e.g. a blank title, or a title too long).
     * Aggregates every field error into a single, comma-separated message.
     *
     * @param ex the validation exception holding the per-field errors
     * @return a 400 Bad Request response listing the invalid fields
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>  handleArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles failed login attempts with wrong credentials. The message is kept
     * deliberately generic so it does not reveal whether the username exists.
     *
     * @param ex the authentication failure
     * @return a 401 Unauthorized response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid username or password",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles an unreadable or malformed request body (e.g. invalid JSON syntax).
     *
     * @param ex the parse failure raised while reading the body
     * @return a 400 Bad Request response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed or invalid request body",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles a path variable that cannot be converted to the target type, such
     * as an unknown enum constant (e.g. {@code GET /tickets/status/FOO}). Without
     * this handler the error would fall through to the generic handler and be
     * reported as 500, even though the fault is a malformed client request.
     * <p>
     * When the target type is an enum, the response also lists the accepted
     * constants (e.g. "Expected one of: OPEN, IN_PROGRESS, CLOSED") so the client
     * knows how to fix the request. The {@code requiredType != null} guard is
     * checked first to avoid a NullPointerException inside the handler itself.
     *
     * @param ex the conversion failure, carrying the offending value, the parameter
     *           name and the expected target type
     * @return a 400 Bad Request response naming the invalid value, plus the valid
     *         options when the target type is an enum
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'";

        if (requiredType != null && requiredType.isEnum()) {
            String allowed = Arrays.stream(requiredType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            message += ". Expected one of: " + allowed;
        }
        ErrorResponse error = new ErrorResponse(
               HttpStatus.BAD_REQUEST.value(),
               message,
                Instant.now()
        );

       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Catch-all fallback for any exception not handled above. Returns an opaque
     * message so internal details are never leaked to the client; the real cause
     * should be inspected in the server logs.
     *
     * @param ex the unexpected exception
     * @return a 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected Error",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}

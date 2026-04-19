package com.kikesoft.moviesapi.advice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;

/**
 * Global exception handlers for MVC controllers.
 *
 * @author Enrique Sanchez
 */
@ControllerAdvice
public class GlobalControllerAdvice {
    private static final Logger LOGGER = LogManager.getLogger(GlobalControllerAdvice.class);


    /**
     * Handles not-found errors and returns HTTP 404 with a structured response body.
     *
     * @param infe source exception
     * @return response entity with error payload
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFound(final ItemNotFoundException infe) {
        LOGGER.warn("Handled not-found exception: {}", infe.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", infe.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles duplicate-item errors and returns HTTP 409 with a structured response body.
     *
     * @param die source exception
     * @return response entity with error payload
     */
    @ExceptionHandler(DuplicatedItemException.class)
    public ResponseEntity<Object> handleDuplicatedItem(final DuplicatedItemException die) {
        LOGGER.warn("Handled duplicated-item exception: {}", die.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", die.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles request id mismatch errors and returns HTTP 400 with a structured response body.
     *
     * @param iime source exception
     * @return response entity with error payload
     */
    @ExceptionHandler(ItemIdMismatchException.class)
    public ResponseEntity<Object> handleItemIdMismatch(final ItemIdMismatchException iime) {
        LOGGER.warn("Handled item-id-mismatch exception: {}", iime.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", iime.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles bean validation failures and returns HTTP 400 with field-level errors.
     *
     * @param manve validation exception
     * @return response entity with validation errors by field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException manve) {
        LOGGER.warn("Handled validation exception with {} field errors", manve.getErrorCount());

        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles unexpected errors and returns HTTP 500 with a structured response body.
     *
     * @param ex source exception
     * @return response entity with generic error payload
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(final Exception e) {
        LOGGER.error("Unhandled exception processing request", e);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Internal server error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

package com.kikesoft.moviesapi.exception;

/**
 * Exception thrown when a required field is absent from the request payload.
 */
public class MissingRequiredFieldException extends RuntimeException {

    /**
     * Constructs a new {@code MissingRequiredFieldException} with the specified detail message and cause.
     *
     * @param message the detail message describing the missing field
     * @param cause   the underlying cause of the exception, or {@code null} if none
     */
    public MissingRequiredFieldException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code MissingRequiredFieldException} with the specified detail message.
     *
     * @param message the detail message describing the missing field
     */
    public MissingRequiredFieldException(final String message) {
        this(message, null);
    }

    /**
     * Constructs a new {@code MissingRequiredFieldException} with the default message {@code "Required field is missing"}.
     */
    public MissingRequiredFieldException() {
        this("Required field is missing");
    }
}

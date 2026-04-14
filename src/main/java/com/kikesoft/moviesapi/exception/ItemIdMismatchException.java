package com.kikesoft.moviesapi.exception;

/**
 * Exception thrown when the resource identifier in the path does not match the identifier in the request payload.
 */
public class ItemIdMismatchException extends RuntimeException {

    /**
     * Constructs a new {@code ItemIdMismatchException} with the specified detail message and cause.
     *
     * @param message the detail message describing the id mismatch
     * @param cause   the underlying cause of the exception, or {@code null} if none
     */
    public ItemIdMismatchException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code ItemIdMismatchException} with the specified detail message.
     *
     * @param message the detail message describing the id mismatch
     */
    public ItemIdMismatchException(final String message) {
        this(message, null);
    }

    /**
     * Constructs a new {@code ItemIdMismatchException} with the default message {@code "Item id mismatch"}.
     */
    public ItemIdMismatchException() {
        this("Item id mismatch");
    }
}
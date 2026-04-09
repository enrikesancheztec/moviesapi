package com.kikesoft.moviesapi.exception;

/**
 * Exception thrown when a requested item cannot be found in the data source.
 */
public class ItemNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ItemNotFoundException} with the specified detail message and cause.
     *
     * @param message the detail message describing the missing item
     * @param cause   the underlying cause of the exception, or {@code null} if none
     */
    public ItemNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
        // TODO: Log the exception
    }

    /**
     * Constructs a new {@code ItemNotFoundException} with the specified detail message.
     *
     * @param message the detail message describing the missing item
     */
    public ItemNotFoundException(final String message) {
        this(message, null);
    }

    /**
     * Constructs a new {@code ItemNotFoundException} with the default message {@code "Item not found"}.
     */
    public ItemNotFoundException() {
        this("Item not found");
    }
}

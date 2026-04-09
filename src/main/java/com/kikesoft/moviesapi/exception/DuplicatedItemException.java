package com.kikesoft.moviesapi.exception;

/**
 * Exception thrown when an attempt is made to create or insert an item that already exists in the data source.
 */
public class DuplicatedItemException extends RuntimeException {

    /**
     * Constructs a new {@code DuplicatedItemException} with the specified detail message and cause.
     *
     * @param message the detail message describing the duplicated item
     * @param cause   the underlying cause of the exception, or {@code null} if none
     */
    public DuplicatedItemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code DuplicatedItemException} with the specified detail message.
     *
     * @param message the detail message describing the duplicated item
     */
    public DuplicatedItemException(final String message) {
        this(message, null);
    }

    /**
     * Constructs a new {@code DuplicatedItemException} with the default message {@code "Item already exists"}.
     */
    public DuplicatedItemException() {
        this("Item already exists");
    }
}

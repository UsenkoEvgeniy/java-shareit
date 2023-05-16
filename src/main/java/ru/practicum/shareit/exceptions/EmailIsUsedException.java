package ru.practicum.shareit.exceptions;

public class EmailIsUsedException extends RuntimeException {
    public EmailIsUsedException(String message) {
        super("Email must be unique: " + message);
    }
}

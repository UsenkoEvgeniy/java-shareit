package ru.practicum.shareit.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super("User doesn't exist for id " + message);
    }
}

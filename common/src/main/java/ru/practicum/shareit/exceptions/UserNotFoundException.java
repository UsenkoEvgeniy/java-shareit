package ru.practicum.shareit.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("User doesn't exist for id " + userId);
    }
}

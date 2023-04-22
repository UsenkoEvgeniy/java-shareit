package ru.practicum.shareit.exceptions;

public class WrongUserException extends RuntimeException {
    public WrongUserException() {
    }

    public WrongUserException(String message) {
        super(message);
    }
}

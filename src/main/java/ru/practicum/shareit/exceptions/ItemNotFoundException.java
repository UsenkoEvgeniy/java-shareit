package ru.practicum.shareit.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
    }

    public ItemNotFoundException(String message) {
        super("Item doesn't exist for id " + message);
    }
}

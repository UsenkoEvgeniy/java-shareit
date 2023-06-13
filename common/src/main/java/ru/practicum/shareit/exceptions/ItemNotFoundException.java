package ru.practicum.shareit.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String itemId) {
        super("Item doesn't exist for id " + itemId);
    }
}

package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item get(long id);

    Collection<Item> getAllForUser(long userId);

    Collection<Item> getAvailable(String text);

}

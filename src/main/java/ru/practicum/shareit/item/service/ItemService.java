package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto get(long itemId);

    Collection<ItemDto> getAllForUser(long userId);

    Collection<ItemDto> getAvailable(String text);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithCommentsAndBookings;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDtoWithCommentsAndBookings get(long itemId, long id);

    Collection<ItemDtoWithCommentsAndBookings> getAllForOwner(long userId);

    Collection<ItemDto> getAvailable(String text);

    CommentDto createComment(long userId, Comment comment, long itemId);
}

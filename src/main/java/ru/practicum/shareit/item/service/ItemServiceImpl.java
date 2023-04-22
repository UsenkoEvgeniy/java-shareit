package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = new Item();
        if (!userService.isExist(userId)) {
            log.warn("User {} is not exist", userId);
            throw new UserNotFoundException(String.valueOf(userId));
        }
        item.setOwnerId(userId);
        log.debug("Creating item {}", itemDto);
        Item itemCreated = itemStorage.create(ItemMapper.mapToItem(itemDto, item));
        return ItemMapper.mapToDto(itemCreated);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemStorage.get(itemId);
        if (userId != item.getOwnerId()) {
            log.warn("User {} is not owner of item {}", userId, itemId);
            throw new WrongUserException("Item update can be performed only by owner");
        }
        ItemMapper.mapToItem(itemDto, item);
        log.debug("Updating item {}", itemId);
        Item itemUpdated = itemStorage.update(item);
        return ItemMapper.mapToDto(itemUpdated);
    }

    @Override
    public ItemDto get(long itemId) {
        log.debug("Getting item for id {}", itemId);
        Item item = itemStorage.get(itemId);
        if (item == null) {
            log.warn("Item doesn't exist for id {}", itemId);
            throw new ItemNotFoundException(String.valueOf(itemId));
        }
        return ItemMapper.mapToDto(item);
    }

    @Override
    public Collection<ItemDto> getAllForUser(long userId) {
        log.debug("Getting all items of user {}", userId);
        return itemStorage.getAllForUser(userId).stream().map(ItemMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getAvailable(String text) {
        if (text.length() == 0) {
            log.debug("Empty search text");
            return Collections.emptyList();
        }
        log.debug("Getting all available items for search text {}", text);
        return itemStorage.getAvailable(text).stream().map(ItemMapper::mapToDto).collect(Collectors.toList());
    }
}

package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithCommentsAndBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final CommentStorage commentStorage;

    public ItemServiceImpl(ItemStorage itemStorage, BookingStorage bookingStorage, UserService userService, CommentStorage commentStorage) {
        this.itemStorage = itemStorage;
        this.bookingStorage = bookingStorage;
        this.userService = userService;
        this.commentStorage = commentStorage;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (!userService.isExist(userId)) {
            log.warn("User {} is not exist", userId);
            throw new UserNotFoundException(String.valueOf(userId));
        }
        Item item = new Item();
        item.setOwnerId(userId);
        log.debug("Creating item {}", itemDto);
        Item itemCreated = itemStorage.save(ItemMapper.mapToItem(itemDto, item));
        return ItemMapper.mapToDto(itemCreated);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemStorage.getReferenceById(itemId);
        if (userId != item.getOwnerId()) {
            log.warn("User {} is not owner of item {}", userId, itemId);
            throw new WrongUserException("Item update can be performed only by owner");
        }
        ItemMapper.mapToItem(itemDto, item);
        log.debug("Updating item {}", itemId);
        Item itemUpdated = itemStorage.save(item);
        return ItemMapper.mapToDto(itemUpdated);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoWithCommentsAndBookings get(long userId, long itemId) {
        log.debug("Getting item for id {}", itemId);
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String.valueOf(itemId)));
        List<Booking> bookingList = Collections.emptyList();
        if (item.getOwnerId() == userId) {
            bookingList = bookingStorage.findByItemId(itemId);
        }
        return ItemMapper.mapToDtoWithComments(item, commentStorage.findByItem_Id(itemId), bookingList);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDtoWithCommentsAndBookings> getAllForOwner(long userId, Integer from, Integer size) {
        log.debug("Getting all items of user {}", userId);
        Collection<Item> items = itemStorage.findByOwnerId(userId, PageRequest.of(from / size, size, Sort.by("id")));
        Map<Long, List<Booking>> bookingMap = bookingStorage.findByItemOwnerId(userId, items).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> commentMap = commentStorage.findByItem_OwnerIdAndItemIn(userId, items).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        return items.stream()
                .map(item -> ItemMapper.mapToDtoWithComments(item, commentMap.get(item.getId()), bookingMap.get(item.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> getAvailable(String text, Integer from, Integer size) {
        if (text.length() == 0) {
            log.debug("Empty search text");
            return Collections.emptyList();
        }
        log.debug("Getting all available items for search text {}", text);
        return itemStorage.findAvailable(text, PageRequest.of(from / size, size)).stream().map(ItemMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(long userId, Comment comment, long itemId) {
        User author = userService.findById(userId);
        if (!bookingStorage.existsByBooker_IdAndEndBeforeAndStatus(userId, LocalDateTime.now(), BookingStatus.APPROVED)) {
            throw new BadRequestException("User " + userId + " hasn't rent the item");
        }
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setItem(itemStorage.findById(itemId).orElseThrow());
        log.debug("Saving comment from user {} to item {}", userId, itemId);
        return CommentMapper.mapToDto(commentStorage.save(comment));
    }
}

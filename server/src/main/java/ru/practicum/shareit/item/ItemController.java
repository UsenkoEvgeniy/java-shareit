package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithCommentsAndBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Post request from userId {} for item{}", userId, itemDto);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID) long userId, @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        log.info("Patch request from userId {} for item {}", userId, itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithCommentsAndBookings get(@RequestHeader(USER_ID) long userId, @PathVariable long itemId) {
        log.info("Get request for itemId {}", itemId);
        return itemService.get(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDtoWithCommentsAndBookings> getAllForUser(@RequestHeader(USER_ID) long userId,
                                                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                                    @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.info("Get all items of user {}, from {}, size {}", userId, from, size);
        return itemService.getAllForOwner(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAvailable(@RequestParam String text,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.info("Search request with text = {}, from {}, size {}", text, from, size);
        return itemService.getAvailable(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID) long userId, @RequestBody Comment comment,
                                    @PathVariable long itemId) {
        log.info("Post request from user {} to create comment {} for item {}", userId, comment, itemId);
        return itemService.createComment(userId, comment, itemId);
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.marker.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping("/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Post request from userId {} for item{}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID) long userId, @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        log.info("Patch request from userId {} for item {}", userId, itemId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) long userId, @PathVariable long itemId) {
        log.info("Get request for itemId {}", itemId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@RequestHeader(USER_ID) long userId,
                                                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                                    @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.info("Get all items of user {}, from {}, size {}", userId, from, size);
        return itemClient.getAllForOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailable(@RequestParam String text,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "20") @Min(1) Integer size,
                                            @RequestHeader(USER_ID) long userId) {
        log.info("Search request with text = {}, from {}, size {}", text, from, size);
        return itemClient.getAvailable(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID) long userId, @RequestBody CommentDto comment,
                                    @PathVariable long itemId) {
        log.info("Post request from user {} to create comment {} for item {}", userId, comment, itemId);
        return itemClient.createComment(userId, comment, itemId);
    }
}

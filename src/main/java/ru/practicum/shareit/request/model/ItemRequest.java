package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

public class ItemRequest {
    private Long id;
    private User requestor;
    private String description;
    private LocalDateTime created;
}

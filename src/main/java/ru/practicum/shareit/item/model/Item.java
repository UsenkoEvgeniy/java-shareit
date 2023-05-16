package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Data
public class Item {
    private Long id;
    @NotNull
    private Long ownerId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean isAvailable = false;
    private HashMap<User, String> reviews = new HashMap<>();
}

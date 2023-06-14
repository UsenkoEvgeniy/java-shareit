package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.marker.OnCreate;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotEmpty(groups = OnCreate.class)
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}

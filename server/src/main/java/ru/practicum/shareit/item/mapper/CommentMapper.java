package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto mapToDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                ItemMapper.mapToDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}

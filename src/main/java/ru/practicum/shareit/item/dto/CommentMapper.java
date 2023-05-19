package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto mapToDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}

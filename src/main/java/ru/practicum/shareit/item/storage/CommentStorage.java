package ru.practicum.shareit.item.storage;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface CommentStorage extends CrudRepository<Comment, Long> {
    List<Comment> findByItem_OwnerIdAndItemIn(Long ownerId, Collection<Item> items);

    List<Comment> findByItem_Id(Long id);
}

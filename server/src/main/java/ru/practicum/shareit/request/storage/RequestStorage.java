package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestStorage extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestor_IdNotOrderByCreatedDesc(long id, Pageable pageable);

    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(long id);
}

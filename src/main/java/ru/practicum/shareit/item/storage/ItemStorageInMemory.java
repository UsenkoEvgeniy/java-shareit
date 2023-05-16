package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long counter = 1L;

    @Override
    public Item create(Item item) {
        long id = counter++;
        item.setId(id);
        items.put(id, item);
        return get(id);
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return get(item.getId());
    }

    @Override
    public Item get(long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> getAllForUser(long userId) {
        return items.values().stream().filter(i -> i.getOwnerId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAvailable(String text) {
        return items.values().stream()
                .filter(i -> i.isAvailable() && i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}

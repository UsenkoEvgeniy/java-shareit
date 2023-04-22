package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long counter = 1L;

    @Override
    public User get(long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        long id = counter++;
        user.setId(id);
        users.put(id, user);
        return get(id);
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public boolean delete(long id) {
        User userDeleted = users.remove(id);
        return userDeleted != null;
    }

    @Override
    public boolean isExist(long id) {
        return users.get(id) != null;
    }

    @Override
    public boolean isEmailExist(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
}
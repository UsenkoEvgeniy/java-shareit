package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
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
        user.setId(counter++);
        users.put(user.getId(), user);
        emails.add(user.getEmail().toLowerCase());
        return get(user.getId());
    }

    @Override
    public User update(User user) {
        if (!users.get(user.getId()).getEmail().equalsIgnoreCase(user.getEmail())) {
            emails.remove(users.get(user.getId()).getEmail().toLowerCase());
            emails.add(user.getEmail().toLowerCase());
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public boolean delete(long id) {
        if (users.containsKey(id)) {
            emails.remove(users.get(id).getEmail());
        }
        return users.remove(id) != null;
    }

    @Override
    public boolean isExist(long id) {
        return users.get(id) != null;
    }

    @Override
    public boolean isEmailExist(String email) {
        return emails.contains(email.toLowerCase());
    }
}
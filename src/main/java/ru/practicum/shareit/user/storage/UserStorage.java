package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {
    User get(long id);

    Collection<User> getAll();

    User create(User user);

    User update(User user);

    boolean delete(long id);

    boolean isExist(long id);

    boolean isEmailExist(String email);
}
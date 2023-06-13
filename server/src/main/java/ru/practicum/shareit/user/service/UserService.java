package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailIsUsedException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserService {
    private final UserStorage userStorage;
    private final Validator validator;

    public UserService(UserStorage userStorage, Validator validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public Collection<UserDto> getAll() {
        log.debug("Getting all users");
        return userStorage.findAll().stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getById(long id) {
        log.debug("Getting userDto for id {}", id);
        User user = userStorage.findById(id).orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
        return UserMapper.mapToDto(user);
    }

    @Transactional(readOnly = true)
    public User findById(long id) {
        log.debug("Getting user for id {}", id);
        return userStorage.findById(id).orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    public UserDto create(User user) {
        log.debug("Creating user {}", user);
        try {
            return UserMapper.mapToDto(userStorage.save(user));
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause() instanceof PSQLException) {
                String message = e.getMostSpecificCause().getMessage();
                log.warn("Email is not unique {}", message);
                throw new EmailIsUsedException(message);
            } else {
                throw e;
            }
        }
    }

    public UserDto update(long id, UserDto userFields) {
        User userFromDb = userStorage.findById(id).orElseThrow();
        User updatedUser = UserMapper.mapToUser(userFields, userFromDb);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(updatedUser);
        if (!constraintViolations.isEmpty()) {
            log.warn("Bad user fields");
            throw new ValidationException("Bad user fields");
        }
        log.debug("Updating user {}", id);
        return UserMapper.mapToDto(userStorage.save(updatedUser));
    }

    @Transactional(readOnly = true)
    public boolean isExist(long userId) {
        log.debug("Check if user {} exists", userId);
        return userStorage.existsById(userId);
    }

    public void delete(long id) {
        log.debug("Deleting user {}", id);
        userStorage.deleteById(id);
    }
}
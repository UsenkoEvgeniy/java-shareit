package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailIsUsedException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<UserDto> getAll() {
        log.debug("Getting all users");
        return userStorage.getAll().stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    public UserDto getById(long id) {
        log.debug("Getting user for id {}", id);
        User user = userStorage.get(id);
        if (user == null) {
            log.warn("User doesn't exist for id {}", id);
            throw new UserNotFoundException(String.valueOf(id));
        }
        return UserMapper.mapToDto(user);
    }

    public UserDto create(UserDto userDto) {
        if (userStorage.isEmailExist(userDto.getEmail())) {
            log.warn("Email {} is already in use", userDto.getEmail());
            throw new EmailIsUsedException(userDto.getEmail());
        }
        User user = UserMapper.mapToUser(userDto, new User());
        log.debug("Creating user {}", userDto);
        User userCreated = userStorage.create(user);
        return UserMapper.mapToDto(userCreated);
    }

    public UserDto update(long id, UserDto userFields) {
        User userFromDb = userStorage.get(id);
        String email = userFields.getEmail();
        if (!userFromDb.getEmail().equalsIgnoreCase(email) && userStorage.isEmailExist(email)) {
            log.warn("Email {} is already in use", email);
            throw new EmailIsUsedException(email);
        }
        UserMapper.mapToUser(userFields, userFromDb);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(userFromDb);
        if (!constraintViolations.isEmpty()) {
            log.warn("Bad user fields");
            throw new ValidationException("Bad user fields");
        }
        log.debug("Updating user {}", id);
        User userUpdated = userStorage.update(userFromDb);
        return UserMapper.mapToDto(userUpdated);
    }

    public boolean isExist(long userId) {
        log.debug("Check if user {} exists", userId);
        return userStorage.isExist(userId);
    }

    public void delete(long id) {
        log.debug("Deleting user {}", id);
        if (!userStorage.delete(id)) {
            log.warn("User {} is not found", id);
            throw new UserNotFoundException(String.valueOf(id));
        }
    }
}
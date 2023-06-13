package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exceptions.EmailIsUsedException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;
    @Mock
    private Validator validator;

    @InjectMocks
    private UserService userService;

    @Test
    void getAll_whenUsersFound_thenReturnUsers() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(userStorage.findAll()).thenReturn(users);

        Collection<UserDto> actualUsers = userService.getAll();

        assertEquals(2, actualUsers.size());
        assertEquals(UserMapper.mapToDto(user1), actualUsers.iterator().next());
    }

    @Test
    void getAll_whenUsersNotFound_thenEmptyCollection() {
        when(userStorage.findAll()).thenReturn(Collections.emptyList());

        Collection<UserDto> actualUsers = userService.getAll();

        assertEquals(0, actualUsers.size());
    }

    @Test
    void getById_whenUserFound_thenReturnUser() {
        long userId = 0L;
        when(userStorage.findById(userId)).thenReturn(Optional.of(new User()));
        UserDto userDto = new UserDto();
        userDto.setId(0L);

        UserDto actualUserDto = userService.getById(userId);

        assertEquals(userDto, actualUserDto);
    }

    @Test
    void getById_whenUserNotFound_thenThrowUserNotFoundException() {
        long userId = 0L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
        assertEquals("User doesn't exist for id 0", e.getMessage());
    }

    @Test
    void findById_whenUserFound_thenReturnUser() {
        long userId = 0L;
        User user = new User();
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        User actualUser = userService.findById(userId);

        assertEquals(user, actualUser);
    }

    @Test
    void findById_whenUserNotFound_thenThrowUserNotFoundException() {
        long userId = 0L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        assertEquals("User doesn't exist for id 0", e.getMessage());
    }

    @Test
    void createUser_whenUserValid_thenReturnSavedUser() {
        User userToSave = new User();
        when(userStorage.save(userToSave)).thenReturn(userToSave);

        UserDto actualuser = userService.create(userToSave);

        assertEquals(UserMapper.mapToDto(userToSave), actualuser);
        verify(userStorage).save(userToSave);
    }

    @Test
    void createUser_whenUserEmailNotValid_thenThrowException() {
        User userToSave = new User();
        DataIntegrityViolationException e = new DataIntegrityViolationException("", new PSQLException("", PSQLState.UNKNOWN_STATE));
        when(userStorage.save(userToSave)).thenThrow(e);

        assertThrows(EmailIsUsedException.class, () -> userService.create(userToSave));
    }

    @Test
    void update_whenUserIsValid_thenUpdate() {
        User expectedUser = new User();
        UserDto expectedUserDto = UserMapper.mapToDto(expectedUser);
        long id = 0L;
        when(userStorage.findById(id)).thenReturn(Optional.of(expectedUser));
        when(userStorage.save(expectedUser)).thenReturn(expectedUser);

        UserDto actualUserDto = userService.update(id, expectedUserDto);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void update_whenUserIsNotExist_thenThrowException() {
        long id = 99L;
        UserDto expectedUser = new UserDto();
        when(userStorage.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.update(id, expectedUser));
        verify(userStorage, never()).save(any());
    }

    @Test
    void isExist_whenExist_returnTrue() {
        long id = 0L;
        boolean expected = true;
        when(userStorage.existsById(anyLong())).thenReturn(expected);

        boolean actual = userService.isExist(id);

        assertTrue(actual);
    }

    @Test
    void isExist_whenNotExist_returnFalse() {
        long id = 0L;
        boolean expected = false;
        when(userStorage.existsById(anyLong())).thenReturn(expected);

        boolean actual = userService.isExist(id);

        assertFalse(actual);
    }

    @Test
    void delete_whenNotExist_throwException() {
        long id = 0L;
        doThrow(EmptyResultDataAccessException.class).when(userStorage).deleteById(id);

        assertThrows(EmptyResultDataAccessException.class, () -> userService.delete(id));
    }
}
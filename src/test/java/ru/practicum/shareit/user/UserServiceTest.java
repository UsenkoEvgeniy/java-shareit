package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.EmailIsUsedException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    private final UserService userService;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @Test
    void getById() {
        UserDto user = userService.create(new UserDto("Name", "u@mail.com"));
        assertEquals(user, userService.getById(user.getId()), "User in db is different");
        assertThrows(UserNotFoundException.class, () -> userService.getById(-1), "Found user with wrong id");
    }

    @Test
    void create() {
        UserDto user = userService.create(new UserDto("Name4", "u4@mail.com"));
        assertEquals(user, userService.getById(user.getId()), "Created user is different");
        assertThrows(EmailIsUsedException.class, () -> userService.create(user), "User with same email created");
    }

    @Test
    void getAll() {
        UserDto user = userService.create(new UserDto("Name2", "u2@mail.com"));
        UserDto user2 = userService.create(new UserDto("Name3", "u3@mail.com"));
        assertEquals(2, userService.getAll().size(), "Wrong list size");
    }

    @Test
    void update() {
        UserDto user = userService.create(new UserDto("Name2", "u@mail.com"));
        UserDto user2 = userService.create(new UserDto("Name3", "u2@mail.com"));
        UserDto userDto = new UserDto();
        userDto.setName("Updated name");
        UserDto updatedUser = userService.update(2, userDto);
        assertEquals("Updated name", updatedUser.getName(), "Updated name is different");
        userDto.setEmail("u@mail.com");
        assertThrows(EmailIsUsedException.class, () -> userService.update(2, userDto),
                "Updated with non unique email");
        userDto.setEmail("email");
        assertThrows(ValidationException.class, () -> userService.update(2, userDto),
                "Updated with wrong email");
        userDto.setName("");
        assertThrows(ValidationException.class, () -> userService.update(2, userDto),
                "Updated with wrong name");
    }

    @Test
    void isExist() {
        UserDto user = userService.create(new UserDto("Name2", "u2@mail.com"));
        assertFalse(userService.isExist(-1), "Found user with wrong id");
        assertTrue(userService.isExist(1), "Doesn't found user with right id");
    }

    @Test
    void delete() {
        UserDto user = userService.create(new UserDto("Name2", "u2@mail.com"));
        userService.delete(1);
        assertEquals(Collections.emptyList(), userService.getAll(), "Wrong list size");
        assertThrows(UserNotFoundException.class, () -> userService.delete(-1), "Deleted with wrong id");
    }
}
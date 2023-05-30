package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void mapToUser() {
        User user = new User();
        UserDto userDto = new UserDto();
        user.setId(1L);
        userDto.setName("Name");
        userDto.setEmail("3@email.com");
        User actual = UserMapper.mapToUser(userDto, user);

        assertEquals(userDto.getName(), actual.getName());
    }
}
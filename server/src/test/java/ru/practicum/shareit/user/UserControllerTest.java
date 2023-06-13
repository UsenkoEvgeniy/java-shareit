package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EmailIsUsedException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void getAll() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getById() {
        long userId = 2L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void getById_whenUserNotFound_returnNotFound() {
        long userId = 2L;
        when(userService.getById(userId)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void create() {
        User userToCreate = new User();
        userToCreate.setName("testName");
        userToCreate.setEmail("test@email.com");
        when(userService.create(userToCreate)).thenReturn(UserMapper.mapToDto(userToCreate));

        String response = mockMvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(UserMapper.mapToDto(userToCreate)), response);
    }

    @SneakyThrows
    @Test
    void create_whenSameEmail() {
        User userToCreate = new User();
        userToCreate.setName("testName");
        userToCreate.setEmail("test@email.com");
        when(userService.create(userToCreate)).thenThrow(EmailIsUsedException.class);

        mockMvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isConflict());

        verify(userService).create(userToCreate);
    }

    @SneakyThrows
    @Test
    void create_whenUserNotValid_returnBadRequest() {
        User userToCreate = new User();

        mockMvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(userToCreate);
    }

    @SneakyThrows
    @Test
    void update() {
        long userId = 2L;
        UserDto userToUpdate = new UserDto();
        when(userService.update(userId, userToUpdate)).thenReturn(userToUpdate);

        String response = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToUpdate), response);
    }

    @SneakyThrows
    @Test
    void update_whenBadUserDields() {
        long userId = 2L;
        UserDto userToUpdate = new UserDto();
        when(userService.update(userId, userToUpdate)).thenThrow(BadRequestException.class);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService).update(userId, userToUpdate);
    }

    @SneakyThrows
    @Test
    void deleteById() {
        long userId = 2L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}
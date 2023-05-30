package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void create() {
        long userId = 2L;
        ItemDto itemToCreate = new ItemDto();
        itemToCreate.setName("testName");
        itemToCreate.setDescription("testDesc");
        itemToCreate.setAvailable(true);
        when(itemService.create(userId, itemToCreate)).thenReturn(itemToCreate);

        String response = mockMvc.perform(post("/items")
                        .header(USER_ID, userId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @SneakyThrows
    @Test
    void create_whenItemNotValid_returnBadRequest() {
        long userId = 2L;
        ItemDto itemToCreate = new ItemDto();

        mockMvc.perform(post("/items")
                        .header(USER_ID, userId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(userId, itemToCreate);
    }

    @SneakyThrows
    @Test
    void update() {
        long userId = 2L;
        long itemId = 1L;
        ItemDto itemToCreate = new ItemDto();
        itemToCreate.setName("testName");
        itemToCreate.setDescription("testDesc");
        itemToCreate.setAvailable(true);
        when(itemService.update(userId, itemId, itemToCreate)).thenReturn(itemToCreate);

        String response = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID, userId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @SneakyThrows
    @Test
    void update_whenNotOwner() {
        long userId = 2L;
        long itemId = 1L;
        ItemDto itemToCreate = new ItemDto();
        itemToCreate.setName("testName");
        itemToCreate.setDescription("testDesc");
        itemToCreate.setAvailable(true);
        when(itemService.update(userId, itemId, itemToCreate)).thenThrow(WrongUserException.class);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID, userId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isForbidden());

        verify(itemService).update(userId, itemId, itemToCreate);
    }

    @SneakyThrows
    @Test
    void get() {
        long userId = 2L;
        long itemId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).get(userId, itemId);
    }

    @SneakyThrows
    @Test
    void getAllForUser() {
        long userId = 2L;
        int from = 0;
        int size = 20;

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).getAllForOwner(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getAvailable() {
        long userId = 2L;
        int from = 0;
        int size = 20;
        String text = "test";

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text={text}", text)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).getAvailable(text, from, size);
    }

    @SneakyThrows
    @Test
    void createComment() {
        long userId = 2L;
        long itemId = 1L;
        Comment comment = new Comment();
        comment.setAuthor(new User());
        when(itemService.createComment(userId, comment, itemId)).thenReturn(CommentMapper.mapToDto(comment));

        String response = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(CommentMapper.mapToDto(comment)), response);
    }
}
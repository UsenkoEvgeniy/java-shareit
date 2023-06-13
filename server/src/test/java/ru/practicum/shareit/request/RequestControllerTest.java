package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    private static final String USER_ID = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void create() {
        RequestDto requestToCreate = new RequestDto();
        requestToCreate.setDescription("test");
        long userId = 1L;
        when(requestService.create(userId, requestToCreate)).thenReturn(requestToCreate);

        String response = mockMvc.perform(post("/requests")
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requestToCreate), response);
    }

    @SneakyThrows
    @Test
    void create_whenEmptyDescriptionRequest_thenBadRequest() {
        RequestDto requestToCreate = new RequestDto();
        long userId = 1L;
        when(requestService.create(userId, requestToCreate)).thenReturn(requestToCreate);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestToCreate)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).create(userId, requestToCreate);
    }

    @SneakyThrows
    @Test
    void getRequestsForUser() {
        long userId = 2L;
        mockMvc.perform(get("/requests")
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(requestService).getRequestsForUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        long userId = 1L;
        int from = 0;
        int size = 20;
        mockMvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(requestService).getAll(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getRequest() {
        long requestId = 1L;
        long userId = 2L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(requestService).getRequest(userId, requestId);
    }
}
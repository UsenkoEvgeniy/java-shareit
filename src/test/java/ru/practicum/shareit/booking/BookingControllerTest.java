package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @SneakyThrows
    void create() {
        BookingDto bookingToCreate = new BookingDto();
        long userId = 2L;
        when(bookingService.create(bookingToCreate, userId)).thenReturn(bookingToCreate);

        String response = mockMvc.perform(post("/bookings")
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }

    @SneakyThrows
    @Test
    void setStatus() {
        long userId = 2L;
        long bookingId = 1L;
        boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).setStatus(bookingId, approved, userId);
    }

    @SneakyThrows
    @Test
    void fingBooking() {
        long userId = 2L;
        long bookingId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).findBooking(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void findBookingsForUser() {
        long userId = 2L;
        String state = "ALL";
        int from = 0;
        int size = 20;

        mockMvc.perform(get("/bookings?state={state}&from={from}&size={size}", state, from, size)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).findBookingsForUserOrOwner(userId, BookingState.valueOf(state), false, from, size);
    }

    @SneakyThrows
    @Test
    void findBookingsForUser_whenNotEnumValue_returnBadRequest() {
        long userId = 2L;
        String state = "UNDEFINED";
        int from = 0;
        int size = 20;

        mockMvc.perform(get("/bookings?state={state}&from={from}&size={size}", state, from, size)
                        .header(USER_ID, userId))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsForUserOrOwner(userId, BookingState.ALL, false, from, size);
    }

    @SneakyThrows
    @Test
    void findBookingsForOwner() {
        long userId = 2L;
        String state = "ALL";
        int from = 0;
        int size = 20;

        mockMvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", state, from, size)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).findBookingsForUserOrOwner(userId, BookingState.valueOf(state), true, from, size);
    }
}
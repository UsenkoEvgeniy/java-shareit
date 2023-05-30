package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestStorage requestStorage;
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private RequestService requestService;

    @Test
    void create_whenUserExist_thenCallSave() {
        long id = 0L;
        when(userStorage.findById(id)).thenReturn(Optional.of(new User()));
        RequestDto expectedRequestDto = RequestMapper.toRequestDto(new ItemRequest());
        ItemRequest expectedRequest = RequestMapper.toRequest(expectedRequestDto);
        when(requestStorage.save(any(ItemRequest.class))).thenReturn(expectedRequest);

        RequestDto actualRequestDto = requestService.create(id, expectedRequestDto);

        verify(requestStorage).save(any(ItemRequest.class));
    }

    @Test
    void create_whenUserNotExist_thenThrowException() {
        long id = 0L;
        RequestDto requestDto = new RequestDto();
        when(userStorage.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> requestService.create(id, requestDto));
        verify(requestStorage, never()).save(any());
    }

    @Test
    void getRequestsForUser_whenUserNotExist_thenThrowException() {
        long id = 0L;
        when(userStorage.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> requestService.getRequestsForUser(id));
    }

    @Test
    void getRequestsForUser_whenUserExist_thenReturnCollection() {
        long id = 0L;
        when(userStorage.existsById(id)).thenReturn(true);
        when(requestStorage.findByRequestor_IdOrderByCreatedDesc(id)).thenReturn(Collections.emptyList());

        Collection<RequestDto> requests = requestService.getRequestsForUser(id);

        assertEquals(0, requests.size());
    }

    @Test
    void getAll_whenUserNotExist_thenThrowException() {
        long id = 0L;
        int from = 0;
        int size = 0;
        when(userStorage.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> requestService.getAll(id, from, size));
    }

    @Test
    void getAll_whenUserExist_thenReturnCollection() {
        long id = 0L;
        int from = 1;
        int size = 1;
        ItemRequest itemRequest1 = new ItemRequest();
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest1);
        when(userStorage.existsById(id)).thenReturn(true);
        when(requestStorage.findByRequestor_IdNotOrderByCreatedDesc(id, PageRequest.of(from, size))).thenReturn(requests);

        Collection<RequestDto> actualRequests = requestService.getAll(id, from, size);

        assertEquals(size, actualRequests.size());
        assertEquals(RequestMapper.toRequestDto(itemRequest1), actualRequests.iterator().next());
    }

    @Test
    void getRequest_whenUserNotExist_thenThrowException() {
        long id = 0L;
        long requestId = 0L;
        when(userStorage.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> requestService.getRequest(id, requestId));
    }

    @Test
    void getRequest_whenRequestNotExist_thenThrowException() {
        long id = 0L;
        long requestId = 0L;
        when(userStorage.existsById(id)).thenReturn(true);
        when(requestStorage.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> requestService.getRequest(id, requestId));
    }

    @Test
    void getRequest_whenUserAndRequestExist_thenReturn() {
        long id = 0L;
        long requestId = 0L;
        ItemRequest request = new ItemRequest();
        when(userStorage.existsById(id)).thenReturn(true);
        when(requestStorage.findById(requestId)).thenReturn(Optional.of(request));

        RequestDto actualRequestDto = requestService.getRequest(id, requestId);

        assertEquals(RequestMapper.toRequestDto(request), actualRequestDto);
    }
}
package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    public RequestService(RequestStorage requestStorage, UserStorage userStorage) {
        this.requestStorage = requestStorage;
        this.userStorage = userStorage;
    }

    public RequestDto create(Long userId, RequestDto requestDto) {
        User user = userStorage.findById(userId).orElseThrow();
        requestDto.setRequestor(UserMapper.mapToDto(user));
        requestDto.setCreated(LocalDateTime.now());
        ItemRequest request = RequestMapper.toRequest(requestDto);
        log.debug("Creating request {}", request);
        return RequestMapper.toRequestDto(requestStorage.save(request));
    }

    @Transactional(readOnly = true)
    public Collection<RequestDto> getRequestsForUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException(String.valueOf(userId));
        }
        log.debug("Getting all requests from user {}", userId);
        Collection<ItemRequest> requests = requestStorage.findByRequestor_IdOrderByCreatedDesc(userId);
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<RequestDto> getAll(Long userId, Integer from, Integer size) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException(String.valueOf(userId));
        }
        int page = from / size;
        log.debug("Get all requests page {}, size {}", from, size);
        List<ItemRequest> requests = requestStorage.findByRequestor_IdNotOrderByCreatedDesc(userId, PageRequest.of(page, size));
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RequestDto getRequest(Long userId, Long requestId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException(String.valueOf(userId));
        }
        ItemRequest request = requestStorage.findById(requestId).orElseThrow();
        log.debug("Get request id {}", requestId);
        return RequestMapper.toRequestDto(request);
    }
}

package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/requests")
@Validated
public class RequestController {
    private final RequestService requestService;
    private static final String USER_ID = "X-Sharer-User-Id";

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public RequestDto create(@RequestHeader(USER_ID) Long userId,@Valid @RequestBody RequestDto request) {
        log.info("Post request to create request: {}", request);
        return requestService.create(userId, request);
    }

    @GetMapping
    public Collection<RequestDto> getRequestsForUser(@RequestHeader(USER_ID) Long userId) {
        log.info("Get request for itemRequests from user {}", userId);
        return requestService.getRequestsForUser(userId);
    }

    @GetMapping("/all")
    public Collection<RequestDto> getAllRequests(@RequestHeader(USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "1") @Min(1) Integer size) {
        log.info("Get all itemRequests from {}, size {}", from, size);
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader(USER_ID) Long userId, @PathVariable Long requestId) {
        log.info("Get request for requestId {} from userId {}", requestId, userId);
        return requestService.getRequest(userId, requestId);
    }
}

package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public class RequestMapper {
    public static ItemRequest toRequest(RequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        if (requestDto.getId() != null) {
            request.setId(requestDto.getId());
        }
        if (requestDto.getRequestor() != null) {
            request.setRequestor(requestDto.getRequestor());
        }
        request.setDescription(requestDto.getDescription());
        if (requestDto.getCreated() != null) {
            request.setCreated(requestDto.getCreated());
        }
        return request;
    }

    public static RequestDto toRequestDto(ItemRequest request) {
        return new RequestDto(request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated(),
                request.getItems());
    }
}

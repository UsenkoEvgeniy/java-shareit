package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ItemRequest toRequest(RequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        if (requestDto.getId() != null) {
            request.setId(requestDto.getId());
        }
        if (requestDto.getRequestor() != null) {
            request.setRequestor(UserMapper.mapToUser(requestDto.getRequestor(), new User()));
        }
        request.setDescription(requestDto.getDescription());
        if (requestDto.getCreated() != null) {
            request.setCreated(requestDto.getCreated());
        }
        return request;
    }

    public static RequestDto toRequestDto(ItemRequest request) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (request.getItems() != null) {
            itemDtoList = request.getItems().stream().map(ItemMapper::mapToDto).collect(Collectors.toList());
        }
        return new RequestDto(request.getId(),
                request.getDescription(),
                UserMapper.mapToDto(request.getRequestor()),
                request.getCreated(),
                itemDtoList);
    }
}

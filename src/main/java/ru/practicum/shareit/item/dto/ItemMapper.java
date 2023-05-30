package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto mapToDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId());
    }

    public static ItemDtoWithCommentsAndBookings mapToDtoWithComments(Item item, List<Comment> comments, List<Booking> bookings) {
        return new ItemDtoWithCommentsAndBookings(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                comments != null ? comments.stream().map(CommentMapper::mapToDto).collect(Collectors.toList()) : Collections.emptyList(),
                getLastBooking(bookings),
                getNextBooking(bookings));
    }

    private static BookingDtoForOwner getNextBooking(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .map(BookingMapper::mapToDtoForOwner)
                .min(Comparator.comparing(BookingDtoForOwner::getStart))
                .orElse(null);
    }

    private static BookingDtoForOwner getLastBooking(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .map(BookingMapper::mapToDtoForOwner)
                .max(Comparator.comparing(BookingDtoForOwner::getEnd))
                .orElse(null);
    }

    public static Item mapToItem(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        return item;
    }
}

package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
    public static BookingDto mapToDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.mapToDto(booking.getItem()),
                UserMapper.mapToDto(booking.getBooker()),
                booking.getStatus());
    }

    public static Booking mapToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDtoForOwner mapToDtoForOwner(Booking booking) {
        return new BookingDtoForOwner(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId(),
                booking.getBooker().getId(), booking.getStatus());
    }
}

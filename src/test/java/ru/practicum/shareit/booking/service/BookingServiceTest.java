package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Validator;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private Validator validator;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void create_whenItemNotExist_throwException() {
        BookingDto expectedBookingDto = new BookingDto();
        long id = 0L;
        when(itemStorage.findById(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookingService.create(expectedBookingDto, id));
        verify(bookingStorage, never()).save(any());
    }

    @Test
    void create_whenItemNotAvailable_throwException() {
        BookingDto expectedBookingDto = new BookingDto();
        long id = 0L;
        Item item = new Item();
        item.setAvailable(false);
        when(itemStorage.findById(any())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.create(expectedBookingDto, id));
        verify(bookingStorage, never()).save(any());
    }

    @Test
    void create_whenUserNotExist_throwException() {
        BookingDto expectedBookingDto = new BookingDto();
        long id = 0L;
        Item item = new Item();
        item.setAvailable(true);
        when(itemStorage.findById(any())).thenReturn(Optional.of(item));
        when(userStorage.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookingService.create(expectedBookingDto, id));
        verify(bookingStorage, never()).save(any());
    }

    @Test
    void create_whenBookOwnedItem_throwException() {
        BookingDto expectedBookingDto = new BookingDto();
        long id = 0L;
        Item item = new Item();
        item.setAvailable(true);
        item.setOwnerId(id);
        expectedBookingDto.setItem(item);
        when(itemStorage.findById(any())).thenReturn(Optional.of(item));
        when(userStorage.existsById(id)).thenReturn(true);
        when(userStorage.findById(id)).thenReturn(Optional.of(new User()));

        assertThrows(AccessControlException.class, () -> bookingService.create(expectedBookingDto, id));
        verify(bookingStorage, never()).save(any());
    }

    @Test
    void create_whenValid_thenCallSave() {
        BookingDto expectedBookingDto = new BookingDto();
        long id = 0L;
        Item item = new Item();
        item.setAvailable(true);
        item.setOwnerId(id + 1);
        expectedBookingDto.setItem(item);
        when(itemStorage.findById(any())).thenReturn(Optional.of(item));
        when(userStorage.existsById(id)).thenReturn(true);
        when(userStorage.findById(id)).thenReturn(Optional.of(new User()));
        when(bookingStorage.save(any())).thenReturn(BookingMapper.mapToBooking(expectedBookingDto));

        BookingDto actualBookingDto = bookingService.create(expectedBookingDto, id);

        verify(bookingStorage).save(any());
    }

    @Test
    void setStatus_whenBookingNotExits_thenThrowException() {
        long id = 0L;
        boolean isApproved = false;
        when(bookingStorage.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookingService.setStatus(id, isApproved, id));
        verify(bookingStorage, never()).save(any());
    }

    @Test
    void setStatus_whenNotOwner_thenThrowException() {
        long id = 0L;
        boolean isApproved = false;
        Booking booking = new Booking();
        Item item = new Item();
        item.setOwnerId(id + 1);
        booking.setItem(item);
        when(bookingStorage.findById(id)).thenReturn(Optional.of(booking));

        assertThrows(AccessControlException.class, () -> bookingService.setStatus(id, isApproved, id));
        verify(bookingStorage, never()).save(any());
    }

    @Test
    void setStatus_whenNotWaiting_thenThrowException() {
        long id = 0L;
        boolean isApproved = false;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        Item item = new Item();
        item.setOwnerId(id);
        booking.setItem(item);
        when(bookingStorage.findById(id)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.setStatus(id, isApproved, id));
        verify(bookingStorage, never()).save(any());
    }

    @Test
    void setStatus_whenApproved_thenSetApproved() {
        long id = 0L;
        boolean isApproved = true;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        item.setOwnerId(id);
        booking.setItem(item);
        ArgumentCaptor<Booking> argument = ArgumentCaptor.forClass(Booking.class);
        when(bookingStorage.findById(id)).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(booking);

        bookingService.setStatus(id, isApproved, id);

        verify(bookingStorage).save(argument.capture());
        assertEquals(BookingStatus.APPROVED, argument.getValue().getStatus());
    }

    @Test
    void setStatus_whenNotApproved_thenSetRejected() {
        long id = 0L;
        boolean isApproved = false;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        item.setOwnerId(id);
        booking.setItem(item);
        ArgumentCaptor<Booking> argument = ArgumentCaptor.forClass(Booking.class);
        when(bookingStorage.findById(id)).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(booking);

        bookingService.setStatus(id, isApproved, id);

        verify(bookingStorage).save(argument.capture());
        assertEquals(BookingStatus.REJECTED, argument.getValue().getStatus());
    }

    @Test
    void findBooking_whenBookingNotExist_thenThrowException() {
        long id = 0L;
        when(bookingStorage.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookingService.findBooking(id, id));
    }

    @Test
    void findBooking_whenNotOwnerAndNotBooker_thenThrowException() {
        long id = 0L;
        Booking booking = new Booking();
        Item item = new Item();
        item.setOwnerId(id + 1);
        booking.setItem(item);
        User user = new User();
        user.setId(id + 1);
        booking.setBooker(user);
        when(bookingStorage.findById(id)).thenReturn(Optional.of(booking));

        assertThrows(AccessControlException.class, () -> bookingService.findBooking(id, id));
    }

    @Test
    void findBooking_whenOwnerAndNotBooker_thenReturnBooking() {
        long id = 0L;
        Booking booking = new Booking();
        Item item = new Item();
        item.setOwnerId(id);
        booking.setItem(item);
        User user = new User();
        user.setId(id + 1);
        booking.setBooker(user);
        when(bookingStorage.findById(id)).thenReturn(Optional.of(booking));

        BookingDto actualBookingDto = bookingService.findBooking(id, id);

        assertEquals(BookingMapper.mapToDto(booking), actualBookingDto);
    }

    @Test
    void findBookingsForUserOrOwner_whenUserNotExist_thenThrowException() {
        long id = 0L;
        int from = 0;
        int size = 1;
        boolean isOwner = true;
        BookingState state = BookingState.ALL;
        when(userStorage.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookingService.findBookingsForUserOrOwner(id, state, isOwner, from, size));
    }

    @Test
    void findBookingsForUserOrOwner_whenValid_thenReturnCollection() {
        long id = 0L;
        int from = 0;
        int size = 1;
        boolean isOwner = true;
        BookingState state = BookingState.ALL;
        when(userStorage.existsById(id)).thenReturn(true);
        when(bookingStorage.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(Page.empty());

        Collection<BookingDto> collection = bookingService.findBookingsForUserOrOwner(id, state, isOwner, from, size);

        assertEquals(Collections.emptyList(), collection);
    }
}
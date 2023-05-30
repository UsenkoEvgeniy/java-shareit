package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithCommentsAndBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private UserService userService;
    @Mock
    private CommentStorage commentStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_whenUserNotExist_thenThrowException() {
        long userId = 0L;
        ItemDto itemDto = new ItemDto();
        when(userService.isExist(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> itemService.create(userId, itemDto));
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void create_whenValid_thenReturnItem() {
        long userId = 0L;
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        item.setOwnerId(userId);
        Item expectedItem = ItemMapper.mapToItem(itemDto, item);
        when(userService.isExist(userId)).thenReturn(true);
        when(itemStorage.save(any(Item.class))).thenReturn(expectedItem);

        ItemDto actualItemDto = itemService.create(userId, itemDto);

        assertEquals(ItemMapper.mapToDto(expectedItem), actualItemDto);
    }

    @Test
    void update_whenItemNotExist_thenThrowException() {
        long userId = 0L;
        long itemId = 0L;
        ItemDto itemDto = new ItemDto();
        when(itemStorage.getReferenceById(itemId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, itemId, itemDto));
        verify(itemStorage, never()).save(any());
    }

    @Test
    void update_whenNotOwner_thenThrowException() {
        long userId = 0L;
        long itemId = 0L;
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        item.setOwnerId(userId + 1);
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);

        assertThrows(WrongUserException.class, () -> itemService.update(userId, itemId, itemDto));
        verify(itemStorage, never()).save(any());
    }

    @Test
    void update_whenValid_thenReturnItem() {
        long userId = 0L;
        long itemId = 0L;
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        item.setOwnerId(userId);
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);
        when(itemStorage.save(item)).thenReturn(item);

        ItemDto actualItem = itemService.update(userId, itemId, itemDto);

        assertEquals(ItemMapper.mapToDto(item), actualItem);
        verify(itemStorage).save(any());
    }

    @Test
    void get_whenItemNotExist_thenThrowException() {
        long userId = 0L;
        long itemId = 0L;
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.get(userId, itemId));
    }

    @Test
    void get_whenNotOwner_thenBookingListEmpty() {
        long userId = 1L;
        long itemId = 0L;
        Item item = new Item();
        item.setOwnerId(userId + 1);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(commentStorage.findByItem_Id(itemId)).thenReturn(Collections.emptyList());

        ItemDtoWithCommentsAndBookings itemDto = itemService.get(userId, itemId);

        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void get_whenOwner_thenItemWithBooking() {
        long userId = 1L;
        long itemId = 0L;
        Item item = new Item();
        item.setOwnerId(userId);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(commentStorage.findByItem_Id(itemId)).thenReturn(Collections.emptyList());
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStart(LocalDateTime.now().minusDays(1));
        when(bookingStorage.findByItemId(itemId)).thenReturn(List.of(booking));

        ItemDtoWithCommentsAndBookings itemDto = itemService.get(userId, itemId);

        assertNotNull(itemDto.getLastBooking());
    }

    @Test
    void getAllForOwner_whenValid_thenReturnCollection() {
        long userId = 0L;
        int from = 0;
        int size = 20;
        when(itemStorage.findByOwnerId(eq(userId), any(PageRequest.class))).thenReturn(Collections.emptyList());
        when(bookingStorage.findByItemOwnerId(any(), any())).thenReturn(Collections.emptyList());
        when(commentStorage.findByItem_OwnerIdAndItemIn(any(), any())).thenReturn(Collections.emptyList());

        Collection<ItemDtoWithCommentsAndBookings> actual = itemService.getAllForOwner(userId, from, size);

        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    void getAvailable_whenEmptyText_thenReturnEmptyCollection() {
        String text = "";
        int from = 0;
        int size = 20;

        Collection<ItemDto> actual = itemService.getAvailable(text, from, size);

        assertEquals(0, actual.size());
    }

    @Test
    void getAvailable_whenValid_thenReturnCollection() {
        String text = "text";
        int from = 0;
        int size = 20;
        Item item = new Item();
        when(itemStorage.findAvailable(text, PageRequest.of(from / size, size))).thenReturn(List.of(item));

        Collection<ItemDto> actual = itemService.getAvailable(text, from, size);

        assertEquals(ItemMapper.mapToDto(item), actual.iterator().next());
    }

    @Test
    void createComment_whenBookingNotExists_thenThrowException() {
        long userId = 0L;
        Comment comment = new Comment();
        long itemId = 0L;
        when(userService.findById(userId)).thenReturn(new User());
        when(bookingStorage.existsByBooker_IdAndEndBeforeAndStatus(eq(userId), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.createComment(userId, comment, itemId));
        verify(commentStorage, never()).save(comment);
    }

    @Test
    void createComment_whenItemNotExists_thenThrowException() {
        long userId = 0L;
        Comment comment = new Comment();
        long itemId = 0L;
        when(userService.findById(userId)).thenReturn(new User());
        when(bookingStorage.existsByBooker_IdAndEndBeforeAndStatus(eq(userId), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(true);
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.createComment(userId, comment, itemId));
        verify(commentStorage, never()).save(comment);
    }

    @Test
    void createComment_whenValid_thenReturnComment() {
        long userId = 0L;
        Comment comment = new Comment();
        long itemId = 0L;
        when(userService.findById(userId)).thenReturn(new User());
        when(bookingStorage.existsByBooker_IdAndEndBeforeAndStatus(eq(userId), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(true);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(commentStorage.save(comment)).thenReturn(comment);

        CommentDto actual = itemService.createComment(userId, comment, itemId);

        assertEquals(CommentMapper.mapToDto(comment), actual);
    }
}
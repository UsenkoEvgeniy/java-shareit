package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingControllerIntTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;

    @BeforeEach
    void addData() {
        User user = new User();
        user.setName("testName1");
        user.setEmail("1@email.com");
        userController.create(user);
        User user2 = new User();
        user2.setName("testName2");
        user2.setEmail("2@email.com");
        userController.create(user2);

        ItemDto item1 = new ItemDto();
        item1.setName("first");
        item1.setDescription("firstDesc");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setName("second");
        item2.setDescription("secondDesc");
        item2.setAvailable(true);

        itemController.create(1L, item1);
        itemController.create(2L, item2);

        BookingDto booking1 = new BookingDto();
        booking1.setStart(LocalDateTime.now().plusMinutes(5));
        booking1.setEnd(LocalDateTime.now().plusMinutes(8));
        booking1.setItemId(1L);
        booking1.setStatus(BookingStatus.APPROVED);

        BookingDto booking2 = new BookingDto();
        booking2.setStart(LocalDateTime.now().plusMinutes(10));
        booking2.setEnd(LocalDateTime.now().plusMinutes(13));
        booking2.setItemId(2L);
        booking2.setStatus(BookingStatus.APPROVED);

        bookingController.create(booking1, 2L);
        bookingController.create(booking2, 1L);
    }

    @SneakyThrows
    @Test
    void findBookingsForOwner_whenOwnerFuture() {
        long userId = 1L;
        BookingState state = BookingState.FUTURE;
        int from = 0;
        int size = 20;

        Collection<BookingDto> collection = bookingController.findBookingsForOwner(userId, state, from, size);

        assertEquals(1, collection.size());
    }

    @SneakyThrows
    @Test
    void findBookingsForOwner_whenUserRejected() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;
        int from = 0;
        int size = 20;

        Collection<BookingDto> collection = bookingController.findBookingsForUser(userId, state, from, size);

        assertEquals(0, collection.size());
    }
}
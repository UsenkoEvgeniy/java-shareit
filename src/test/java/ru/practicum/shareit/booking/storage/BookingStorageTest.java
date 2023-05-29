package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private BookingStorage bookingStorage;

    @BeforeEach
    void addData() {
        User user = new User();
        user.setName("testName1");
        user.setEmail("1@email.com");
        userStorage.save(user);
        User user2 = new User();
        user2.setName("testName2");
        user2.setEmail("2@email.com");
        userStorage.save(user2);

        Item item1 = new Item();
        item1.setOwnerId(1L);
        item1.setName("first");
        item1.setDescription("firstDesc");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setOwnerId(2L);
        item2.setName("second");
        item2.setDescription("secondDesc");
        item2.setAvailable(true);

        itemStorage.save(item1);
        itemStorage.save(item2);

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusMinutes(5));
        booking1.setEnd(LocalDateTime.now().plusMinutes(8));
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking1.setStatus(BookingStatus.APPROVED);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusMinutes(10));
        booking2.setEnd(LocalDateTime.now().plusMinutes(13));
        booking2.setItem(item2);
        booking2.setBooker(user);
        booking2.setStatus(BookingStatus.APPROVED);

        bookingStorage.save(booking1);
        bookingStorage.save(booking2);
    }

    @Test
    void findByItemOwnerId() {
        long ownerId = 1L;
        Item item1 = new Item();
        item1.setId(1L);
        item1.setOwnerId(1L);
        item1.setName("first");
        item1.setDescription("firstDesc");
        item1.setAvailable(true);

        List<Booking> list = bookingStorage.findByItemOwnerId(ownerId, List.of(item1));

        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getId());
    }

    @Test
    void findByItemId() {
        long itemId = 2L;
        List<Booking> list = bookingStorage.findByItemId(itemId);

        assertEquals(1, list.size());
        assertEquals("second", list.get(0).getItem().getName());
    }

    @Test
    void existsByBooker_IdAndEndBeforeAndStatus() {
        long bookerId = 2L;
        LocalDateTime end = LocalDateTime.now().plusMinutes(11);
        BookingStatus status = BookingStatus.APPROVED;
        boolean exist = bookingStorage.existsByBooker_IdAndEndBeforeAndStatus(bookerId, end, status);

        assertTrue(exist);
    }
}
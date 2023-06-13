package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestStorageTest {

    @Autowired
    private RequestStorage requestStorage;
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void addRequest() {
        User user = new User();
        user.setName("testName1");
        user.setEmail("1@email.com");
        userStorage.save(user);
        User user2 = new User();
        user2.setName("testName2");
        user2.setEmail("2@email.com");
        userStorage.save(user2);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("firstRequest");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        requestStorage.save(itemRequest);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("secondRequest");
        itemRequest2.setRequestor(user2);
        itemRequest2.setCreated(LocalDateTime.now());
        requestStorage.save(itemRequest2);
    }

    @Test
    void findByRequestor_IdNotOrderByCreatedDesc() {
        long userId = 2L;
        PageRequest pageable = PageRequest.of(0, 20);
        List<ItemRequest> list = requestStorage.findByRequestor_IdNotOrderByCreatedDesc(userId, pageable);

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getRequestor().getId());
    }

    @Test
    void findByRequestor_IdOrderByCreatedDesc() {
        long userId = 2L;
        List<ItemRequest> list = requestStorage.findByRequestor_IdOrderByCreatedDesc(userId);

        assertEquals(1, list.size());
        assertEquals(2L, list.get(0).getRequestor().getId());
    }
}
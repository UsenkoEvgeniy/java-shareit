package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void addItems() {
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
        item1.setAvailable(false);

        Item item2 = new Item();
        item2.setOwnerId(2L);
        item2.setName("second");
        item2.setDescription("secondDesc");
        item2.setAvailable(true);

        itemStorage.save(item1);
        itemStorage.save(item2);
    }

    @Test
    void findByOwnerId() {
        long ownerId = 1L;
        PageRequest pr = PageRequest.of(0, 20, Sort.by("id"));
        Collection<Item> collection = itemStorage.findByOwnerId(ownerId, pr);

        assertEquals(1, collection.size());
        assertEquals(ownerId, collection.iterator().next().getOwnerId());
    }

    @Test
    void findAvailable() {
        String item2Name = "second";
        String searchText = "desc";
        PageRequest pr = PageRequest.of(0, 20);
        Collection<Item> collection = itemStorage.findAvailable(searchText, pr);

        assertEquals(1, collection.size());
        assertEquals(item2Name, collection.iterator().next().getName());
    }
}
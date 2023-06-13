package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentStorageTest {
    @Autowired
    private CommentStorage commentStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

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
        item1.setAvailable(false);

        Item item2 = new Item();
        item2.setOwnerId(2L);
        item2.setName("second");
        item2.setDescription("secondDesc");
        item2.setAvailable(true);

        itemStorage.save(item1);
        itemStorage.save(item2);

        Comment comment = new Comment();
        comment.setText("firstComment");
        comment.setItem(item1);
        comment.setAuthor(user2);
        comment.setCreated(LocalDateTime.now());
        Comment comment2 = new Comment();
        comment2.setText("secondComment");
        comment2.setItem(item2);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now());

        commentStorage.save(comment);
        commentStorage.save(comment2);
    }

    @Test
    void findByItem_OwnerIdAndItemIn() {
        long ownerId = 1L;
        Item item1 = new Item();
        item1.setId(1L);
        item1.setOwnerId(ownerId);
        item1.setName("first");
        item1.setDescription("firstDesc");
        item1.setAvailable(false);

        List<Comment> list = commentStorage.findByItem_OwnerIdAndItemIn(ownerId, List.of(item1));

        assertEquals(1, list.size());
        assertEquals(ownerId, list.get(0).getItem().getOwnerId());
    }

    @Test
    void findByItem_Id() {
        long itemId = 2L;

        List<Comment> list = commentStorage.findByItem_Id(itemId);

        assertEquals(1, list.size());
        assertEquals(itemId, list.get(0).getItem().getId());
    }
}
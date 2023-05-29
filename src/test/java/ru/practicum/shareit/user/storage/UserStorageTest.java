package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserStorageTest {

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    private void addUsers() {
        User user = new User();
        user.setName("first");
        user.setEmail("first@email.com");
        userStorage.save(user);
    }

    @AfterEach
    private void deleteUsers() {
        userStorage.deleteAll();
    }

    @Test
    void findById() {
        long userId = 1L;
        Optional<User> actual = userStorage.findById(userId);

        assertTrue(actual.isPresent());
    }

}
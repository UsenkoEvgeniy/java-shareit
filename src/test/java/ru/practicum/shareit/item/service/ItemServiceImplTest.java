package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemServiceImplTest {

    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    ItemServiceImplTest(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    @Test
    void create() {
        UserDto user = userService.create(new UserDto("Name1", "e@mail.com"));
        ItemDto itemDto = new ItemDto("Screwdriver", "Old screwdriver", true);
        ItemDto itemCreated = itemService.create(user.getId(), itemDto);
        assertEquals(itemDto.getName(), itemCreated.getName(), "Created name is different");
        assertEquals(itemDto.getDescription(), itemCreated.getDescription(),
                "Created description is different");
        assertThrows(UserNotFoundException.class, () -> itemService.create(-1, itemDto),
                "Created item with wrong user");
    }

    @Test
    void update() {
        UserDto user = userService.create(new UserDto("Name2", "e2@mail.com"));
        ItemDto createdItem = itemService.create(user.getId(), new ItemDto("Axe", "Sharp axe",
                true));
        ItemDto itemToUpdate = new ItemDto();
        itemToUpdate.setName("Updated axe");
        ItemDto updatedItem = itemService.update(user.getId(), createdItem.getId(), itemToUpdate);
        assertEquals("Updated axe", updatedItem.getName(), "Updated name is different");
        assertThrows(WrongUserException.class, () -> itemService.update(-1, createdItem.getId(), itemToUpdate),
                "Updated by not owner of the item");
    }

    @Test
    void get() {
        UserDto user = userService.create(new UserDto("Name3", "e3@mail.com"));
        ItemDto itemDto = new ItemDto("Screwdriver", "Old screwdriver", true);
        ItemDto itemCreated = itemService.create(user.getId(), itemDto);
        assertEquals(itemCreated, itemService.get(itemCreated.getId()),
                "Item created and item in db are different");
        assertThrows(ItemNotFoundException.class, () -> itemService.get(-1), "Found item for wrong id");
    }

    @Test
    void getAllForUser() {
        UserDto user = userService.create(new UserDto("Name4", "e4@mail.com"));
        ItemDto itemDto = new ItemDto("Screwdriver", "Old screwdriver", true);
        itemService.create(user.getId(), itemDto);
        ItemDto itemDto2 = new ItemDto("Screwdriver2", "Old screwdriver2", true);
        itemService.create(user.getId(), itemDto2);
        assertEquals(2, itemService.getAllForUser(user.getId()).size());
    }

    @Test
    void getAvailable() {
        UserDto user = userService.create(new UserDto("Name5", "e5@mail.com"));
        ItemDto itemDto = new ItemDto("Desk", "Old desk", true);
        itemService.create(user.getId(), itemDto);
        ItemDto itemDto2 = new ItemDto("Dusty desk", "Dusty desk", true);
        itemService.create(user.getId(), itemDto2);
        ItemDto itemDto3 = new ItemDto("Shiny desk", "Shiny desk", false);
        itemService.create(user.getId(), itemDto3);
        itemService.getAvailable("dEsK").forEach(System.out::println);
        assertEquals(2, itemService.getAvailable("dEsK").size(), "Got unavailable item");
        assertEquals(Collections.emptyList(), itemService.getAvailable(""),
                "Non empty list for empty request");
    }
}
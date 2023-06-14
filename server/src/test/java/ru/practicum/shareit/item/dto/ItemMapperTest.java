package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void mapToItem() {
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        itemDto.setAvailable(true);
        itemDto.setName("name");
        itemDto.setRequestId(4L);
        itemDto.setDescription("Desc");
        Item actualItem = ItemMapper.mapToItem(itemDto, item);

        assertEquals(item.getName(), actualItem.getName());
    }
}
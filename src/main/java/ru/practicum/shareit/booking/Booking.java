package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private LocalDate startDate;
    private LocalDate endDate;
    private Item item;
    private User user;
    private boolean isConfirmed = false;
}

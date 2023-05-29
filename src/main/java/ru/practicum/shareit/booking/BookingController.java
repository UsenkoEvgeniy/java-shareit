package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Min;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody(required = false) BookingDto booking,
                             @RequestHeader(USER_ID) long userId) {
        log.info("Post request for booking: {}", booking);
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatus(@PathVariable long bookingId,
                                @RequestParam boolean approved,
                                @RequestHeader(USER_ID) long userId) {
        log.info("Set status {} for bookingId: {} from user: {}", approved, bookingId, userId);
        return bookingService.setStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto fingBooking(@RequestHeader(USER_ID) long userId,
                                  @PathVariable long bookingId) {
        log.info("Get request for bookingId {} from userId {}", bookingId, userId);
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findBookingsForUser(@RequestHeader(USER_ID) long userId,
                                                      @RequestParam(defaultValue = "ALL") BookingState state,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.info("Get request for bookings of user {} with state {}", userId, state);
        return bookingService.findBookingsForUserOrOwner(userId, state, false, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findBookingsForOwner(@RequestHeader(USER_ID) long userId,
                                                       @RequestParam(defaultValue = "ALL") BookingState state,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.info("Get request for bookings of user {} as owner with state {}", userId, state);
        return bookingService.findBookingsForUserOrOwner(userId, state, true, from, size);
    }
}

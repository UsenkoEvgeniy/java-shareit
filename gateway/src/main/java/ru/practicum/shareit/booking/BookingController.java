package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String USER_ID = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) long userId,
											  @RequestParam(defaultValue = "ALL") BookingState state,
											  @RequestParam(defaultValue = "0") @Min(0) Integer from,
											  @RequestParam(defaultValue = "20") @Min(1) Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID) long userId,
			@RequestBody @Valid BookingDto bookingDto) {
		log.info("Creating booking {}, userId={}", bookingDto, userId);
		return bookingClient.bookItem(userId, bookingDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> setStatus(@PathVariable long bookingId,
								@RequestParam boolean approved,
								@RequestHeader(USER_ID) long userId) {
		log.info("Set status {} for bookingId: {} from user: {}", approved, bookingId, userId);
		return bookingClient.setStatus(bookingId, approved, userId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findBookingsForOwner(@RequestHeader(USER_ID) long userId,
													   @RequestParam(defaultValue = "ALL") BookingState state,
													   @RequestParam(defaultValue = "0") @Min(0) Integer from,
													   @RequestParam(defaultValue = "20") @Min(1) Integer size) {
		log.info("Get request for bookings of user {} as owner with state {}", userId, state);
		return bookingClient.findBookingsForUserOrOwner(userId, state, from, size);
	}
}

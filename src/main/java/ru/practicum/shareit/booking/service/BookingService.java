package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.criteria.Predicate;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.security.AccessControlException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final Validator validator;

    public BookingService(BookingStorage bookingStorage, UserStorage userStorage, ItemStorage itemStorage, Validator validator) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.validator = validator;
    }

    public BookingDto create(BookingDto bookingDto, long userId) {
        if (!itemStorage.findById(bookingDto.getItemId()).orElseThrow().isAvailable()) {
            throw new BadRequestException("Item is not available");
        }
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException(String.valueOf(userId));
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.mapToBooking(bookingDto);
        booking.setItem(itemStorage.findById(bookingDto.getItemId()).orElseThrow());
        booking.setBooker(userStorage.findById(userId).orElseThrow());
        if (booking.getItem().getOwnerId() == userId) {
            throw new AccessControlException("Can't book your own item");
        }
        Set<ConstraintViolation<Booking>> constraintViolations = validator.validate(booking);
        if (!constraintViolations.isEmpty()) {
            throw new ValidationException("Bad booking fields: " + constraintViolations + "\n" + booking);
        }
        log.debug("Creating booking {}", booking);
        return BookingMapper.mapToDto(bookingStorage.save(booking));
    }

    public BookingDto setStatus(long bookingId, boolean isApproved, long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow();
        if (booking.getItem().getOwnerId() != userId) {
            throw new AccessControlException("User " + userId + " is not owner of the item");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (isApproved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new BadRequestException("Can't change status after decision");
        }
        log.debug("Set {} at booking {} by user {}", isApproved, bookingId, userId);
        return BookingMapper.mapToDto(bookingStorage.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingDto findBooking(long userId, long bookingId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow();
        if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwnerId()) {
            log.debug("Returning booking {} to user {}", bookingId, userId);
            return BookingMapper.mapToDto(booking);
        } else {
            throw new AccessControlException("User " + userId + " is neither owner nor booker");
        }
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> findBookingsForUserOrOwner(long userId, BookingState state, boolean isOwner) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException(String.valueOf(userId));
        }
        Specification<Booking> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate user;
            if (isOwner) {
                user = criteriaBuilder.equal(root.join("item").get("ownerId"), userId);
            } else {
                user = criteriaBuilder.equal(root.join("booker").get("id"), userId);
            }
            predicates.add(user);
            Predicate predicate = null;
            switch (state) {
                case ALL:
                    break;
                case CURRENT:
                    predicate = criteriaBuilder.and(criteriaBuilder.<LocalDateTime>lessThan(root.get("start"), LocalDateTime.now()),
                            criteriaBuilder.greaterThan(root.<LocalDateTime>get("end"), LocalDateTime.now()));
                    break;
                case PAST:
                    predicate = criteriaBuilder.lessThan(root.<LocalDateTime>get("end"), LocalDateTime.now());
                    break;
                case FUTURE:
                    predicate = criteriaBuilder.greaterThan(root.<LocalDateTime>get("start"), LocalDateTime.now());
                    break;
                case WAITING:
                    predicate = criteriaBuilder.equal(root.get("status"), BookingStatus.WAITING);
                    break;
                case REJECTED:
                    predicate = criteriaBuilder.equal(root.get("status"), BookingStatus.REJECTED);
                    break;
                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
            if (predicate != null) {
                predicates.add(predicate);
            }
            log.debug("Search has {} criteria", predicates.size());
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        });
        return bookingStorage.findAll(specification, Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .map(BookingMapper::mapToDto)
                .collect(Collectors.toList());
    }
}

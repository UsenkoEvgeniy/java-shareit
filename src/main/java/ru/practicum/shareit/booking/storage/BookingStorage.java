package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.status = 'APPROVED'")
    List<Booking> findByItemOwnerId(Long ownerId);

    @Query("select b from Booking b where b.item.id = ?1 and b.status = 'APPROVED'")
    List<Booking> findByItemId(Long itemId);

    boolean existsByBooker_IdAndEndBeforeAndStatus(long id, LocalDateTime end, BookingStatus status);
}

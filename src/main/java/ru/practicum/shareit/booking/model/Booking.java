package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Future
    @Column(name = "start_date")
    private LocalDateTime start;
    @Future
    @Column(name = "end_date")
    private LocalDateTime end;
    @NotNull(message = "Booking can't exist without item")
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @NotNull(message = "Booking can't exist without booker")
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndAfterStart() {
        return end.isAfter(start);
    }
}


package com.hotel.manager;

import com.hotel.model.EventBooking;
import com.hotel.model.EventHall;
import com.hotel.enums.EventBookingStatus;
import com.hotel.repository.HibernateRepository;

import java.time.LocalDateTime;
import java.util.List;

public class EventBookingManager {

    private final HibernateRepository repository =
            new HibernateRepository();

    // Add a new event booking
    public boolean addBooking(EventBooking booking) {

        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking cannot be null");
        }

        if (booking.getBookingId() > 0) {
            return false;
        }

        if (!isHallAvailable(
                booking.getHall(),
                booking.getStartDateTime(),
                booking.getEndDateTime())) {
            return false;
        }

        repository.save(booking);
        return true;
    }

    // Find a booking by ID
    public EventBooking findBookingById(int bookingId) {

        if (bookingId <= 0) {
            return null;
        }

        return repository.findById(
                EventBooking.class,
                bookingId
        );
    }

    // Get all event bookings
    public List<EventBooking> findAllBookings() {

        return repository.findAll(EventBooking.class);
    }

    // Update an existing booking
    public EventBooking updateBooking(EventBooking booking) {

        if (booking == null || booking.getBookingId() <= 0) {
            throw new IllegalArgumentException(
                    "A saved booking is required");
        }

        return repository.update(booking);
    }

    // Cancel a booking
    public boolean cancelBooking(
            int bookingId,
            String reason) {

        EventBooking booking = findBookingById(bookingId);

        if (booking == null) {
            return false;
        }

        if (booking.getStatus() != EventBookingStatus.PENDING
                && booking.getStatus()
                != EventBookingStatus.CONFIRMED) {
            return false;
        }

        booking.cancelBooking(reason);
        repository.update(booking);

        return true;
    }

    // Record deposit payment
    public EventBooking payDeposit(int bookingId) {

        EventBooking booking = findBookingById(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking not found");
        }

        booking.payDeposit();

        return repository.update(booking);
    }

    // Confirm booking after deposit payment
    public boolean confirmBooking(int bookingId) {

        EventBooking booking = findBookingById(bookingId);

        if (booking == null) {
            return false;
        }

        boolean confirmed = booking.confirmBooking();

        if (!confirmed) {
            return false;
        }

        repository.update(booking);
        return true;
    }

    // Check hall availability for a given period
    public boolean isHallAvailable(
            EventHall hall,
            LocalDateTime start,
            LocalDateTime end) {

        if (hall == null) {
            throw new IllegalArgumentException(
                    "Hall is required");
        }

        if (start == null || end == null
                || !end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Invalid booking period");
        }

        // Confirm the hall exists in the database
        EventHall savedHall = repository.findById(
                EventHall.class,
                hall.getHallId()
        );

        if (savedHall == null) {
            return false;
        }

        List<EventBooking> bookings =
                repository.findAll(EventBooking.class);

        for (EventBooking booking : bookings) {

            if (booking.getStatus()
                    == EventBookingStatus.CANCELLED) {
                continue;
            }

            if (booking.getHall().getHallId()
                    != hall.getHallId()) {
                continue;
            }

            boolean overlaps =
                    booking.getStartDateTime().isBefore(end)
                            && booking.getEndDateTime().isAfter(start);

            if (overlaps) {
                return false;
            }
        }

        return true;
    }
    // Get all event halls
    public List<EventHall> findAllHalls() {
        return repository.findAll(EventHall.class);
    }

    // Find event hall by ID
    public EventHall findHallById(int hallId) {

        if (hallId <= 0) {
            return null;
        }

        return repository.findById(EventHall.class, hallId);
    }
}

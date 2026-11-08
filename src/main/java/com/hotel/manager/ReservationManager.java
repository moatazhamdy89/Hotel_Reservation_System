package com.hotel.manager;

import com.hotel.enums.ReservationStatus;
import com.hotel.enums.RoomStatus;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.repository.HibernateRepository;

import java.time.LocalDate;
import java.util.List;

public class ReservationManager {

    private final HibernateRepository repository =
            new HibernateRepository();

    // Add a new reservation
    public boolean addReservation(Reservation reservation) {

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Reservation cannot be null");
        }

        if (reservation.isSaved()) {
            return false;
        }

        if (!isRoomAvailable(
                reservation.getRoom(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate())) {
            return false;
        }

        repository.save(reservation);
        return true;
    }

    // Find a reservation by ID
    public Reservation findReservationById(int reservationId) {

        if (reservationId <= 0) {
            return null;
        }

        return repository.findById(
                Reservation.class,
                reservationId
        );
    }

    // Find all reservations
    public List<Reservation> findAllReservations() {
        return repository.findAll(Reservation.class);
    }

    // Update an existing reservation
    public Reservation updateReservation(
            Reservation reservation) {

        if (reservation == null || !reservation.isSaved()) {
            throw new IllegalArgumentException(
                    "A saved reservation is required");
        }

        return repository.update(reservation);
    }

    // Cancel an existing reservation
    public boolean cancelReservation(int reservationId) {

        Reservation reservation =
                findReservationById(reservationId);

        if (reservation == null
                || reservation.getStatus()
                == ReservationStatus.CANCELLED) {
            return false;
        }

        reservation.cancel();
        repository.update(reservation);

        return true;
    }

    // Record deposit payment and confirm reservation
    public Reservation payDeposit(int reservationId) {

        Reservation reservation =
                findReservationById(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Reservation not found");
        }

        reservation.payDeposit();

        return repository.update(reservation);
    }

    // Check room availability for a requested period
    public boolean isRoomAvailable(
            Room room,
            LocalDate checkInDate,
            LocalDate checkOutDate) {

        if (room == null) {
            throw new IllegalArgumentException(
                    "Room is required");
        }

        if (checkInDate == null || checkOutDate == null
                || checkInDate.isBefore(LocalDate.now())
                || !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException(
                    "Invalid reservation dates");
        }

        Room savedRoom = repository.findById(
                Room.class,
                room.getRoomId()
        );

        if (savedRoom == null) {
            return false;
        }

        // Room status rules
        RoomStatus status = savedRoom.getStatus();

        // Maintenance rooms cannot be booked
        if (status == RoomStatus.UNDER_MAINTAINANCE) {
            return false;
        }

        // Same-day check-in requires a ready room
        if (checkInDate.equals(LocalDate.now())
                && status != RoomStatus.AVAILABLE) {
            return false;
        }

        // Check for overlapping reservations
        List<Reservation> reservations =
                repository.findAll(Reservation.class);

        for (Reservation existing : reservations) {

            // Cancelled reservations do not block availability
            if (existing.getStatus()
                    == ReservationStatus.CANCELLED) {
                continue;
            }

            // Ignore reservations for other rooms
            if (existing.getRoom().getRoomId()
                    != savedRoom.getRoomId()) {
                continue;
            }

            // Date ranges overlap
            boolean overlaps =
                    existing.getCheckInDate().isBefore(checkOutDate)
                            && existing.getCheckOutDate().isAfter(checkInDate);

            if (overlaps) {
                return false;
            }
        }

        return true;
    }
}
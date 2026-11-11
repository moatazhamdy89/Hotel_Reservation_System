package com.hotel.model;

import com.hotel.enums.ReservationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservations")
public class Reservation {

    private static final BigDecimal depositRate =
            new BigDecimal("0.20");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private int reservationId;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accommodation_type", nullable = false)
    private Accommodation accommodation;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "deposit_paid")
    private boolean depositPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;


    // Required by Hibernate
    protected Reservation() {
    }


    // NEW reservation
    public Reservation(LocalDate checkInDate, LocalDate checkOutDate,
                       Accommodation accommodation, Guest guest, Room room) {

        if (accommodation == null || guest == null || room == null) {
            throw new IllegalArgumentException(
                    "Accommodation, Guest and Room are required");
        }

        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.accommodation = accommodation;
        this.guest = guest;
        this.room = room;
        this.bookingDate = LocalDate.now();
        this.status = ReservationStatus.PENDING;
        this.depositPaid = false;
        this.reservationId = 0;

        if (!validateDates()) {
            throw new IllegalArgumentException(
                    "Invalid dates: check-in must be today or later, and check-out after check-in");
        }

        if (checkInDate.isBefore(bookingDate)) {
            throw new IllegalArgumentException(
                    "Check-in date cannot be in the past");
        }

        this.depositAmount = calculateDeposit();
    }

    public boolean validateDates() {
        return getCheckInDate() != null
                && getCheckOutDate() != null
                && getCheckOutDate().isAfter(getCheckInDate());
    }


    public int calculateNights() {
        return (int) ChronoUnit.DAYS.between(
                getCheckInDate(), getCheckOutDate());
    }


    public BigDecimal calculateAccommodationCost() {

        if (calculateNights() <= 0) {
            throw new IllegalArgumentException(
                    "Number of nights must be greater than 0");
        }

        BigDecimal pricePerNight =
                accommodation.getPricePerNight()
                        .add(room.getBasePrice());

        return pricePerNight
                .multiply(BigDecimal.valueOf(calculateNights()))
                .setScale(2, RoundingMode.HALF_UP);
    }


    public BigDecimal calculateDeposit() {
        return calculateAccommodationCost()
                .multiply(depositRate)
                .setScale(2, RoundingMode.HALF_UP);
    }


    public void confirmReservation() {
        if (getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a PENDING reservation can be confirmed");
        }

        if (!isDepositPaid()) {
            throw new IllegalStateException(
                    "Cannot Confirm: deposit is not paid");
        }

        this.status = ReservationStatus.CONFIRMED;
    }


    public void payDeposit() {
        if (getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                    "Deposit can only be paid for a PENDING reservation");
        }

        this.depositPaid = true;
        confirmReservation();
    }


    public void cancel() {
        if (status != ReservationStatus.PENDING
                && status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only pending or confirmed reservations can be cancelled");
        }

        this.status = ReservationStatus.CANCELLED;
        this.cancellationDate = LocalDate.now();
    }


    public boolean isSaved() {
        return reservationId > 0;
    }


    // Marks a confirmed reservation as checked in
    public void checkIn() {
        if (status != ReservationStatus.CONFIRMED)
            throw new IllegalStateException(
                    "Only confirmed reservations can check in");

        if (!depositPaid)
            throw new IllegalStateException(
                    "Deposit must be paid before check-in");

        if (!LocalDate.now().equals(checkInDate))
            throw new IllegalStateException(
                    "Check-in is only allowed on the scheduled date");

        status = ReservationStatus.CHECKED_IN;
    }


    // Allows check-out during the reservation period
    public void checkOut() {
        if (status != ReservationStatus.CHECKED_IN)
            throw new IllegalStateException(
                    "Only checked-in reservations can check out");

        LocalDate today = LocalDate.now();

        if (today.isBefore(checkInDate)
                || today.isAfter(checkOutDate))
            throw new IllegalStateException(
                    "Check-out must be within the reservation period");

        status = ReservationStatus.CHECKED_OUT;
    }


    public boolean isDepositPaid() {
        return depositPaid;
    }

    public int getReservationId() {
        return reservationId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }
}
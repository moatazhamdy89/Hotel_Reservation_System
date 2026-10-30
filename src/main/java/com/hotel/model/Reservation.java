package com.hotel.model;
import com.hotel.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit; // Da import hnstkhdmo 3shan n7sb number of days between 2 dates

public class Reservation {

    private static final double depositRate = 0.20 ;
    // 20 % w static 3shan percentage of the deposit is constant for any object from this class
    private int reservationId;

    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final LocalDate bookingDate;
    private LocalDate cancellationDate;   // null until the reservation is cancelled
    private final Accommodation accommodation;
    private double depositAmount;
    private boolean depositPaid;
    private ReservationStatus status;
    private final Guest guest;
    private final Room room;


    // 3. Constructor for a NEW reservation (status = PENDING, depositPaid = false)
    public Reservation(LocalDate checkInDate, LocalDate checkOutDate,
                       Accommodation accommodation, Guest guest, Room room) {
        if (accommodation == null || guest == null || room == null) {
            throw new IllegalArgumentException("Accommodation, Guest and Room are required");
        }
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.accommodation = accommodation;
        this.guest = guest;
        this.room = room;
        this.bookingDate = LocalDate.now(); // 3mlto LocalDate.now() 3shan ysgl automatic el date el hgz feh
        this.status = ReservationStatus.PENDING;
        this.depositPaid = false;
        this.reservationId = 0;   // lw value of reservationId 0 = not saved yet,
        // laken lw  positive = saved ==> m3nah en el 7agz 3ndo id kda

        if (!validateDates()) {
            throw new IllegalArgumentException("Invalid dates: check-in must be today or later, and check-out after check-in");
        }
        if (checkInDate.isBefore(bookingDate)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        this.depositAmount = calculateDeposit();
    }





    // 4. Constructor for RELOADING a saved reservation (no recalculation, no past-date check)
    public Reservation(int reservationId, LocalDate checkInDate, LocalDate checkOutDate,
                       LocalDate bookingDate, LocalDate cancellationDate,
                       Accommodation accommodation, double depositAmount, boolean depositPaid,
                       ReservationStatus status, Guest guest, Room room) {
        if (reservationId <= 0) {
            throw new IllegalArgumentException("Reservation ID must be positive");
        }
        // Critical rule: CONFIRMED requires depositPaid = true
        if (status == ReservationStatus.CONFIRMED && !depositPaid) {
            throw new IllegalArgumentException("A CONFIRMED reservation must have its deposit paid");
        }
        this.reservationId = reservationId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingDate = bookingDate;
        this.cancellationDate = cancellationDate;
        this.accommodation = accommodation;
        this.depositAmount = depositAmount;
        this.depositPaid = depositPaid;
        this.status = status;
        this.guest = guest;
        this.room = room;
    }

    public boolean validateDates() {
        return getCheckInDate() != null && getCheckOutDate() != null && getCheckOutDate().isAfter(getCheckInDate());
        // isAfter built in method
    }

    public int calculateNights() {
        // ChronoUnit.DAYS.between(a, b) = number of days from a to b (check-out day is not counted)
        return (int) ChronoUnit.DAYS.between(getCheckInDate(), getCheckOutDate()); // 3mlt (int) 3shan between de htrg3o long
    }

    //  cost = (accommodation price per night + room price per night) * nights
    public double calculateAccommodationCost() {
        if (calculateNights() <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than 0");
        }
        // Per-night price of the package (BREAKFAST / HALF_BOARD / FULL_BOARD) + per-night price of the room
        double pricePerNight = accommodation.getPricePerNight() + room.getBasePrice();
        return Math.round(pricePerNight * calculateNights() * 100.0) / 100.0; // *100 then /100.0 = round to 2 decimals
    }

    public double calculateDeposit() {
        // Deposit = depositRate of (room + accommodation); no loyalty discount applied (assumption)
        return Math.round(calculateAccommodationCost() * depositRate * 100.0) / 100.0;
    }



    public void confirmReservation() {
        if (getStatus() != ReservationStatus.PENDING) {          // Check 1
            throw new IllegalStateException("Only a PENDING reservation can be confirmed ");
        }
        if (!isDepositPaid()) {                                  // Check 2
            throw new IllegalStateException("Cannot Confirm: deposit is not paid");
        }
        this.status = ReservationStatus.CONFIRMED;           // Only reached if both checks passed
    }
//    Check 1: only a PENDING reservation can be confirmed. A CANCELLED or already CONFIRMED one cannot.
//    Check 2: if the deposit is not paid, stop. This protects our most important rule: a reservation must never be CONFIRMED while depositPaid is false.
//    Last line: it runs only when both checks passed, and it changes the status.


    public void payDeposit() {
        if (getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Deposit can only be paid for a PENDING reservation");
        }
        this.depositPaid = true;
        confirmReservation();
    }

    public void cancel() {
        if (getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancellationDate = LocalDate.now();
    }


    //database lessa
    public void assignId(int generatedId) { // DAO calls this after INSERT
        if (reservationId != 0) {
            throw new IllegalStateException("Reservation already has ID " + reservationId);
        }
        if (generatedId <= 0) {
            throw new IllegalArgumentException("Generated ID must be positive");
        }
        this.reservationId = generatedId;
    }

    public boolean isSaved() {
        return reservationId > 0;
    }

    public boolean isDepositPaid() {return depositPaid;}
    public int getReservationId() { return reservationId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalDate getCancellationDate() { return cancellationDate; }
    public Accommodation getAccommodation() { return accommodation; }
    public double getDepositAmount() { return depositAmount; }
    public ReservationStatus getStatus() { return status; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
}
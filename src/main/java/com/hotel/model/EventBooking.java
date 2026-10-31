package com.hotel.model;

import com.hotel.enums.EventBookingStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_bookings")
public class EventBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private int bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private EventHall hall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Column(name = "external_client_name")
    private String externalClientName;

    @Column(name = "external_client_phone")
    private String externalClientPhone;

    @Column(name = "external_client_national_id")
    private String externalClientNationalId;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private double depositAmount;

    @Column(name = "deposit_paid")
    private boolean depositPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventBookingStatus status;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    // Required by Hibernate
    protected EventBooking() {
    }

    // Constructor for a registered guest
    public EventBooking(EventHall hall, Guest guest,
                        LocalDateTime startDateTime,
                        LocalDateTime endDateTime,
                        double depositAmount) {

        validateBooking(hall, startDateTime,
                endDateTime, depositAmount);

        if (guest == null) {
            throw new IllegalArgumentException(
                    "Guest is required");
        }

        this.hall = hall;
        this.guest = guest;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.depositAmount = depositAmount;
        this.status = EventBookingStatus.PENDING;
        this.depositPaid = false;
    }

    // Constructor for an external client
    public EventBooking(EventHall hall, String clientName,
                        String clientPhone, String nationalId,
                        LocalDateTime startDateTime,
                        LocalDateTime endDateTime,
                        double depositAmount) {

        validateBooking(hall, startDateTime,
                endDateTime, depositAmount);

        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException(
                    "Client name is required");
        }

        this.hall = hall;
        this.externalClientName = clientName;
        this.externalClientPhone = clientPhone;
        this.externalClientNationalId = nationalId;

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.depositAmount = depositAmount;

        this.status = EventBookingStatus.PENDING;
        this.depositPaid = false;
    }

    // Common validation for both constructors
    private void validateBooking(
            EventHall hall,
            LocalDateTime start,
            LocalDateTime end,
            double deposit) {

        if (hall == null) {
            throw new IllegalArgumentException(
                    "Hall is required");
        }

        if (start == null || end == null ||
                !end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "End date must be after start date");
        }

        if (!Double.isFinite(deposit) || deposit < 0) {
            throw new IllegalArgumentException(
                    "Invalid deposit amount");
        }
    }

    // Record deposit payment
    public void payDeposit() {

        if (status != EventBookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Deposit cannot be paid in this status");
        }

        if (depositPaid) {
            throw new IllegalStateException(
                    "Deposit already paid");
        }

        depositPaid = true;
    }

    // Confirm only after deposit payment
    public boolean confirmBooking() {

        if (status != EventBookingStatus.PENDING) {
            return false;
        }

        if (!depositPaid) {
            return false;
        }

        status = EventBookingStatus.CONFIRMED;
        return true;
    }

    // Cancel a pending or confirmed booking
    public void cancelBooking(String reason) {

        if (status != EventBookingStatus.PENDING &&
                status != EventBookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Booking cannot be cancelled");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Cancellation reason is required");
        }

        status = EventBookingStatus.CANCELLED;
        cancellationReason = reason;
    }

    public int getBookingId() {
        return bookingId;
    }

    public EventHall getHall() {
        return hall;
    }

    public Guest getGuest() {
        return guest;
    }

    public String getExternalClientName() {
        return externalClientName;
    }

    public String getExternalClientPhone() {
        return externalClientPhone;
    }

    public String getExternalClientNationalId() {
        return externalClientNationalId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public boolean isDepositPaid() {
        return depositPaid;
    }

    public EventBookingStatus getStatus() {
        return status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    // Kept temporarily for old DAO compatibility
    public void assignId(int bookingId) {

        if (this.bookingId != 0 || bookingId <= 0) {
            throw new IllegalStateException(
                    "Invalid booking ID assignment");
        }

        this.bookingId = bookingId;
    }
}
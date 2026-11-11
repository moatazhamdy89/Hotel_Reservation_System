package com.hotel.model;

import com.hotel.enums.EventBookingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "event_invoices")
@PrimaryKeyJoinColumn(name = "invoice_id")
public class EventInvoice extends Invoice {

    // Linked to one event booking; no setter to prevent changing it
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_booking_id", nullable = false, unique = true)
    private EventBooking eventBooking;

    // Required by Hibernate when loading an existing invoice
    protected EventInvoice() {
        super();
    }

    // Create a NEW invoice for a confirmed event booking
    public EventInvoice(EventBooking eventBooking) {
        super();

        if (eventBooking == null)
            throw new IllegalArgumentException(
                    "Event booking is required");

        if (eventBooking.getStatus() != EventBookingStatus.CONFIRMED)
            throw new IllegalArgumentException(
                    "An event invoice can only be issued for a CONFIRMED booking, but status is "
                            + eventBooking.getStatus());

        this.eventBooking = eventBooking;
        this.totalAmount = calculateTotal();
    }

    @Override
    public BigDecimal calculateTotal() {
        return round2(eventBooking.getHall().getRentalPrice());
    }

    @Override
    public BigDecimal getRemainingAmount() {
        if (isPaid)
            return BigDecimal.ZERO;

        BigDecimal paidDeposit = eventBooking.isDepositPaid()
                ? eventBooking.getDepositAmount()
                : BigDecimal.ZERO;

        BigDecimal remaining = totalAmount.subtract(paidDeposit);

        if (remaining.compareTo(BigDecimal.ZERO) < 0)
            return BigDecimal.ZERO;

        return round2(remaining);
    }

    @Override
    public void displayInvoice() {
        System.out.println("=== EVENT INVOICE ===");

        super.displayInvoice();

        System.out.println("Booking ID: "
                + eventBooking.getBookingId());

        System.out.println("Hall: "
                + eventBooking.getHall().getHallName());

        System.out.println("Deposit: "
                + eventBooking.getDepositAmount()
                .setScale(2, java.math.RoundingMode.HALF_UP)
                .toPlainString()
                + (eventBooking.isDepositPaid()
                ? " (paid)"
                : " (not paid)"));
    }

    public EventBooking getEventBooking() {
        return eventBooking;
    }
}

package com.hotel.model;

import com.hotel.enums.EventBookingStatus;

public class EventInvoice extends Invoice {

    // Linked to one event booking; no setter to prevent changing it
    private EventBooking eventBooking;

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

        // Calculate and freeze the total when the invoice is issued
        lockTotal();
    }

    @Override
    public double calculateTotal() {
        return round2(eventBooking.getHall().getRentalPrice());
    }

    @Override
    public double getRemainingAmount() {
        if (isPaid)
            return 0.0;

        double paidDeposit = eventBooking.isDepositPaid()
                ? eventBooking.getDepositAmount()
                : 0.0;

        return round2(Math.max(0.0, totalAmount - paidDeposit));
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
                + String.format("%.2f", eventBooking.getDepositAmount())
                + (eventBooking.isDepositPaid()
                ? " (paid)"
                : " (not paid)"));
    }

    public EventBooking getEventBooking() {
        return eventBooking;
    }
}

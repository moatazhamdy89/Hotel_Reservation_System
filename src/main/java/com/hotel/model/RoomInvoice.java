
package com.hotel.model;

import com.hotel.enums.ReservationStatus;

public class RoomInvoice extends Invoice {

    private Reservation reservation;
    private ServiceOrder serviceOrder;
    private double discountAmount;

    // NEW invoice issued during checkout
    public RoomInvoice(Reservation reservation, ServiceOrder serviceOrder) {
        super();

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Reservation is required");
        }

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Only a CONFIRMED reservation can have a room invoice");
        }

        if (!reservation.isDepositPaid()) {
            throw new IllegalArgumentException(
                    "Deposit must be paid before issuing a room invoice");
        }

        if (serviceOrder != null) {
            Reservation orderReservation = serviceOrder.getReservation();

            if (orderReservation == null) {
                throw new IllegalArgumentException(
                        "Service order has no reservation");
            }

            // Same reservation object OR same saved reservation ID
            boolean sameReservation = orderReservation == reservation
                    || (reservation.getReservationId() > 0
                    && orderReservation.getReservationId()
                    == reservation.getReservationId());

            if (!sameReservation) {
                throw new IllegalArgumentException(
                        "Service order belongs to another reservation");
            }
        }

        this.reservation = reservation;
        this.serviceOrder = serviceOrder;

        lockTotal();
    }

    @Override
    public double calculateTotal() {
        double accommodationCost =
                reservation.calculateAccommodationCost();

        double discountRate = reservation.getGuest()
                .getLoyaltyProgram()
                .calculateDiscount();

        discountAmount = round2(accommodationCost * discountRate);

        return round2(
                accommodationCost
                        - discountAmount
                        + calculateServicesCost());
    }

    public double calculateServicesCost() {
        if (serviceOrder == null) {
            return 0.0;
        }

        double total = 0.0;

        for (Service service : serviceOrder.getServices()) {
            total += service.getPrice();
        }

        return round2(total);
    }

    @Override
    public double getRemainingAmount() {
        if (isPaid) {
            return 0.0;
        }

        double remaining = totalAmount - reservation.getDepositAmount();

        if (remaining < 0) {
            return 0.0;
        }

        return round2(remaining);
    }

    @Override
    public void displayInvoice() {
        System.out.println("=== ROOM INVOICE ===");

        super.displayInvoice();

        System.out.println("Reservation ID: "
                + reservation.getReservationId());

        System.out.println("Nights: "
                + reservation.calculateNights());

        System.out.println("Room + Accommodation: "
                + reservation.calculateAccommodationCost());

        System.out.println("Loyalty Discount: "
                + discountAmount);

        System.out.println("Services Cost: "
                + calculateServicesCost());

        System.out.println("Deposit Paid: "
                + reservation.getDepositAmount());
    }

    public Reservation getReservation() {
        return reservation;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
}

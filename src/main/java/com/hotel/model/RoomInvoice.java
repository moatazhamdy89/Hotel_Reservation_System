package com.hotel.model;

import com.hotel.enums.ReservationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "room_invoices")
@PrimaryKeyJoinColumn(name = "invoice_id")
public class RoomInvoice extends Invoice {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_order_id", unique = true)
    private ServiceOrder serviceOrder;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // Required by Hibernate
    protected RoomInvoice() {
        super();
    }

    // NEW invoice issued during checkout
    public RoomInvoice(Reservation reservation, ServiceOrder serviceOrder) {
        super();

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Reservation is required");
        }


        if (reservation.getStatus() != ReservationStatus.CHECKED_IN
                && reservation.getStatus() != ReservationStatus.CHECKED_OUT) {
            throw new IllegalArgumentException(
                    "Room invoice requires a checked-in or checked-out reservation");
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
        this.totalAmount = calculateTotal();
    }

    @Override
    public BigDecimal calculateTotal() {
        BigDecimal accommodationCost =
                reservation.calculateAccommodationCost();

        BigDecimal discountRate = reservation.getGuest()
                .getLoyaltyProgram()
                .calculateDiscount();

        discountAmount = accommodationCost.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal servicesCost = calculateServicesCost();

        return accommodationCost
                .subtract(discountAmount)
                .add(servicesCost)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateServicesCost() {
        if (serviceOrder == null) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        for (ServiceOrderItem item : serviceOrder.getItems())
            total = total.add(item.getService().calculateTotal(item.getQuantity()));

        return round2(total);
    }

    @Override
    public BigDecimal getRemainingAmount() {
        if (isPaid) {
            return new BigDecimal("0.00");
        }

        BigDecimal depositAmount = reservation.getDepositAmount();
        BigDecimal remaining = totalAmount.subtract(depositAmount != null ? depositAmount : new BigDecimal("0.00"));

        if (remaining != null && remaining.compareTo(BigDecimal.ZERO) < 0) {
            return new BigDecimal("0.00");
        }

        return remaining.setScale(2, RoundingMode.HALF_UP);
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
}
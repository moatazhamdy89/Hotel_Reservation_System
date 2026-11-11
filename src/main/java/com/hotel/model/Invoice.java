package com.hotel.model;

import com.hotel.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "invoices")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    protected int invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    protected PaymentMethod paymentMethod;

    @Column(name = "is_paid")
    protected boolean isPaid;

    @Column(name = "total_amount", precision = 10, scale = 2)
    protected BigDecimal totalAmount;

    // Used when creating a new invoice
    protected Invoice() {
        this.invoiceId = 0;
        this.isPaid = false;
        this.paymentMethod = null;
        this.totalAmount = BigDecimal.ZERO;
    }

    // Checks whether an ID has been assigned.
    // This does NOT guarantee that the transaction was committed.
    public boolean isSaved() {
        return invoiceId > 0;
    }

    // Each invoice subtype calculates its own total
    public abstract BigDecimal calculateTotal();

    // Each invoice subtype calculates its remaining balance
    public abstract BigDecimal getRemainingAmount();

    public void pay(PaymentMethod method) {
        if (method == null)
            throw new IllegalArgumentException(
                    "Payment method is required");

        if (!isSaved())
            throw new IllegalStateException(
                    "Save the invoice before payment");

        if (isPaid)
            throw new IllegalStateException(
                    "Invoice " + invoiceId + " is already paid");

        this.paymentMethod = method;
        this.isPaid = true;
    }

    public void displayInvoice() {
        System.out.println("Invoice ID: " +
                (isSaved()
                        ? String.valueOf(invoiceId)
                        : "(no ID assigned yet)"));

        System.out.println("Total: " +
                totalAmount.setScale(2, RoundingMode.HALF_UP)
                        .toPlainString() + " EGP");

        System.out.println("Remaining: " +
                getRemainingAmount()
                        .setScale(2, RoundingMode.HALF_UP)
                        .toPlainString() + " EGP");

        System.out.println("Status: " +
                (isPaid
                        ? "PAID via " + paymentMethod
                        : "UNPAID"));
    }

    protected static BigDecimal round2(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isPaid() {
        return isPaid;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
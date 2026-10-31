package com.hotel.model;

import com.hotel.enums.PaymentMethod;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "invoices")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    protected int invoiceId;                 // 0 = no ID assigned yet

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    protected PaymentMethod paymentMethod;   // null until paid

    @Column(name = "is_paid")
    protected boolean isPaid;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Column(name = "total_amount", precision = 10, scale = 2)
    protected double totalAmount;            // frozen when issued

    @Transient
    private boolean totalLocked = false;

    // Used when creating a new invoice
    protected Invoice() {
        this.invoiceId = 0;
        this.isPaid = false;
        this.paymentMethod = null;
    }

    // Calculates and freezes the invoice total once
    protected final void lockTotal() {
        if (totalLocked)
            throw new IllegalStateException(
                    "Total of invoice is already fixed");

        double computed = calculateTotal();

        if (!Double.isFinite(computed) || computed < 0)
            throw new IllegalStateException(
                    "Calculated total must be finite and non-negative");

        this.totalAmount = round2(computed);
        this.totalLocked = true;
    }

    // Checks whether an ID has been assigned.
    // This does NOT guarantee that the transaction was committed.
    public boolean isSaved() {
        return invoiceId > 0;
    }

    // Each invoice subtype calculates its own total
    public abstract double calculateTotal();

    // Each invoice subtype calculates its remaining balance
    public abstract double getRemainingAmount();

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
                String.format("%.2f", totalAmount) + " EGP");

        System.out.println("Remaining: " +
                String.format("%.2f", getRemainingAmount()) + " EGP");

        System.out.println("Status: " +
                (isPaid
                        ? "PAID via " + paymentMethod
                        : "UNPAID"));
    }

    protected static double round2(double amount) {
        return Math.round(amount * 100.0) / 100.0;
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

    public double getTotalAmount() {
        return totalAmount;
    }
}

package com.hotel.manager;

import com.hotel.model.Invoice;
import com.hotel.enums.PaymentMethod;
import com.hotel.model.LoyaltyProgram;
import com.hotel.model.RoomInvoice;
import com.hotel.repository.HibernateRepository;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceManager {

    private final HibernateRepository repository =
            new HibernateRepository();

    // Add a new invoice
    public Invoice addInvoice(Invoice invoice) {

        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice cannot be null");
        }

        if (invoice.isSaved()) {
            throw new IllegalStateException(
                    "Invoice is already saved");
        }

        return repository.save(invoice);
    }

    // Find invoice by ID
    public Invoice findInvoiceById(int invoiceId) {

        if (invoiceId <= 0) {
            return null;
        }

        return repository.findById(
                Invoice.class,
                invoiceId
        );
    }

    // Find all invoices
    public List<Invoice> findAllInvoices() {

        return repository.findAll(Invoice.class);
    }

    // Update an existing invoice
    public Invoice updateInvoice(Invoice invoice) {

        if (invoice == null || !invoice.isSaved()) {
            throw new IllegalArgumentException(
                    "A saved invoice is required");
        }

        return repository.update(invoice);
    }

    public Invoice payInvoice(
            int invoiceId,
            PaymentMethod method) {

        if (invoiceId <= 0) {
            throw new IllegalArgumentException("Invalid invoice ID");
        }

        if (method == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        return repository.withTransaction(session -> {

            Invoice invoice = session.find(
                    Invoice.class,
                    invoiceId,
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (invoice == null) {
                throw new IllegalArgumentException("Invoice not found");
            }

            if (invoice.isPaid()) {
                throw new IllegalStateException("Invoice already paid");
            }

            BigDecimal paidAmount = invoice.getRemainingAmount();

            if (invoice instanceof RoomInvoice roomInvoice) {

                int guestId = roomInvoice.getReservation()
                        .getGuest().getUserID();

                LoyaltyProgram loyalty = session.find(
                        LoyaltyProgram.class,
                        guestId,
                        LockModeType.PESSIMISTIC_WRITE
                );

                if (loyalty == null) {
                    throw new IllegalStateException(
                            "Guest loyalty program not found"
                    );
                }

                int earnedPoints = paidAmount
                        .divideToIntegralValue(new BigDecimal("100"))
                        .intValue();

                invoice.pay(method);

                if (earnedPoints > 0) {
                    loyalty.addPoints(earnedPoints);
                }

            } else {
                invoice.pay(method);
            }

            return invoice;
        });
    }
}

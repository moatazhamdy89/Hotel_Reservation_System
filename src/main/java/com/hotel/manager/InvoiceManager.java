
package com.hotel.manager;

import com.hotel.model.Invoice;
import com.hotel.enums.PaymentMethod;
import com.hotel.repository.HibernateRepository;

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
            throw new IllegalArgumentException(
                    "Invalid invoice ID");
        }

        if (method == null) {
            throw new IllegalArgumentException(
                    "Payment method is required");
        }

        return repository.withTransaction(session -> {

            Invoice invoice = session.find(
                    Invoice.class,
                    invoiceId,
                    jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
            );

            if (invoice == null) {
                throw new IllegalArgumentException(
                        "Invoice not found");
            }

            invoice.pay(method);

            // Hibernate automatically saves changes on commit
            return invoice;
        });
    }
}

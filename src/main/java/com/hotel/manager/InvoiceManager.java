
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

    // Pay an existing invoice
    public Invoice payInvoice(
            int invoiceId,
            PaymentMethod method) {

        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice not found");
        }

        invoice.pay(method);

        return repository.update(invoice);
    }
}

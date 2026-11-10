
package com.hotel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "service_order_items")
public class ServiceOrderItem {

    @Id
    @ManyToOne
    @JoinColumn(name = "order_id")
    private ServiceOrder serviceOrder;

    @Id
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    public ServiceOrderItem() {}

    public ServiceOrderItem(ServiceOrder serviceOrder, Service service, int quantity) {
        if (serviceOrder == null || service == null)
            throw new IllegalArgumentException("Order and service are required");

        this.serviceOrder = serviceOrder;
        this.service = service;
        setQuantity(quantity);
    }

    public ServiceOrderItem(ServiceOrder serviceOrder, Service service,
                            int quantity, LocalDate scheduledDate) {
        this(serviceOrder, service, quantity);
        setScheduledDate(scheduledDate);
    }

    public BigDecimal calculateTotal() {
        return service.calculateTotal(quantity);
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");
        this.quantity = quantity;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        if (scheduledDate == null)
            throw new IllegalArgumentException("Scheduled date cannot be null");
        this.scheduledDate = scheduledDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ServiceOrderItem other)) return false;

        return Objects.equals(
                serviceOrder == null ? null : serviceOrder.getOrderId(),
                other.serviceOrder == null ? null : other.serviceOrder.getOrderId()
        ) && Objects.equals(
                service == null ? null : service.getServiceId(),
                other.service == null ? null : other.service.getServiceId()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                serviceOrder == null ? null : serviceOrder.getOrderId(),
                service == null ? null : service.getServiceId()
        );
    }
}

package com.hotel.model;

import jakarta.persistence.*;
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

    public ServiceOrderItem() {
    }

    public ServiceOrderItem(ServiceOrder serviceOrder,
                            Service service,
                            int quantity) {
        this.serviceOrder = serviceOrder;
        this.service = service;
        setQuantity(quantity);
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
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }
        this.quantity = quantity;
    }




    // Compares two items using their order ID and service ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof ServiceOrderItem other))
            return false;

        return Objects.equals(
                serviceOrder == null ? null : serviceOrder.getOrderId(),
                other.serviceOrder == null ? null : other.serviceOrder.getOrderId()
        ) && Objects.equals(
                service == null ? null : service.getServiceId(),
                other.service == null ? null : other.service.getServiceId()
        );
    }


    // Generates a hash code using the order ID and service ID
    @Override
    public int hashCode() {
        return Objects.hash(
                serviceOrder == null ? null : serviceOrder.getOrderId(),
                service == null ? null : service.getServiceId()
        );
    }

}
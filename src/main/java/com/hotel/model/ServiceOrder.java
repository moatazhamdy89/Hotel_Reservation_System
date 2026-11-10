
package com.hotel.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_orders")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ServiceOrderItem> items = new ArrayList<>();

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    protected ServiceOrder() {}

    public ServiceOrder(Reservation reservation) {
        if (reservation == null)
            throw new IllegalArgumentException("Reservation cannot be null");
        this.reservation = reservation;
        this.orderTime = LocalDateTime.now();
    }

    public void addService(Service service) {
        addService(service, 1);
    }

    public void addService(Service service, int quantity) {
        if (service == null)
            throw new IllegalArgumentException("Service cannot be null");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        for (ServiceOrderItem item : items) {
            if (item.getService().getServiceId() == service.getServiceId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        items.add(new ServiceOrderItem(this, service, quantity));
    }

    public void addService(Service service, int quantity, LocalDate scheduledDate) {
        if (service == null)
            throw new IllegalArgumentException("Service cannot be null");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");
        if (scheduledDate == null)
            throw new IllegalArgumentException("Scheduled date is required");

        if (!service.canBookOn(reservation, scheduledDate))
            throw new IllegalArgumentException("Activity is unavailable on this date or outside guest stay");

        for (ServiceOrderItem item : items) {
            if (item.getService().getServiceId() == service.getServiceId()) {
                if (!scheduledDate.equals(item.getScheduledDate()))
                    throw new IllegalArgumentException("This activity is already booked on another date");

                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        items.add(new ServiceOrderItem(this, service, quantity, scheduledDate));
    }

    public void removeService(Service service) {
        if (service == null) return;
        items.removeIf(item -> item.getService().getServiceId() == service.getServiceId());
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public List<ServiceOrderItem> getItems() {
        return items;
    }

    public void setItems(List<ServiceOrderItem> items) {
        List<ServiceOrderItem> newItems = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.items.clear();

        for (ServiceOrderItem item : newItems) {
            item.setServiceOrder(this);
            this.items.add(item);
        }
    }

    public List<Service> getServices() {
        List<Service> services = new ArrayList<>();
        for (ServiceOrderItem item : items)
            services.add(item.getService());
        return services;
    }
}

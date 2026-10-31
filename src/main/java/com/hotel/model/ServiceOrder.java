package com.hotel.model;

import jakarta.persistence.*;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    @ManyToMany
    @JoinTable(
            name = "service_order_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    // Required by Hibernate
    protected ServiceOrder() {
    }

    public void addService(Service service) {
        this.services.add(service);
    }

    public void removeService(Service service) {
        this.services.remove(service);
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<Service> getServices() {
        return services;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
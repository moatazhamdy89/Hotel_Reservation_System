package com.hotel.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class ServiceOrder {
    private int orderId;
    private Reservation reservation;
    private List<Service> services = new ArrayList<>();
    private LocalDateTime orderTime;
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



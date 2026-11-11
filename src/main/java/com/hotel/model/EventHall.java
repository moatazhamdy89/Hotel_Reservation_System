package com.hotel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "event_halls")
public class EventHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_id")
    private int hallId;

    @Column(name = "hall_name")
    private String hallName;

    @Column(name = "location")
    private String location;

    @Column(name = "rental_price", precision = 10, scale = 2)
    private BigDecimal rentalPrice;

    @Column(name = "max_capacity")
    private int maxCapacity;

    // Required by Hibernate
    protected EventHall() {
    }

    // Constructor for creating a new hall
    public EventHall(String hallName, String location,
                     BigDecimal rentalPrice, int maxCapacity) {
        this.setHallName(hallName);
        this.setLocation(location);
        this.setRentalPrice(rentalPrice);
        this.setMaxCapacity(maxCapacity);
    }

    public int getHallId() {
        return hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public String getLocation() {
        return location;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setHallName(String hallName) {
        if (hallName == null || hallName.isBlank()) {
            throw new IllegalArgumentException("Hall name is required");
        }
        this.hallName = hallName;
    }

    public void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location is required");
        }
        this.location = location;
    }

    public void setRentalPrice(BigDecimal rentalPrice) {
        if (rentalPrice == null ||
                rentalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid rental price");
        }
        this.rentalPrice = rentalPrice;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.maxCapacity = maxCapacity;
    }
}
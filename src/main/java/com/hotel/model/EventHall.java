package com.hotel.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "event_halls")
public class EventHall {

    @Id // 3shan da primary key f 3mlna @ID
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    @Column(name = "hall_id")
    private int hallId;

    @Column(name = "hall_name")
    private String hallName;

    @Column(name = "location")
    private String location;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Column(name = "rental_price", precision = 10, scale = 2)
    private double rentalPrice;

    @Column(name = "max_capacity")
    private int maxCapacity;

    // Required by Hibernate
    protected EventHall() {
    }

    // Constructor for creating a new hall
    public EventHall(String hallName, String location,
                     double rentalPrice, int maxCapacity) {
       this.hallName= hallName;
       this.location = location;
       this.rentalPrice = rentalPrice;
       this.maxCapacity = maxCapacity;
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

    public double getRentalPrice() {
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

    public void setRentalPrice(double rentalPrice) {
        if (!Double.isFinite(rentalPrice) || rentalPrice < 0) {
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
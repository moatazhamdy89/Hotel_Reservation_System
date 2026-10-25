package com.hotel.model;
public class EventHall {

    private int hallId;
    private String hallName;
    private String location;
    private double rentalPrice;
    private int maxCapacity;

    // New hall - MySQL generates the ID
    public EventHall(String hallName, String location,
                     double rentalPrice, int maxCapacity) {
        setHallName(hallName);
        setLocation(location);
        setRentalPrice(rentalPrice);
        setMaxCapacity(maxCapacity);
    }

    // Existing hall loaded from database
    public EventHall(int hallId, String hallName, String location,
                     double rentalPrice, int maxCapacity) {
        this(hallName, location, rentalPrice, maxCapacity);
        setHallId(hallId);
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

    public void setHallId(int hallId) {
        if (hallId <= 0) {
            throw new IllegalArgumentException("Invalid hall ID");
        }
        this.hallId = hallId;
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
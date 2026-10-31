package com.hotel.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "service_name", nullable = false, length = 255)
    private String serviceName;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description", columnDefinition = "text")
    private String description;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Column(name = "price", precision = 10, scale = 2)
    private double price;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    // Required by Hibernate
    protected Service() {
    }

    public Service(
            String serviceName,
            String description,
            double price,
            boolean isAvailable,
            LocalDate serviceDate,
            LocalTime startTime) {

        this.setServiceName(serviceName);
        this.description = description;
        this.setPrice(price);
        this.isAvailable = isAvailable;
        this.serviceDate = serviceDate;
        this.startTime = startTime;
    }

    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }

    public String getServiceDetails() {
        return "Service ID: " + serviceId +
                ", Name: " + serviceName +
                ", Description: " + description +
                ", Price: " + price +
                ", Available: " + isAvailable +
                ", Date: " + serviceDate +
                ", Start Time: " + startTime;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public DayOfWeek getDayOfWeek() {
        if (serviceDate != null) {
            return serviceDate.getDayOfWeek();
        } else {
            return null;
        }
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                    "Service price must be valid and non-negative"
            );
        }

        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getServiceId() {
        return serviceId;
    }
}
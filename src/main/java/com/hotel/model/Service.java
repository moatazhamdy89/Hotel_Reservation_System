
package com.hotel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "service_name", nullable = false, length = 255)
    private String serviceName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_day")
    private DayOfWeek serviceDay;

    protected Service() {}

    public Service(String serviceName, String description, BigDecimal price,
                   boolean isAvailable, LocalDate serviceDate, LocalTime startTime) {
        setServiceName(serviceName);
        this.description = description;
        setPrice(price);
        this.isAvailable = isAvailable;
        this.serviceDate = serviceDate;
        this.startTime = startTime;
    }

    public void toggleAvailability() {
        isAvailable = !isAvailable;
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
        return serviceDate == null ? null : serviceDate.getDayOfWeek();
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Service price must be valid and non-negative");
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

    public DayOfWeek getServiceDay() {
        return serviceDay;
    }

    public void setServiceDay(DayOfWeek serviceDay) {
        if (serviceDay == null)
            throw new IllegalArgumentException("Service day cannot be null");
        this.serviceDay = serviceDay;
    }

    public LocalDate getNextServiceDate(LocalDate fromDate) {
        if (fromDate == null || serviceDay == null)
            throw new IllegalArgumentException("Date and service day are required");
        return fromDate.with(TemporalAdjusters.nextOrSame(serviceDay));
    }

    public BigDecimal calculateTotal(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public List<LocalDate> getAvailableDates(Reservation reservation) {
        List<LocalDate> dates = new ArrayList<>();
        if (reservation == null || !isAvailable || serviceDay == null) return dates;

        LocalDate start = reservation.getCheckInDate();
        LocalDate end = reservation.getCheckOutDate();
        if (start == null || end == null) return dates;
        if (start.isBefore(LocalDate.now())) start = LocalDate.now();

        for (LocalDate date = getNextServiceDate(start);
             date.isBefore(end); date = date.plusWeeks(1))
            dates.add(date);

        return dates;
    }

    public boolean canBookOn(Reservation reservation, LocalDate date) {
        return date != null && getAvailableDates(reservation).contains(date);
    }
}

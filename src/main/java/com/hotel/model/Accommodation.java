package com.hotel.model;

import com.hotel.enums.AccommodationType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accommodations")
public class Accommodation {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AccommodationType type;

    @Column(name = "price_per_night", precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    protected Accommodation() {
    }

    public Accommodation(AccommodationType type, BigDecimal pricePerNight) {
        if (type == null) {
            throw new IllegalArgumentException("Accommodation type is required");
        }

        validatePrice(pricePerNight);

        this.type = type;
        this.pricePerNight = pricePerNight;
    }

    public AccommodationType getType() {
        return type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        validatePrice(pricePerNight);
        this.pricePerNight = pricePerNight;
    }

    public static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid Price");
        }
    }

    @Override
    public String toString() {
        return getType() + " (" + getPricePerNight() + " EGP/Night)";
    }
}
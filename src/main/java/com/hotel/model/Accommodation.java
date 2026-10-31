package com.hotel.model;

import com.hotel.enums.AccommodationType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "accommodations")
public class Accommodation {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private  AccommodationType type;   // final: the type is the primary key, so it never changes

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Column(name = "price_per_night", precision = 10, scale = 2)
    private double pricePerNight;

    protected Accommodation(){}
    public Accommodation(AccommodationType type, double pricePerNight) {
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

    public double getPricePerNight() {
        return choosingAccommodationType();
    }

    public void setPricePerNight(double pricePerNight) {
        validatePrice(pricePerNight);
        this.pricePerNight = pricePerNight;
    }

    public double choosingAccommodationType ( ){
        if (type == AccommodationType.BREAKFAST) {
            pricePerNight = 600;
        } else if (type == AccommodationType.HALF_BOARD) {
            pricePerNight = 1000;
        } else {
            pricePerNight = 1500;
        }
        return pricePerNight;
    }


    public static void validatePrice(double price) {
        if (!Double.isFinite(price) || price < 0) { // because Double.isInFinite() can't catch NAN.
            throw new IllegalArgumentException("Invalid Price");
        }
    }

    @Override
    public String toString() {
        return getType() + " (" + getPricePerNight() + " EGP/Night)";
    }
}
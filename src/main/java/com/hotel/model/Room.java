package com.hotel.model;

import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "rooms")
@Inheritance(strategy = InheritanceType.JOINED)
public class Room {

    @Id
    @Column(name = "room_id")
    protected int roomId;

    @Column(name = "room_number")
    protected int roomNumber;

    @Column(name = "floor_number")
    protected int floorNumber;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Column(name = "base_price", precision = 10, scale = 2)
    protected double basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected RoomStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "view_type")
    protected ViewType viewType;

    @Column(name = "description")
    protected String description;

    @Column(name = "max_guests")
    protected int maxGuests;

    // Required by Hibernate
    protected Room() {
    }

    public Room(int roomId, int roomNumber, int floorNumber,
                double basePrice, RoomStatus status, ViewType viewType,
                String description, int maxGuests) {

        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floorNumber = floorNumber;
        this.basePrice = basePrice > 0 ? basePrice : 100.0;
        this.status = status != null ? status : RoomStatus.AVAILABLE;
        this.viewType = viewType;
        this.description = description;
        this.maxGuests = maxGuests > 0 ? maxGuests : 2;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double newPrice) {
        if (newPrice > 0) {
            this.basePrice = newPrice;
        }
    }

    int getMaxGuests() {
        return maxGuests;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public boolean isAvailable() {
        return this.status == RoomStatus.AVAILABLE;
    }

    public void setStatus(RoomStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
    }
}
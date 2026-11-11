package com.hotel.model;

import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Room {

    @Id
    @Column(name = "room_id")
    protected int roomId;

    @Column(name = "room_number")
    protected int roomNumber;

    @Column(name = "floor_number")
    protected int floorNumber;

    @Column(name = "base_price", precision = 10, scale = 2)
    protected BigDecimal basePrice;

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
                BigDecimal basePrice, RoomStatus status, ViewType viewType,
                String description, int maxGuests) {

        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floorNumber = floorNumber;

        this.basePrice = (basePrice != null && basePrice.compareTo(BigDecimal.ZERO) > 0)
                ? basePrice
                : new BigDecimal("100.00");

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

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal newPrice) {
        if (newPrice != null && newPrice.compareTo(BigDecimal.ZERO) > 0) {
            this.basePrice = newPrice;
        }
    }

    public int getMaxGuests() {
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

    public RoomStatus getStatus() {
        return status;
    }
}
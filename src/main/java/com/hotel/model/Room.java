package com.hotel.model;

import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;

public class Room {
    protected int roomId;
    protected int roomNumber;
    protected int floorNumber;
    protected double basePrice;
    protected RoomStatus status;
    protected ViewType viewType;
    protected String description;
    protected int maxGuests;

    public Room(int roomId, int roomNumber, int floorNumber, double basePrice, RoomStatus status, ViewType viewType, String description, int maxGuests) {
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
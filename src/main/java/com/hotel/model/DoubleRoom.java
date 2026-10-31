package com.hotel.model;

import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;
import jakarta.persistence.*;

@Entity
@Table(name = "double_rooms")
@PrimaryKeyJoinColumn(name = "room_id")
public class DoubleRoom extends Room {

    @Column(name = "extra_bed_available")
    private boolean doubleBedAvailable;

    // Required by Hibernate
    protected DoubleRoom() {
        super();
    }

    public DoubleRoom(int roomId, int roomNumber, int floorNumber,
                      double basePrice, RoomStatus status,
                      ViewType viewType, String description,
                      boolean doubleBedAvailable) {

        super(roomId, roomNumber, floorNumber, basePrice,
                status, viewType, description, 2);

        this.doubleBedAvailable = doubleBedAvailable;
    }

    public boolean canAddExtraBed() {
        boolean isAvailable = (this.status == RoomStatus.AVAILABLE);
        boolean hasCapacity = (this.maxGuests >= 2);
        return isAvailable && hasCapacity && doubleBedAvailable;
    }

    public boolean isDoubleBedAvailable() {
        return doubleBedAvailable;
    }

    public void setDoubleBedAvailable(boolean doubleBedAvailable) {
        this.doubleBedAvailable = doubleBedAvailable;
    }
}
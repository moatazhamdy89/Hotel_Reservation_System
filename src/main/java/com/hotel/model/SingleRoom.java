package com.hotel.model;

import com.hotel.enums.BedType;
import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;
import jakarta.persistence.*;

@Entity
@Table(name = "single_rooms")
@PrimaryKeyJoinColumn(name = "room_id")
public class SingleRoom extends Room {

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type")
    private BedType bedType;

    // Required by Hibernate
    protected SingleRoom() {
        super();
    }

    public SingleRoom(int roomId, int roomNumber, int floorNumber,
                      double basePrice, RoomStatus status,
                      ViewType viewType, String description,
                      BedType bedType) {

        super(roomId, roomNumber, floorNumber, basePrice,
                status, viewType, description, 1);

        this.bedType = bedType != null ? bedType : BedType.SINGLE;
    }

    public BedType getBedType() {
        return bedType;
    }

    public void setBedType(BedType bedType) {
        if (bedType != null) {
            this.bedType = bedType;
        }
    }
}
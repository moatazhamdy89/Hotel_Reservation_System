package com.hotel.model;

import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;
import jakarta.persistence.*;

@Entity
@Table(name = "vip_rooms")
@PrimaryKeyJoinColumn(name = "room_id")
public class VIPRoom extends Room {

    @Column(name = "has_private_pool_or_jacuzzi")
    private boolean hasPrivatePoolOrJacuzzi;

    @Column(name = "has_butler_service")
    private boolean hasButlerService;

    @Column(name = "has_vip_lounge_access")
    private boolean hasVipLoungeAccess;

    // Required by Hibernate
    protected VIPRoom() {
        super();
    }

    public VIPRoom(int roomId, int roomNumber, int floorNumber,
                   double basePrice, RoomStatus status, ViewType viewType,
                   String description, boolean hasPrivatePoolOrJacuzzi,
                   boolean hasButlerService, boolean hasVipLoungeAccess) {

        super(roomId, roomNumber, floorNumber, basePrice,
                status, viewType, description, 3);

        this.hasPrivatePoolOrJacuzzi = hasPrivatePoolOrJacuzzi;
        this.hasButlerService = hasButlerService;
        this.hasVipLoungeAccess = hasVipLoungeAccess;
    }

    public boolean hasButlerService() {
        return this.status == RoomStatus.AVAILABLE && hasButlerService;
    }

    public void setHasButlerService(boolean hasButlerService) {
        this.hasButlerService = hasButlerService;
    }

    public boolean hasVipLoungeAccess() {
        return this.status != RoomStatus.UNDER_MAINTAINANCE
                && hasVipLoungeAccess;
    }

    public void setHasVipLoungeAccess(boolean hasVipLoungeAccess) {
        this.hasVipLoungeAccess = hasVipLoungeAccess;
    }

    public boolean hasPrivatePoolOrJacuzzi() {
        return hasPrivatePoolOrJacuzzi;
    }

    public void setHasPrivatePoolOrJacuzzi(boolean hasPrivatePoolOrJacuzzi) {
        this.hasPrivatePoolOrJacuzzi = hasPrivatePoolOrJacuzzi;
    }
}
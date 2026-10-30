package com.hotel.model;

import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;

public class SuiteRoom extends Room {
    private int numberOfRooms;
    private boolean hasLivingRoom;
    private boolean hasDiningArea;

    public SuiteRoom(int roomId, int roomNumber, int floorNumber, double basePrice, RoomStatus status, ViewType viewType, String description, int numberOfRooms, boolean hasLivingRoom, boolean hasDiningArea) {
        super(roomId, roomNumber, floorNumber, basePrice, status, viewType, description, 4);
        this.numberOfRooms = numberOfRooms > 0 ? numberOfRooms : 2;
        this.hasLivingRoom = hasLivingRoom;
        this.hasDiningArea = hasDiningArea;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        if (numberOfRooms > 0) {
            this.numberOfRooms = numberOfRooms;
        }
    }

    public boolean hasLivingRoom() {
        return hasLivingRoom;
    }

    public void setHasLivingRoom(boolean hasLivingRoom) {
        this.hasLivingRoom = hasLivingRoom;
    }

    public boolean hasDiningArea() {
        return hasDiningArea;
    }

    public void setHasDiningArea(boolean hasDiningArea) {
        this.hasDiningArea = hasDiningArea;
    }
}

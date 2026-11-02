
package com.hotel.manager;

import com.hotel.model.Room;
import com.hotel.enums.RoomStatus;
import com.hotel.repository.HibernateRepository;

import java.util.List;

public class RoomManager {

    private final HibernateRepository repository =
            new HibernateRepository();

    // Add a new room
    public Room addRoom(Room room) {

        if (room == null) {
            throw new IllegalArgumentException(
                    "Room cannot be null");
        }

        return repository.save(room);
    }

    // Find a room by ID
    public Room findRoomById(int roomId) {

        if (roomId <= 0) {
            return null;
        }

        return repository.findById(Room.class, roomId);
    }

    // Find all rooms and room subtypes
    public List<Room> findAllRooms() {
        return repository.findAll(Room.class);
    }

    // Update an existing room
    public Room updateRoom(Room room) {

        if (room == null || room.getRoomId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid room is required");
        }

        return repository.update(room);
    }

    // Update room price
    public Room updateRoomPrice(int roomId, double newPrice) {

        if (!Double.isFinite(newPrice) || newPrice <= 0) {
            throw new IllegalArgumentException(
                    "Price must be positive and finite");
        }

        Room room = findRoomById(roomId);

        if (room == null) {
            throw new IllegalArgumentException(
                    "Room not found");
        }

        room.setBasePrice(newPrice);

        return repository.update(room);
    }

    // Update room status
    public Room updateRoomStatus(
            int roomId,
            RoomStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Status is required");
        }

        Room room = findRoomById(roomId);

        if (room == null) {
            throw new IllegalArgumentException(
                    "Room not found");
        }

        room.setStatus(newStatus);

        return repository.update(room);
    }

    // Delete room by ID
    public boolean deleteRoom(int roomId) {

        if (roomId <= 0) {
            return false;
        }

        return repository.deleteById(Room.class, roomId);
    }
}

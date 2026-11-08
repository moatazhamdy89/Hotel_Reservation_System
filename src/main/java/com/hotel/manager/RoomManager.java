package com.hotel.manager;

import com.hotel.model.Room;
import com.hotel.model.RoomSearchFilter;
import com.hotel.enums.RoomStatus;
import com.hotel.repository.HibernateRepository;

import java.time.LocalDate;
import java.util.Comparator;
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

    // Find the cheapest room matching guest requirements
    public Room findMatchingRoom(
            RoomSearchFilter filter,
            LocalDate checkIn,
            LocalDate checkOut) {

        if (filter == null) {
            throw new IllegalArgumentException(
                    "Room search filter is required");
        }

        if (checkIn == null || checkOut == null
                || checkIn.isBefore(LocalDate.now())
                || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Invalid reservation dates");
        }

        ReservationManager reservationManager =
                new ReservationManager();

        List<Room> rooms = findAllRooms();

        Room selectedRoom = null;

        for (Room room : rooms) {

            // 1. Check room requirements
            if (!filter.matches(room)) {
                continue;
            }

            // 2. Check room availability
            if (!reservationManager.isRoomAvailable(
                    room, checkIn, checkOut)) {
                continue;
            }

            // 3. Select the first matching room
            if (selectedRoom == null) {
                selectedRoom = room;
            }

            // 4. Select a cheaper room if found
            else if (room.getBasePrice()
                    < selectedRoom.getBasePrice()) {
                selectedRoom = room;
            }

            // 5. If prices are equal, select smaller ID
            else if (room.getBasePrice()
                    == selectedRoom.getBasePrice()
                    && room.getRoomId()
                    < selectedRoom.getRoomId()) {
                selectedRoom = room;
            }
        }

        return selectedRoom;
    }

    // Delete room by ID
    public boolean deleteRoom(int roomId) {

        if (roomId <= 0) {
            return false;
        }

        return repository.deleteById(Room.class, roomId);
    }
}
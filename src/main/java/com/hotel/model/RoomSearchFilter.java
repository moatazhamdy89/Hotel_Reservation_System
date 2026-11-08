package com.hotel.model;

import com.hotel.enums.ViewType;

public class RoomSearchFilter {

    private final Class<? extends Room> desiredRoomType;
    private final ViewType desiredView;
    private final double minPrice;
    private final double maxPrice;
    private final int numberOfGuests;

    public RoomSearchFilter(
            Class<? extends Room> desiredRoomType,
            ViewType desiredView,
            double minPrice,
            double maxPrice,
            int numberOfGuests) {

        if (desiredRoomType != SingleRoom.class &&
                desiredRoomType != DoubleRoom.class &&
                desiredRoomType != SuiteRoom.class &&
                desiredRoomType != VIPRoom.class) {
            throw new IllegalArgumentException(
                    "Please select a valid room type");
        }

        if (numberOfGuests <= 0) {
            throw new IllegalArgumentException(
                    "Number of guests must be positive");
        }

        if (!Double.isFinite(minPrice) ||
                !Double.isFinite(maxPrice) ||
                minPrice < 0 ||
                maxPrice < minPrice) {
            throw new IllegalArgumentException(
                    "Invalid price range");
        }

        this.desiredRoomType = desiredRoomType;
        this.desiredView = desiredView;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.numberOfGuests = numberOfGuests;
    }

    public boolean matches(Room room) {

        if (room == null) {
            return false;
        }

        boolean matchesType =
                desiredRoomType.isInstance(room); // check it is from class room or anything extends from room or not

        boolean matchesView =
                desiredView == null ||
                        room.getViewType() == desiredView;

        boolean matchesPrice =
                room.getBasePrice() >= minPrice &&
                        room.getBasePrice() <= maxPrice;

        boolean matchesCapacity =
                room.getMaxGuests() >= numberOfGuests;

        return matchesType &&
                matchesView &&
                matchesPrice &&
                matchesCapacity;
    }

    public Class<? extends Room> getDesiredRoomType() {
        return desiredRoomType;
    }

    public ViewType getDesiredView() {
        return desiredView;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }
}
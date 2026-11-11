package com.hotel.model;

import com.hotel.enums.ViewType;

import java.math.BigDecimal;

public class RoomSearchFilter {

    private final Class<? extends Room> desiredRoomType;
    private final ViewType desiredView;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final int numberOfGuests;

    public RoomSearchFilter(
            Class<? extends Room> desiredRoomType,
            ViewType desiredView,
            BigDecimal minPrice,
            BigDecimal maxPrice,
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

        if (minPrice == null || maxPrice == null ||
                minPrice.compareTo(BigDecimal.ZERO) < 0 ||
                maxPrice.compareTo(minPrice) < 0) {
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
                desiredRoomType.isInstance(room);

        boolean matchesView =
                desiredView == null ||
                        room.getViewType() == desiredView;

        boolean matchesPrice =
                room.getBasePrice().compareTo(minPrice) >= 0 &&
                        room.getBasePrice().compareTo(maxPrice) <= 0;

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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }
}
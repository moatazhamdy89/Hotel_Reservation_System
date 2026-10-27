package com.hotel.model;
import com.hotel.enums.ViewType;

public class RoomSearchFilter {
        private ViewType desiredView;
        private double minPrice;
        private double maxPrice;

        public RoomSearchFilter(ViewType desiredView, double minPrice, double maxPrice) {
            this.desiredView = desiredView;
            this.minPrice = minPrice >= 0 ? minPrice : 0.0;
            this.maxPrice = maxPrice >= this.minPrice ? maxPrice : Double.MAX_VALUE;
        }

        public boolean matches(Room room) {
            if (room == null) {
                return false;
            }

            boolean matchesView = (this.desiredView == null || room.getViewType() == this.desiredView);
            boolean matchesPrice = (room.getBasePrice() >= this.minPrice && room.getBasePrice() <= this.maxPrice);
            boolean isAvailable = room.isAvailable();

            return matchesView && matchesPrice && isAvailable;
        }

        public ViewType getDesiredView() {
            return desiredView;
        }

        public void setDesiredView(ViewType desiredView) {
            this.desiredView = desiredView;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(double minPrice) {
            if (minPrice >= 0) {
                this.minPrice = minPrice;
            }
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(double maxPrice) {
            if (maxPrice >= this.minPrice) {
                this.maxPrice = maxPrice;
            }
        }
    }


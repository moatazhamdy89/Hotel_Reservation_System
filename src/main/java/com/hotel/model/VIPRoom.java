package com.hotel.model;
import com.hotel.enums.RoomStatus;
import com.hotel.enums.ViewType;

    public class VIPRoom extends Room {
        private boolean hasPrivatePoolOrJacuzzi;
        private boolean hasButlerService;
        private boolean hasVipLoungeAccess;

        public VIPRoom(int roomId, int roomNumber, int floorNumber, double basePrice, RoomStatus status, ViewType viewType, String description, boolean hasPrivatePoolOrJacuzzi, boolean hasButlerService, boolean hasVipLoungeAccess) {
            super(roomId, roomNumber, floorNumber, basePrice, status, viewType, description, 3);
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
            return this.status != RoomStatus.UNDER_MAINTENANCE && hasVipLoungeAccess;
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


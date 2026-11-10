
package com.hotel;

import com.hotel.enums.AccommodationType;
import com.hotel.enums.PaymentMethod;
import com.hotel.manager.*;
import com.hotel.model.*;
import com.hotel.repository.HibernateRepository;
import com.hotel.utils.HibernateUtil;

import com.hotel.enums.RoomStatus;
import com.hotel.enums.ReservationStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class HotelTest {

    public static void main(String[] args) {

        // TEST 0: Hibernate Connection
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("PASS: Hibernate connected successfully!");
        } catch (Exception e) {
            System.out.println("FAIL: Hibernate connection failed!");
            e.printStackTrace();
            return;
        }

        RoomManager roomManager = new RoomManager();
        UserManager userManager = new UserManager();
        ReservationManager reservationManager = new ReservationManager();

        // TEST 1: Find All Rooms
        System.out.println("\n===== TEST 1: FIND ALL ROOMS =====");

        try {
            List<Room> rooms = roomManager.findAllRooms();

            System.out.println("Total rooms: " + rooms.size());

            for (Room room : rooms) {
                System.out.println(
                        "ID: " + room.getRoomId()
                                + " | Number: " + room.getRoomNumber()
                                + " | Type: " + room.getClass().getSimpleName()
                                + " | Status: " + room.getStatus()
                );
            }

            if (!rooms.isEmpty()) {
                System.out.println("PASS: Rooms loaded successfully!");
            } else {
                System.out.println("FAIL: No rooms found!");
            }

            // TEST 2: Find Room By ID
            System.out.println("\n===== TEST 2: FIND ROOM BY ID =====");

            if (!rooms.isEmpty()) {
                int roomId = rooms.get(0).getRoomId();

                Room foundRoom = roomManager.findRoomById(roomId);

                if (foundRoom != null
                        && foundRoom.getRoomId() == roomId) {
                    System.out.println("PASS: Found room ID " + roomId);
                } else {
                    System.out.println("FAIL: Room lookup failed!");
                }
            }

        } catch (Exception e) {
            System.out.println("FAIL: Room tests failed!");
            e.printStackTrace();
        }

        // TEST 3: Find All Users
        System.out.println("\n===== TEST 3: FIND ALL USERS =====");

        try {
            List<User> users = userManager.findAllUsers();

            System.out.println("Total users: " + users.size());

            for (User user : users) {
                System.out.println(
                        "ID: " + user.getUserID()
                                + " | Type: " + user.getClass().getSimpleName()
                );
            }

            if (!users.isEmpty()) {
                System.out.println("PASS: Users loaded successfully!");
            } else {
                System.out.println("FAIL: No users found!");
            }

            // TEST 4: Find User By ID
            System.out.println("\n===== TEST 4: FIND USER BY ID =====");

            if (!users.isEmpty()) {
                int userId = users.get(0).getUserID();

                User foundUser = userManager.findUserById(userId);

                if (foundUser != null
                        && foundUser.getUserID() == userId) {
                    System.out.println("PASS: Found user ID " + userId);
                } else {
                    System.out.println("FAIL: User lookup failed!");
                }
            }

        } catch (Exception e) {
            System.out.println("FAIL: User tests failed!");
            e.printStackTrace();
        }

        // TEST 5: Find All Reservations
        System.out.println("\n===== TEST 5: FIND ALL RESERVATIONS =====");

        try {
            List<Reservation> reservations =
                    reservationManager.findAllReservations();

            System.out.println(
                    "Total reservations: " + reservations.size()
            );

            for (Reservation reservation : reservations) {
                System.out.println(
                        "ID: " + reservation.getReservationId()
                                + " | Status: " + reservation.getStatus()
                                + " | Check-in: " + reservation.getCheckInDate()
                                + " | Check-out: " + reservation.getCheckOutDate()
                );
            }

            if (!reservations.isEmpty()) {
                System.out.println("PASS: Reservations loaded successfully!");
            } else {
                System.out.println("FAIL: No reservations found!");
            }

            // TEST 6: Find Reservation By ID
            System.out.println(
                    "\n===== TEST 6: FIND RESERVATION BY ID ====="
            );

            if (!reservations.isEmpty()) {
                int reservationId =
                        reservations.get(0).getReservationId();

                Reservation foundReservation =
                        reservationManager.findReservationById(reservationId);

                if (foundReservation != null
                        && foundReservation.getReservationId()
                        == reservationId) {
                    System.out.println(
                            "PASS: Found reservation ID " + reservationId
                    );
                } else {
                    System.out.println("FAIL: Reservation lookup failed!");
                }
            }

        } catch (Exception e) {
            System.out.println("FAIL: Reservation tests failed!");
            e.printStackTrace();
        }

        // TEST 7: Existing reservation blocks overlapping dates
        System.out.println(
                "\n===== TEST 7: DOUBLE BOOKING PREVENTION ====="
        );

        try {
            List<Reservation> reservations =
                    reservationManager.findAllReservations();

            Reservation selected = null;

            for (Reservation reservation : reservations) {
                if (reservation.getStatus() != ReservationStatus.CANCELLED
                        && !reservation.getCheckInDate()
                        .isBefore(LocalDate.now())) {
                    selected = reservation;
                    break;
                }
            }

            if (selected == null) {
                System.out.println(
                        "SKIP: No active future reservation found"
                );
            } else {
                boolean available = reservationManager.isRoomAvailable(
                        selected.getRoom(),
                        selected.getCheckInDate(),
                        selected.getCheckOutDate()
                );

                if (!available) {
                    System.out.println(
                            "PASS: Existing reservation blocks overlap"
                    );
                } else {
                    System.out.println(
                            "FAIL: Double booking was allowed"
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("FAIL: Double booking test failed!");
            e.printStackTrace();
        }

        // TEST 8: Maintenance room cannot be booked
        System.out.println(
                "\n===== TEST 8: MAINTENANCE ROOM ====="
        );

        try {
            List<Room> rooms = roomManager.findAllRooms();

            Room maintenanceRoom = null;

            for (Room room : rooms) {
                if (room.getStatus() == RoomStatus.UNDER_MAINTAINANCE) {
                    maintenanceRoom = room;
                    break;
                }
            }

            if (maintenanceRoom == null) {
                System.out.println(
                        "SKIP: No maintenance room found"
                );
            } else {
                boolean available = reservationManager.isRoomAvailable(
                        maintenanceRoom,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3)
                );

                if (!available) {
                    System.out.println(
                            "PASS: Maintenance room is unavailable"
                    );
                } else {
                    System.out.println(
                            "FAIL: Maintenance room was available"
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("FAIL: Maintenance room test failed!");
            e.printStackTrace();
        }

        // TEST 9: Find a room available for a future period
        System.out.println(
                "\n===== TEST 9: AVAILABLE ROOM ====="
        );

        try {
            List<Room> rooms = roomManager.findAllRooms();

            LocalDate checkIn = LocalDate.now().plusDays(30);
            LocalDate checkOut = checkIn.plusDays(2);

            Room availableRoom = null;

            for (Room room : rooms) {
                if (reservationManager.isRoomAvailable(
                        room, checkIn, checkOut)) {
                    availableRoom = room;
                    break;
                }
            }

            if (availableRoom != null) {
                System.out.println(
                        "PASS: Available room found: "
                                + availableRoom.getRoomId()
                );
            } else {
                System.out.println(
                        "INFO: No available room found for selected dates"
                );
            }

        } catch (Exception e) {
            System.out.println("FAIL: Available room test failed!");
            e.printStackTrace();
        }

        // TEST 10: Invalid reservation dates
        System.out.println(
                "\n===== TEST 10: INVALID DATES ====="
        );

        try {
            List<Room> rooms = roomManager.findAllRooms();

            if (rooms.isEmpty()) {
                System.out.println("SKIP: No rooms found");
            } else {
                Room room = rooms.get(0);

                try {
                    reservationManager.isRoomAvailable(
                            room,
                            LocalDate.now().plusDays(5),
                            LocalDate.now().plusDays(2)
                    );

                    System.out.println(
                            "FAIL: Invalid dates were accepted"
                    );

                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "PASS: Invalid dates rejected"
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("FAIL: Invalid dates test failed!");
            e.printStackTrace();
        }





        // TEST 11: Prepare real database data for reservation
        System.out.println(
                "\n===== TEST 11: RESERVATION PREPARATION ====="
        );

        try {
            List<User> users = userManager.findAllUsers();
            List<Room> rooms = roomManager.findAllRooms();

            Guest selectedGuest = null;
            Room selectedRoom = null;

            LocalDate checkIn = LocalDate.now().plusDays(7);
            LocalDate checkOut = checkIn.plusDays(3);

            // Find an existing Guest
            for (User user : users) {
                if (user instanceof Guest) {
                    selectedGuest = (Guest) user;
                    break;
                }
            }

            // Find an available room
            for (Room room : rooms) {
                if (reservationManager.isRoomAvailable(
                        room, checkIn, checkOut)) {
                    selectedRoom = room;
                    break;
                }
            }

            if (selectedGuest == null) {
                System.out.println("SKIP: No Guest found");
            } else if (selectedRoom == null) {
                System.out.println("SKIP: No available Room found");
            } else {
                System.out.println(
                        "PASS: Guest ID = "
                                + selectedGuest.getUserID()
                );

                System.out.println(
                        "PASS: Room ID = "
                                + selectedRoom.getRoomId()
                );

                System.out.println(
                        "Check-in: " + checkIn
                );

                System.out.println(
                        "Check-out: " + checkOut
                );

                System.out.println(
                        "READY: Reservation can be created"
                );
            }

        } catch (Exception e) {
            System.out.println(
                    "FAIL: Reservation preparation failed"
            );
            e.printStackTrace();
        }




        // TEST 12: Read existing accommodations
        System.out.println(
                "\n===== TEST 12: FIND ACCOMMODATIONS ====="
        );

        try {
            HibernateRepository repository =
                    new HibernateRepository();

            List<Accommodation> accommodations =
                    repository.findAll(Accommodation.class);

            System.out.println(
                    "Total accommodations: " + accommodations.size()
            );

            for (Accommodation accommodation : accommodations) {
                System.out.println(
                        "Type: " + accommodation.getType()
                                + " | Price per night: "
                                + accommodation.getPricePerNight()
                                + " EGP"
                );
            }

            if (!accommodations.isEmpty()) {
                System.out.println(
                        "PASS: Accommodations loaded successfully!"
                );
            } else {
                System.out.println(
                        "FAIL: No accommodations found!"
                );
            }

        } catch (Exception e) {
            System.out.println(
                    "FAIL: Accommodation test failed!"
            );
            e.printStackTrace();
        }





        // TEST 13: Auto Matching + Create Reservation
//        System.out.println(
//                "\n===== TEST 13: AUTO MATCHING RESERVATION ====="
//        );
//
//        try {
//            HibernateRepository repository =
//                    new HibernateRepository();
//
//            User user = userManager.findUserById(10);
//
//            Accommodation accommodation = repository.findById(
//                    Accommodation.class,
//                    AccommodationType.BREAKFAST
//            );
//
//            LocalDate checkIn = LocalDate.of(2026, 11, 17);
//            LocalDate checkOut = LocalDate.of(2026, 11, 20);
//
//            RoomSearchFilter filter = new RoomSearchFilter(
//                    SingleRoom.class,
//                    null,
//                    new BigDecimal("0"),
//                    new BigDecimal("10000"),
//                    1
//            );
//
//            if (!(user instanceof Guest)) {
//                System.out.println("SKIP: User 10 is not a Guest");
//
//            } else if (accommodation == null) {
//                System.out.println("SKIP: Accommodation not found");
//
//            } else {
//                Room room = roomManager.findMatchingRoom(
//                        filter, checkIn, checkOut
//                );
//
//                if (room == null) {
//                    System.out.println(
//                            "SKIP: No matching available room"
//                    );
//                } else {
//                    System.out.println(
//                            "Auto-selected Room ID: "
//                                    + room.getRoomId()
//                    );
//
//                    System.out.println(
//                            "Room type: "
//                                    + room.getClass().getSimpleName()
//                    );
//
//                    System.out.println(
//                            "Room price: "
//                                    + room.getBasePrice()
//                    );
//
//                    Reservation reservation = new Reservation(
//                            checkIn,
//                            checkOut,
//                            accommodation,
//                            (Guest) user,
//                            room
//                    );
//
//                    System.out.println(
//                            "Accommodation cost: "
//                                    + reservation.calculateAccommodationCost()
//                    );
//
//                    System.out.println(
//                            "Deposit: "
//                                    + reservation.calculateDeposit()
//                    );
//
//                    boolean saved =
//                            reservationManager.addReservation(reservation);
//
//                    if (saved) {
//                        Reservation loaded =
//                                reservationManager.findReservationById(
//                                        reservation.getReservationId()
//                                );
//
//                        if (loaded != null
//                                && loaded.getStatus()
//                                == ReservationStatus.PENDING) {
//
//                            System.out.println(
//                                    "PASS: Reservation saved as PENDING"
//                            );
//
//                            System.out.println(
//                                    "Reservation ID: "
//                                            + loaded.getReservationId()
//                            );
//
//                        } else {
//                            System.out.println(
//                                    "FAIL: Reservation verification failed"
//                            );
//                        }
//
//                    } else {
//                        System.out.println(
//                                "FAIL: Reservation was not saved"
//                        );
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println(
//                    "FAIL: Auto matching reservation test failed"
//            );
//            e.printStackTrace();
//        }




        // TEST 14: Pay Deposit and Confirm Reservation
        System.out.println(
                "\n===== TEST 14: PAY DEPOSIT ====="
        );

        try {
            int reservationId = 129;

            Reservation before =
                    reservationManager.findReservationById(reservationId);

            if (before == null) {
                System.out.println("FAIL: Reservation not found");

            } else if (before.getStatus() != ReservationStatus.PENDING) {
                System.out.println(
                        "SKIP: Reservation is not PENDING. Status: "
                                + before.getStatus()
                );

            } else {
                System.out.println(
                        "Before payment: " + before.getStatus()
                );

                Reservation updated =
                        reservationManager.payDeposit(reservationId);

                Reservation reloaded =
                        reservationManager.findReservationById(reservationId);

                if (updated != null
                        && reloaded != null
                        && reloaded.getStatus()
                        == ReservationStatus.CONFIRMED
                        && reloaded.isDepositPaid()) {

                    System.out.println(
                            "PASS: Deposit paid successfully"
                    );

                    System.out.println(
                            "Status: " + reloaded.getStatus()
                    );

                } else {
                    System.out.println(
                            "FAIL: Deposit payment verification failed"
                    );
                }
            }

        } catch (Exception e) {
            System.out.println(
                    "FAIL: Deposit payment test failed"
            );
            e.printStackTrace();
        }



        // ==========================================
        // TEST 15: LOAD ALL SERVICES
        // ==========================================

        System.out.println("\n===== TEST 15: LOAD ALL SERVICES =====");

        try {
            HibernateRepository serviceRepository =
                    new HibernateRepository();

            List<Service> services =
                    serviceRepository.findAll(Service.class);

            if (services == null || services.isEmpty()) {
                System.out.println("FAIL: No services found");
            } else {
                for (Service service : services) {
                    System.out.println(
                            "ID: " + service.getServiceId()
                                    + " | Name: " + service.getServiceName()
                                    + " | Price: " + service.getPrice()
                                    + " | Available: " + service.isAvailable()
                    );
                }

                System.out.println(
                        "PASS: Loaded " + services.size() + " services"
                );
            }

        } catch (Exception e) {
            System.out.println("FAIL: Test 15");
            e.printStackTrace();
        }


        // ==========================================
        // TEST 16: FIND SERVICE BY ID
        // ==========================================

        System.out.println("\n===== TEST 16: FIND SERVICE BY ID =====");

        try {
            HibernateRepository serviceRepository =
                    new HibernateRepository();

            Service service =
                    serviceRepository.findById(Service.class, 1);

            if (service != null
                    && "Desert Safari".equals(service.getServiceName())) {

                System.out.println("Service: " + service.getServiceName());
                System.out.println("Price: " + service.getPrice());
                System.out.println("PASS: Service found successfully");

            } else {
                System.out.println(
                        "FAIL: Service not found or incorrect data"
                );
            }

        } catch (Exception e) {
            System.out.println("FAIL: Test 16");
            e.printStackTrace();
        }



        System.out.println("\n========== TEST 15: WEEKLY ACTIVITIES ==========");

        HibernateRepository repository = new HibernateRepository();

        List<Service> services = repository.findAll(Service.class);

        System.out.println("Total activities: " + services.size());

        for (Service service : services) {

            System.out.println("-------------------------");
            System.out.println("ID: " + service.getServiceId());
            System.out.println("Name: " + service.getServiceName());
            System.out.println("Day: " + service.getServiceDay());
            System.out.println("Time: " + service.getStartTime());
            System.out.println("Price: " + service.getPrice());
            System.out.println("Available: " + service.isAvailable());
        }

        System.out.println("========== END TEST 15 ==========");



        System.out.println("\n========== TEST 16: WEEKLY SCHEDULE ==========");

        Service safari = repository.findById(Service.class, 1);

        if (safari == null) {
            System.out.println("FAIL: Desert Safari not found");
        } else {

            LocalDate fromDate = LocalDate.of(2026, 11, 17);

            LocalDate nextDate = safari.getNextServiceDate(fromDate);

            System.out.println("Activity: " + safari.getServiceName());
            System.out.println("Scheduled Day: " + safari.getServiceDay());
            System.out.println("Search From: " + fromDate);
            System.out.println("Next Activity Date: " + nextDate);

            if (nextDate.equals(LocalDate.of(2026, 11, 21))) {
                System.out.println("TEST 16 PASSED");
            } else {
                System.out.println("TEST 16 FAILED");
            }
        }

        System.out.println("========== END TEST 16 ==========");




//        System.out.println("\n========== TEST 17: CREATE SERVICE ORDER ==========");
//
//        ServiceOrderManager serviceManager = new ServiceOrderManager();
//
//        int reservationId = 129;
//
//        ServiceOrder existingOrder =
//                serviceManager.findOrderByReservationId(reservationId);
//
//        if (existingOrder != null) {
//
//            System.out.println("Service order already exists.");
//            System.out.println("Order ID: " + existingOrder.getOrderId());
//            System.out.println("TEST 17 PASSED");
//
//        } else {
//
//            Reservation reservation =
//                    repository.findById(Reservation.class, reservationId);
//
//            if (reservation == null) {
//
//                System.out.println("TEST 17 FAILED: Reservation not found");
//
//            } else {
//
//                ServiceOrder newOrder = new ServiceOrder(reservation);
//
//                boolean created = serviceManager.addOrder(newOrder);
//
//                ServiceOrder savedOrder =
//                        serviceManager.findOrderByReservationId(reservationId);
//
//                if (created && savedOrder != null) {
//
//                    System.out.println("Reservation ID: " + reservationId);
//                    System.out.println("Service Order ID: " + savedOrder.getOrderId());
//                    System.out.println("Order Time: " + savedOrder.getOrderTime());
//                    System.out.println("TEST 17 PASSED");
//
//                } else {
//
//                    System.out.println("TEST 17 FAILED");
//                }
//            }
//        }
//
//        System.out.println("========== END TEST 17 ==========");




        System.out.println("\n========== DEBUG TEST 18 ==========");

        Service service = repository.findById(Service.class, 13);

        if (service == null) {
            System.out.println("Service ID 5 NOT FOUND");
        } else {
            System.out.println("Service Name: " + service.getServiceName());
            System.out.println("Available: " + service.isAvailable());
            System.out.println("Service Day: " + service.getServiceDay());
        }

        System.out.println("========== END DEBUG ==========");



        System.out.println("\n========== TEST 18: BOOK WEEKLY ACTIVITY ==========");

        ServiceOrderManager manager = new ServiceOrderManager();

        int orderId = 45;
        int serviceId = 13;
        LocalDate activityDate = LocalDate.of(2026, 11, 18);

        ServiceOrder order = manager.findOrderById(orderId);

        if (order == null) {
            System.out.println("TEST 18 FAILED: Order not found");
        } else {

            boolean alreadyBooked = false;

            for (ServiceOrderItem item : order.getItems()) {
                if (item.getService().getServiceId() == serviceId) {
                    alreadyBooked = true;
                    System.out.println("Activity already booked.");
                    System.out.println("Scheduled Date: " + item.getScheduledDate());
                    System.out.println("Quantity: " + item.getQuantity());
                    break;
                }
            }

            if (!alreadyBooked) {

                boolean added = manager.addService(
                        orderId,
                        serviceId,
                        2,
                        activityDate
                );

                if (added) {

                    ServiceOrder updatedOrder = manager.findOrderById(orderId);

                    boolean verified = false;

                    for (ServiceOrderItem item : updatedOrder.getItems()) {

                        if (item.getService().getServiceId() == serviceId
                                && activityDate.equals(item.getScheduledDate())
                                && item.getQuantity() == 2) {

                            verified = true;
                            System.out.println("Activity: " +
                                    item.getService().getServiceName());
                            System.out.println("Scheduled Date: " +
                                    item.getScheduledDate());
                            System.out.println("Quantity: " +
                                    item.getQuantity());
                        }
                    }

                    System.out.println(verified
                            ? "TEST 18 PASSED"
                            : "TEST 18 FAILED: Saved data mismatch");

                } else {
                    System.out.println("TEST 18 FAILED: Could not add activity");
                }
            } else {
                System.out.println("TEST 18 SKIPPED: Existing booking");
            }
        }

        System.out.println("========== END TEST 18 ==========");




        System.out.println("\n========== TEST 19: INVALID ACTIVITY DAY ==========");

        ServiceOrderManager manager19 = new ServiceOrderManager();

        int orderId19 = 45;
        int serviceId19 = 13;

// Thursday, but Snorkeling is only on Wednesday
        LocalDate invalidDate = LocalDate.of(2026, 11, 19);

        try {

            boolean result = manager19.addService(
                    orderId19,
                    serviceId19,
                    1,
                    invalidDate
            );

            if (!result) {
                System.out.println("TEST 19 PASSED: Booking rejected");
            } else {
                System.out.println("TEST 19 FAILED: Invalid booking accepted");
            }

        } catch (IllegalArgumentException e) {

            System.out.println("Rejected reason: " + e.getMessage());
            System.out.println("TEST 19 PASSED");
        }

        System.out.println("========== END TEST 19 ==========");




        System.out.println("\n========== TEST 20: OUTSIDE GUEST STAY ==========");

        ServiceOrderManager manager20 = new ServiceOrderManager();

        try {

            boolean result = manager20.addService(
                    45,
                    13,
                    1,
                    LocalDate.of(2026, 11, 25)
            );

            if (result) {
                System.out.println("TEST 20 FAILED: Booking accepted");
            } else {
                System.out.println("TEST 20 FAILED: Unexpected rejection");
            }

        } catch (IllegalArgumentException e) {

            if ("Activity date must be within guest stay"
                    .equals(e.getMessage())) {

                System.out.println("Rejected reason: " + e.getMessage());
                System.out.println("TEST 20 PASSED");

            } else {

                System.out.println("TEST 20 FAILED: " + e.getMessage());
            }
        }

        System.out.println("========== END TEST 20 ==========");



        System.out.println("\n========== TEST 21: REVIEW BEFORE CHECKOUT ==========");

        Reservation reservation21 =
                repository.findById(Reservation.class, 129);

        if (reservation21 == null) {

            System.out.println("TEST 21 FAILED: Reservation not found");

        } else {

            try {

                Review review21 = Review.create(
                        reservation21,
                        5,
                        "Amazing hotel experience!"
                );

                System.out.println(
                        "TEST 21 FAILED: Review was created too early"
                );

            } catch (IllegalStateException e) {

                if ("You can review only after the stay ends"
                        .equals(e.getMessage())) {

                    System.out.println("Rejected reason: " + e.getMessage());
                    System.out.println("TEST 21 PASSED");

                } else {

                    System.out.println("TEST 21 FAILED: " + e.getMessage());
                }
            }
        }

        System.out.println("========== END TEST 21 ==========");


        System.out.println("\n========== FULL REVIEW MODULE TEST ==========");

        HibernateRepository reviewRepo = new HibernateRepository();

        int passed = 0;
        int failed = 0;

// TEST 21: Read all reviews
        try {
            List<Review> reviews = Review.findAllReviews();
            System.out.println("TEST 21 PASSED: Found " + reviews.size() + " reviews");
            passed++;
        } catch (Exception e) {
            System.out.println("TEST 21 FAILED: " + e.getMessage());
            failed++;
        }

// TEST 22: Count reviews
        try {
            long total = Review.getTotalReviews();
            System.out.println("TEST 22 PASSED: Total reviews = " + total);
            passed++;
        } catch (Exception e) {
            System.out.println("TEST 22 FAILED: " + e.getMessage());
            failed++;
        }

// TEST 23: Average rating
        try {
            double average = Review.getAverageRating();

            if (average >= 0 && average <= 5) {
                System.out.println("TEST 23 PASSED: Average = " + average);
                passed++;
            } else {
                System.out.println("TEST 23 FAILED: Invalid average");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("TEST 23 FAILED: " + e.getMessage());
            failed++;
        }

// Find an existing checked-out reservation
        Reservation eligibleReservation = null;

        for (Reservation r : reviewRepo.findAll(Reservation.class)) {
            if (r.getStatus() == ReservationStatus.CHECKED_OUT
                    && !r.getCheckOutDate().isAfter(LocalDate.now())
                    && Review.findFor(r).isEmpty()) {

                eligibleReservation = r;
                break;
            }
        }

// TEST 24: Reject invalid rating
        try {
            if (eligibleReservation != null) {
                Review.create(eligibleReservation, 6, "Invalid rating");
            } else {
                Reservation r = reviewRepo.findById(Reservation.class, 129);
                Review.create(r, 6, "Invalid rating");
            }

            System.out.println("TEST 24 FAILED: Rating 6 accepted");
            failed++;

        } catch (IllegalArgumentException e) {
            System.out.println("TEST 24 PASSED: " + e.getMessage());
            passed++;
        }

// TEST 25: Reject review before checkout
        try {
            Reservation r = reviewRepo.findById(Reservation.class, 129);

            if (r == null) {
                System.out.println("TEST 25 SKIPPED: Reservation 129 not found");
            } else {
                Review.create(r, 5, "Too early");

                System.out.println("TEST 25 FAILED: Early review accepted");
                failed++;
            }

        } catch (IllegalStateException e) {
            if ("Only a CHECKED_OUT reservation can be reviewed"
                    .equals(e.getMessage())) {
                System.out.println("TEST 25 PASSED: " + e.getMessage());
                passed++;
            } else {
                System.out.println("TEST 25 FAILED: " + e.getMessage());
                failed++;
            }
        }

// TEST 26-29: Create, Find, Edit, Duplicate
        if (eligibleReservation == null) {

            System.out.println(
                    "TESTS 26-29 SKIPPED: No eligible checked-out reservation"
            );

        } else {

            int reservationId = eligibleReservation.getReservationId();

            try {
                // TEST 26: Create
                Review created = Review.create(
                        eligibleReservation,
                        5,
                        "Excellent hotel experience"
                );

                if (created.getReviewId() > 0) {
                    System.out.println("TEST 26 PASSED: Review ID = "
                            + created.getReviewId());
                    passed++;
                } else {
                    System.out.println("TEST 26 FAILED: Review ID not generated");
                    failed++;
                }

                // TEST 27: Find
                Review found = Review.findFor(eligibleReservation)
                        .orElse(null);

                if (found != null && found.getReviewId() == created.getReviewId()) {
                    System.out.println("TEST 27 PASSED: Review found");
                    passed++;
                } else {
                    System.out.println("TEST 27 FAILED: Review not found");
                    failed++;
                }

                // TEST 28: Edit
                created.editReview(4, "Very good stay");

                Review updated = Review.findFor(eligibleReservation)
                        .orElse(null);

                if (updated != null
                        && updated.getRating() == 4
                        && "Very good stay".equals(updated.getComment())) {

                    System.out.println("TEST 28 PASSED: Review updated");
                    passed++;
                } else {
                    System.out.println("TEST 28 FAILED: Update not saved");
                    failed++;
                }

                // TEST 29: Reject duplicate
                try {
                    Review.create(
                            eligibleReservation,
                            5,
                            "Duplicate review"
                    );

                    System.out.println("TEST 29 FAILED: Duplicate accepted");
                    failed++;

                } catch (IllegalStateException e) {
                    if ("This reservation already has a review"
                            .equals(e.getMessage())) {

                        System.out.println("TEST 29 PASSED: Duplicate rejected");
                        passed++;
                    } else {
                        System.out.println("TEST 29 FAILED: " + e.getMessage());
                        failed++;
                    }
                }

            } catch (Exception e) {
                System.out.println("TESTS 26-29 ERROR: " + e.getMessage());
                failed++;
                e.printStackTrace();
            }
        }

        System.out.println("\n========== REVIEW TEST SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("=========================================");





        System.out.println("\n========== FULL INVOICE & PAYMENT TEST ==========");

        InvoiceManager invoiceManager = new InvoiceManager();

        int invoicePassed = 0;
        int invoiceFailed = 0;
        int invoiceSkipped = 0;

// TEST 30: Find all invoices
        List<Invoice> invoices = null;

        try {
            invoices = invoiceManager.findAllInvoices();

            System.out.println("TEST 30 PASSED: Found "
                    + invoices.size() + " invoices");
            invoicePassed++;
        } catch (Exception e) {
            System.out.println("TEST 30 FAILED: " + e.getMessage());
            invoiceFailed++;
        }

// TEST 31: Find invoice by invalid ID
        try {
            Invoice result = invoiceManager.findInvoiceById(-1);

            if (result == null) {
                System.out.println("TEST 31 PASSED: Invalid ID rejected");
                invoicePassed++;
            } else {
                System.out.println("TEST 31 FAILED");
                invoiceFailed++;
            }
        } catch (Exception e) {
            System.out.println("TEST 31 FAILED: " + e.getMessage());
            invoiceFailed++;
        }

// TEST 32: Reject null invoice
        try {
            invoiceManager.addInvoice(null);
            System.out.println("TEST 32 FAILED");
            invoiceFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("TEST 32 PASSED: " + e.getMessage());
            invoicePassed++;
        }

// TEST 33: Reject unsaved invoice update
        try {
            invoiceManager.updateInvoice(null);
            System.out.println("TEST 33 FAILED");
            invoiceFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("TEST 33 PASSED: " + e.getMessage());
            invoicePassed++;
        }

        if (invoices != null) {

            // TEST 34: Validate all invoice totals
            try {
                boolean valid = true;

                for (Invoice invoice : invoices) {
                    if (invoice.getTotalAmount() == null
                            || invoice.getTotalAmount()
                            .compareTo(BigDecimal.ZERO) < 0) {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    System.out.println("TEST 34 PASSED: Invoice totals valid");
                    invoicePassed++;
                } else {
                    System.out.println("TEST 34 FAILED: Invalid total found");
                    invoiceFailed++;
                }
            } catch (Exception e) {
                System.out.println("TEST 34 FAILED: " + e.getMessage());
                invoiceFailed++;
            }

            // TEST 35: Validate remaining balances
            try {
                boolean valid = true;

                for (Invoice invoice : invoices) {
                    BigDecimal remaining = invoice.getRemainingAmount();

                    if (remaining == null
                            || remaining.compareTo(BigDecimal.ZERO) < 0
                            || remaining.compareTo(invoice.getTotalAmount()) > 0) {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    System.out.println("TEST 35 PASSED: Remaining balances valid");
                    invoicePassed++;
                } else {
                    System.out.println("TEST 35 FAILED: Invalid remaining amount");
                    invoiceFailed++;
                }
            } catch (Exception e) {
                System.out.println("TEST 35 FAILED: " + e.getMessage());
                invoiceFailed++;
            }


            System.out.println("\n========== DIAGNOSTIC TEST 36 ==========");

            for (Invoice invoice : invoiceManager.findAllInvoices()) {

                if (!(invoice instanceof RoomInvoice)) {
                    continue;
                }

                RoomInvoice roomInvoice = (RoomInvoice) invoice;

                try {
                    BigDecimal accommodation =
                            roomInvoice.getReservation()
                                    .calculateAccommodationCost();

                    BigDecimal discount =
                            roomInvoice.getDiscountAmount();

                    BigDecimal services1 =
                            roomInvoice.calculateServicesCost();

                    BigDecimal expected = accommodation
                            .subtract(discount)
                            .add(services1);

                    BigDecimal stored = roomInvoice.getTotalAmount();

                    if (expected.compareTo(stored) != 0) {

                        System.out.println("MISMATCH FOUND!");
                        System.out.println("Invoice ID: "
                                + roomInvoice.getInvoiceId());

                        System.out.println("Accommodation: " + accommodation);
                        System.out.println("Discount: " + discount);
                        System.out.println("Services: " + services);
                        System.out.println("Expected Total: " + expected);
                        System.out.println("Stored Total: " + stored);
                        System.out.println("Difference: "
                                + expected.subtract(stored));

                        break;
                    }

                } catch (Exception e) {
                    System.out.println("Invoice #"
                            + roomInvoice.getInvoiceId()
                            + " ERROR: " + e.getMessage());
                    break;
                }
            }

            System.out.println("========== END DIAGNOSTIC 36 ==========");


//            // TEST 37: Event invoice calculations
//            try {
//                int count = 0;
//
//                for (Invoice invoice : invoices) {
//                    if (invoice instanceof EventInvoice) {
//                        EventInvoice eventInvoice = (EventInvoice) invoice;
//
//                        BigDecimal expected = eventInvoice
//                                .getEventBooking()
//                                .getHall()
//                                .getRentalPrice();
//
//                        if (expected.compareTo(eventInvoice.getTotalAmount()) != 0) {
//                            throw new IllegalStateException(
//                                    "Event invoice total mismatch");
//                        }
//
//                        count++;
//                    }
//                }
//
//                if (count == 0) {
//                    System.out.println("TEST 37 SKIPPED: No event invoices");
//                    invoiceSkipped++;
//                } else {
//                    System.out.println("TEST 37 PASSED: Checked "
//                            + count + " event invoices");
//                    invoicePassed++;
//                }
//            } catch (Exception e) {
//                System.out.println("TEST 37 FAILED: " + e.getMessage());
//                invoiceFailed++;
//            }
//
//            // Find an unpaid invoice
//            Invoice unpaidInvoice = null;
//
//            for (Invoice invoice : invoices) {
//                if (!invoice.isPaid()) {
//                    unpaidInvoice = invoice;
//                    break;
//                }
//            }
//
//            if (unpaidInvoice == null) {
//                System.out.println("TESTS 38-41 SKIPPED: No unpaid invoices");
//                invoiceSkipped += 4;
//            } else {
//
//                int invoiceId = unpaidInvoice.getInvoiceId();
//
//                // TEST 38: Reject null payment method
//                try {
//                    invoiceManager.payInvoice(invoiceId, null);
//
//                    System.out.println("TEST 38 FAILED");
//                    invoiceFailed++;
//
//                } catch (IllegalArgumentException e) {
//                    System.out.println("TEST 38 PASSED: " + e.getMessage());
//                    invoicePassed++;
//                }
//
//                // TEST 39: Pay invoice
//                try {
//                    Invoice paid = invoiceManager.payInvoice(
//                            invoiceId,
//                            PaymentMethod.CASH
//                    );
//
//                    if (paid.isPaid()
//                            && paid.getPaymentMethod() == PaymentMethod.CASH) {
//
//                        System.out.println("TEST 39 PASSED: Invoice #"
//                                + invoiceId + " paid successfully");
//                        invoicePassed++;
//                    } else {
//                        System.out.println("TEST 39 FAILED");
//                        invoiceFailed++;
//                    }
//                } catch (Exception e) {
//                    System.out.println("TEST 39 FAILED: " + e.getMessage());
//                    invoiceFailed++;
//                }
//
//                // TEST 40: Verify payment persisted
//                try {
//                    Invoice reloaded = invoiceManager.findInvoiceById(invoiceId);
//
//                    if (reloaded != null
//                            && reloaded.isPaid()
//                            && reloaded.getPaymentMethod() == PaymentMethod.CASH
//                            && reloaded.getRemainingAmount()
//                            .compareTo(BigDecimal.ZERO) == 0) {
//
//                        System.out.println("TEST 40 PASSED: Payment saved in DB");
//                        invoicePassed++;
//                    } else {
//                        System.out.println("TEST 40 FAILED");
//                        invoiceFailed++;
//                    }
//                } catch (Exception e) {
//                    System.out.println("TEST 40 FAILED: " + e.getMessage());
//                    invoiceFailed++;
//                }
//
//                // TEST 41: Reject duplicate payment
//                try {
//                    invoiceManager.payInvoice(
//                            invoiceId,
//                            PaymentMethod.VISA
//                    );
//
//                    System.out.println("TEST 41 FAILED: Duplicate accepted");
//                    invoiceFailed++;
//
//                } catch (IllegalStateException e) {
//                    System.out.println("TEST 41 PASSED: " + e.getMessage());
//                    invoicePassed++;
//                }
//            }
//        }
//
//        System.out.println("\n========== INVOICE TEST SUMMARY ==========");
//        System.out.println("Passed: " + invoicePassed);
//        System.out.println("Failed: " + invoiceFailed);
//        System.out.println("Skipped: " + invoiceSkipped);
//        System.out.println("==========================================");


            System.out.println("\n========== INVOICE DIAGNOSTIC ==========");

            HibernateRepository repo = new HibernateRepository();

            try {
                repo.withTransaction(session -> {

                    // ROOM INVOICE #1019
                    RoomInvoice room = session.createSelectionQuery(
                                    "select i from RoomInvoice i " +
                                            "join fetch i.reservation r " +
                                            "join fetch r.guest " +
                                            "where i.invoiceId = :id",
                                    RoomInvoice.class
                            )
                            .setParameter("id", 1019)
                            .uniqueResult();

                    if (room != null) {
                        System.out.println("\nROOM INVOICE #1019");
                        System.out.println("Accommodation: "
                                + room.getReservation()
                                .calculateAccommodationCost());
                        System.out.println("Discount: "
                                + room.getDiscountAmount());
                        System.out.println("Calculated Services: "
                                + room.calculateServicesCost());
                        System.out.println("Stored Total: "
                                + room.getTotalAmount());
                        System.out.println("Recalculated Total: "
                                + room.calculateTotal());
                    }

                    // EVENT INVOICE
                    List<EventInvoice> events =
                            session.createSelectionQuery(
                                    "select i from EventInvoice i " +
                                            "join fetch i.eventBooking b " +
                                            "join fetch b.hall",
                                    EventInvoice.class
                            ).getResultList();

                    System.out.println("\nEVENT INVOICES FOUND: "
                            + events.size());

                    for (EventInvoice event : events) {
                        System.out.println(
                                "Invoice #" + event.getInvoiceId()
                                        + " | Hall Price: "
                                        + event.getEventBooking()
                                        .getHall()
                                        .getRentalPrice()
                                        + " | Stored Total: "
                                        + event.getTotalAmount()
                        );
                    }

                    return null;
                });

            } catch (Exception e) {
                System.out.println("DIAGNOSTIC FAILED:");
                e.printStackTrace();
            }
        }

            System.out.println("========== END DIAGNOSTIC ==========");



        System.out.println("\n========== ROOM INVOICE CALCULATION TEST ==========");

        boolean found = false;

        for (Reservation reservation : reservationManager.findAllReservations()) {

            if ((reservation.getStatus() == ReservationStatus.CHECKED_IN
                    || reservation.getStatus() == ReservationStatus.CHECKED_OUT)
                    && reservation.isDepositPaid()) {

                try {
                    RoomInvoice invoice = new RoomInvoice(reservation, null);

                    BigDecimal accommodation =
                            reservation.calculateAccommodationCost();

                    BigDecimal discount = invoice.getDiscountAmount();

                    BigDecimal expected = accommodation.subtract(discount)
                            .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal actual = invoice.getTotalAmount();

                    System.out.println("Reservation ID: "
                            + reservation.getReservationId());
                    System.out.println("Expected: " + expected);
                    System.out.println("Actual: " + actual);

                    if (expected.compareTo(actual) == 0) {
                        System.out.println("TEST PASSED");
                    } else {
                        System.out.println("TEST FAILED");
                    }

                    found = true;
                    break;

                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    break;
                }
            }
        }

        if (!found) {
            System.out.println("TEST SKIPPED: No eligible reservation found");
        }

        System.out.println("========== END TEST ==========");






        System.out.println("\n========== TEST 42: ROOM INVOICE WITH SERVICES ==========");

        try {
            Reservation testReservation = null;

            // STEP 1: Find an eligible reservation
            for (Reservation r : reservationManager.findAllReservations()) {

                if ((r.getStatus() == ReservationStatus.CHECKED_IN
                        || r.getStatus() == ReservationStatus.CHECKED_OUT)
                        && r.isDepositPaid()) {

                    testReservation = r;
                    break;
                }
            }

            if (testReservation == null) {
                System.out.println("TEST 42 SKIPPED: No eligible reservation found");

            } else {

                // STEP 2: Create a new ServiceOrder in memory
                ServiceOrder testOrder = new ServiceOrder(testReservation);

                System.out.println("\n--- RESERVATION CHECK ---");
                System.out.println("Reservation ID: "
                        + testReservation.getReservationId());

                System.out.println("Order Reservation ID: "
                        + testOrder.getReservation().getReservationId());

                System.out.println("Same Object: "
                        + (testReservation == testOrder.getReservation()));

                System.out.println("Same ID: "
                        + (testReservation.getReservationId()
                        == testOrder.getReservation().getReservationId()));

                // STEP 3: Create a test service
                Service testService = new Service(
                        "Test Spa",
                        "Invoice calculation test",
                        new BigDecimal("500.00"),
                        true,
                        null,
                        null
                );

                // Quantity = 3, total services = 1500
                testOrder.addService(testService, 3);

                // STEP 4: Create RoomInvoice (not saved)
                RoomInvoice testInvoice = new RoomInvoice(
                        testReservation,
                        testOrder
                );

                // STEP 5: Calculate expected total
                BigDecimal accommodation =
                        testReservation.calculateAccommodationCost();

                BigDecimal discount =
                        testInvoice.getDiscountAmount();

                BigDecimal servicesCost =
                        new BigDecimal("500.00")
                                .multiply(BigDecimal.valueOf(3));

                BigDecimal expected = accommodation
                        .subtract(discount)
                        .add(servicesCost)
                        .setScale(2, RoundingMode.HALF_UP);

                BigDecimal actual = testInvoice.getTotalAmount();

                System.out.println("\n--- INVOICE CALCULATION ---");
                System.out.println("Accommodation: " + accommodation);
                System.out.println("Discount: " + discount);
                System.out.println("Services: " + servicesCost);
                System.out.println("Expected Total: " + expected);
                System.out.println("Actual Total: " + actual);

                if (expected.compareTo(actual) == 0) {
                    System.out.println("\nTEST 42 PASSED");
                } else {
                    System.out.println("\nTEST 42 FAILED: Total mismatch");
                }
            }

        } catch (Exception e) {
            System.out.println("TEST 42 FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========== END TEST 42 ==========");


        System.out.println("\n========== AUTHENTICATION TESTS 43-52 ==========");

        UserManager authManager = new UserManager();

        int authPassed = 0;
        int authFailed = 0;
        int authSkipped = 0;

        Guest authGuest = null;
        String testPassword = "Hotel@2026Test";
        String testEmail = "authtest" + System.nanoTime()
                + "@hoteltest.com";

// TEST 43: Register Guest
        try {
            authGuest = authManager.registerGuest(
                    "123456789",
                    "Test",
                    "Guest",
                    "Female",
                    22,
                    testEmail,
                    testPassword,
                    "Egyptian",
                    "01012345678"
            );

            if (authGuest != null && authGuest.getUserID() > 0) {
                System.out.println("TEST 43 PASSED: Guest ID = "
                        + authGuest.getUserID());
                authPassed++;
            } else {
                System.out.println("TEST 43 FAILED");
                authFailed++;
            }
        } catch (Exception e) {
            System.out.println("TEST 43 FAILED: " + e.getMessage());
            authFailed++;
        }

// TEST 44: Guest saved in database
        try {
            if (authGuest == null) {
                System.out.println("TEST 44 SKIPPED");
                authSkipped++;
            } else {
                User saved = authManager.findUserById(
                        authGuest.getUserID());

                if (saved instanceof Guest
                        && saved.getEmail().equals(testEmail)) {
                    System.out.println("TEST 44 PASSED: Guest found in DB");
                    authPassed++;
                } else {
                    System.out.println("TEST 44 FAILED");
                    authFailed++;
                }
            }
        } catch (Exception e) {
            System.out.println("TEST 44 FAILED: " + e.getMessage());
            authFailed++;
        }

// TEST 45: Correct login
        try {
            if (authGuest == null) {
                System.out.println("TEST 45 SKIPPED");
                authSkipped++;
            } else {
                User saved = authManager.findUserById(
                        authGuest.getUserID());

                if (saved.login(testEmail, testPassword)) {
                    System.out.println("TEST 45 PASSED: Login successful");
                    authPassed++;
                } else {
                    System.out.println("TEST 45 FAILED");
                    authFailed++;
                }
            }
        } catch (Exception e) {
            System.out.println("TEST 45 FAILED: " + e.getMessage());
            authFailed++;
        }

// TEST 46: Wrong password rejected
        try {
            if (authGuest == null) {
                System.out.println("TEST 46 SKIPPED");
                authSkipped++;
            } else {
                User saved = authManager.findUserById(
                        authGuest.getUserID());

                if (!saved.login(testEmail, "WrongPassword@123")) {
                    System.out.println("TEST 46 PASSED: Wrong password rejected");
                    authPassed++;
                } else {
                    System.out.println("TEST 46 FAILED");
                    authFailed++;
                }
            }
        } catch (Exception e) {
            System.out.println("TEST 46 FAILED: " + e.getMessage());
            authFailed++;
        }

// TEST 47: Wrong email rejected
        try {
            if (authGuest == null) {
                System.out.println("TEST 47 SKIPPED");
                authSkipped++;
            } else {
                User saved = authManager.findUserById(
                        authGuest.getUserID());

                if (!saved.login("wrong@example.com", testPassword)) {
                    System.out.println("TEST 47 PASSED: Wrong email rejected");
                    authPassed++;
                } else {
                    System.out.println("TEST 47 FAILED");
                    authFailed++;
                }
            }
        } catch (Exception e) {
            System.out.println("TEST 47 FAILED: " + e.getMessage());
            authFailed++;
        }

// TEST 48: Logout
        try {
            if (authGuest == null) {
                System.out.println("TEST 48 SKIPPED");
                authSkipped++;
            } else {
                User saved = authManager.findUserById(
                        authGuest.getUserID());

                saved.login(testEmail, testPassword);
                saved.logout();

                if (!saved.isLoggedIn()) {
                    System.out.println("TEST 48 PASSED: Logout successful");
                    authPassed++;
                } else {
                    System.out.println("TEST 48 FAILED");
                    authFailed++;
                }
            }
        } catch (Exception e) {
            System.out.println("TEST 48 FAILED: " + e.getMessage());
            authFailed++;
        }

// TEST 49: Invalid email rejected
        try {
            Guest.createForRegistration(
                    "123456789",
                    "Invalid",
                    "Email",
                    "Female",
                    22,
                    "invalid-email",
                    testPassword,
                    "Egyptian",
                    "01012345678"
            );

            System.out.println("TEST 49 FAILED");
            authFailed++;

        } catch (IllegalArgumentException e) {
            System.out.println("TEST 49 PASSED: " + e.getMessage());
            authPassed++;
        }

// TEST 50: Weak password rejected
        try {
            Guest.createForRegistration(
                    "123456789",
                    "Weak",
                    "Password",
                    "Female",
                    22,
                    "weaktest@example.com",
                    "123",
                    "Egyptian",
                    "01012345678"
            );

            System.out.println("TEST 50 FAILED");
            authFailed++;

        } catch (IllegalArgumentException e) {
            System.out.println("TEST 50 PASSED: Weak password rejected");
            authPassed++;
        }

// TEST 51: Non-admin cannot register Receptionist
        try {
            int guestId = authGuest == null ? -1 : authGuest.getUserID();

            authManager.registerReceptionist(
                    guestId,
                    "123456789",
                    "Test",
                    "Receptionist",
                    "Female",
                    25,
                    "receptiontest@example.com",
                    testPassword,
                    "01012345678",
                    "Morning"
            );

            System.out.println("TEST 51 FAILED");
            authFailed++;

        } catch (SecurityException e) {
            System.out.println("TEST 51 PASSED: " + e.getMessage());
            authPassed++;
        }

// TEST 52: Non-admin cannot register Admin
        try {
            int guestId = authGuest == null ? -1 : authGuest.getUserID();

            authManager.registerAdmin(
                    guestId,
                    "123456789",
                    "Test",
                    "Admin",
                    "Female",
                    25,
                    "admintest@example.com",
                    testPassword
            );

            System.out.println("TEST 52 FAILED");
            authFailed++;

        } catch (SecurityException e) {
            System.out.println("TEST 52 PASSED: " + e.getMessage());
            authPassed++;
        }

        System.out.println("\n========== AUTHENTICATION SUMMARY ==========");
        System.out.println("Passed: " + authPassed);
        System.out.println("Failed: " + authFailed);
        System.out.println("Skipped: " + authSkipped);
        System.out.println("============================================");




        System.out.println("\n========== DASHBOARD TESTS 53-60 ==========");

        DashboardManager dashboard = new DashboardManager();

        int dashboardPassed = 0;
        int dashboardFailed = 0;

// TEST 53: Report Date
        try {
            System.out.println("\nTEST 53: Report Date");

            System.out.println("Report Date: "
                    + dashboard.getReportDate());

            if (dashboard.getReportDate() != null) {
                System.out.println("TEST 53 PASSED");
                dashboardPassed++;
            } else {
                System.out.println("TEST 53 FAILED");
                dashboardFailed++;
            }

        } catch (Exception e) {
            System.out.println("TEST 53 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

// TEST 54: Scheduled Check-ins
        try {
            System.out.println("\nTEST 54: Scheduled Check-ins");

            long count = dashboard.calculateCheckInsCount();

            System.out.println("Scheduled Check-ins: " + count);

            if (count >= 0) {
                System.out.println("TEST 54 PASSED");
                dashboardPassed++;
            } else {
                System.out.println("TEST 54 FAILED");
                dashboardFailed++;
            }

        } catch (Exception e) {
            System.out.println("TEST 54 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

// TEST 55: Scheduled Check-outs
        try {
            System.out.println("\nTEST 55: Scheduled Check-outs");

            long count = dashboard.calculateCheckOutsCount();

            System.out.println("Scheduled Check-outs: " + count);

            if (count >= 0) {
                System.out.println("TEST 55 PASSED");
                dashboardPassed++;
            } else {
                System.out.println("TEST 55 FAILED");
                dashboardFailed++;
            }

        } catch (Exception e) {
            System.out.println("TEST 55 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

// TEST 56: New Bookings
        try {
            System.out.println("\nTEST 56: New Bookings");

            long count = dashboard.calculateNewBookingsCount();

            System.out.println("New Bookings: " + count);

            if (count >= 0) {
                System.out.println("TEST 56 PASSED");
                dashboardPassed++;
            } else {
                System.out.println("TEST 56 FAILED");
                dashboardFailed++;
            }

        } catch (Exception e) {
            System.out.println("TEST 56 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

// TEST 57: Cancellations
        try {
            System.out.println("\nTEST 57: Cancellations");

            long count = dashboard.calculateCancellationsCount();

            System.out.println("Cancellations: " + count);

            if (count >= 0) {
                System.out.println("TEST 57 PASSED");
                dashboardPassed++;
            } else {
                System.out.println("TEST 57 FAILED");
                dashboardFailed++;
            }

        } catch (Exception e) {
            System.out.println("TEST 57 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

// TEST 58: Total Reservations
        try {
            System.out.println("\nTEST 58: Total Reservations");

            int total = dashboard.getTotalReservations();

            System.out.println("Total Reservations: " + total);

            if (total >= 0) {
                System.out.println("TEST 58 PASSED");
                dashboardPassed++;
            } else {
                System.out.println("TEST 58 FAILED");
                dashboardFailed++;
            }

        } catch (Exception e) {
            System.out.println("TEST 58 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

// TEST 59: Total Revenue
        try {
            System.out.println("\nTEST 59: Total Revenue");

            BigDecimal revenue =
                    dashboard.calculateTotalRevenue();

            System.out.println("Total Paid Invoice Amount: "
                    + revenue + " EGP");

            if (revenue != null
                    && revenue.compareTo(BigDecimal.ZERO) >= 0) {
                System.out.println("TEST 59 PASSED");
                dashboardPassed++;
            } else {
                System.out.println("TEST 59 FAILED");
                dashboardFailed++;
            }

        } catch (Exception e) {
            System.out.println("TEST 59 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

// TEST 60: Weekly Availability
        try {
            System.out.println("\nTEST 60: Weekly Availability");

            dashboard.displayWeeklyAvailability();

            System.out.println("TEST 60 PASSED: Method executed");
            dashboardPassed++;

        } catch (Exception e) {
            System.out.println("TEST 60 FAILED: " + e.getMessage());
            dashboardFailed++;
        }

        System.out.println("\n========== DASHBOARD SUMMARY ==========");
        System.out.println("Passed: " + dashboardPassed);
        System.out.println("Failed: " + dashboardFailed);
        System.out.println("=======================================");



        System.out.println("\n===== TESTING COMPLETED =====");



    }



}

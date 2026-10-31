package com.hotel;

import com.hotel.config.DatabaseConnection;
import com.hotel.enums.AccommodationType;
import com.hotel.model.*;
import com.hotel.repository.HibernateRepository;
import com.hotel.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        EventHall eventHall =
                new EventHall("Nile Towers" , "New Capital" , 100000 , 600);
        HibernateRepository r = new HibernateRepository();
//        r.save(eventHall);
//        r.deleteById(EventHall.class, 7);

//        // Test 1: Read event halls
//        List<EventHall> halls = repo.findAll(EventHall.class);
//
//        System.out.println("=== EVENT HALLS ===");
//        for (EventHall hall : halls) {
//            System.out.println(
//                    hall.getHallId() + " - " + hall.getHallName()
//            );
//        }

//        // Test 3: Read rooms with inheritance
//        List<Room> rooms = repo.findAll(Room.class);
//
//        System.out.println("=== ROOMS ===");
//
//        for (Room room : rooms) {
//            System.out.println(
//                    room.getRoomId() + " - " +
//                            room.getRoomNumber() + " - " +
//                            room.getClass().getSimpleName()
//            );
//        }


//        Guest guest = r.findById(Guest.class, 83);
//        Room room = r.findById(Room.class, 5);
//
//        Accommodation accommodation = r.findById(
//                Accommodation.class,
//                AccommodationType.BREAKFAST
//        );
//
//        Reservation reservation = new Reservation(
//                LocalDate.now().plusDays(5),
//                LocalDate.now().plusDays(8),
//                accommodation,
//                guest,
//                room
//        );
//
////        r.save(reservation);
//        System.out.println("Before payment: " + reservation.getStatus());
//
//// Pay the 20% deposit
//        reservation.payDeposit();
//
//// Save the updated status in MySQL
//        r.update(reservation);


        Reservation reservation =
                r.findById(Reservation.class, 127);

        if (reservation == null) {
            throw new IllegalStateException("Reservation not found");
        }

        System.out.println("Before: " + reservation.getCancellationDate());

//        reservation.payDeposit();

//        r.update(reservation);

//        System.out.println("After: " + reservation.getStatus());
//        System.out.println("Deposit Paid: " + reservation.isDepositPaid());

//        reservation.cancel();
//        r.update(reservation);


        Accommodation accommodation = r.findById(
                Accommodation.class,
                AccommodationType.FULL_BOARD
        );

        accommodation.setPricePerNight(2500);
        r.update(accommodation);


        System.out.println("After: " + reservation.getCancellationDate());




    }

    }

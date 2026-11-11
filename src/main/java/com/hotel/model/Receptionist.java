package com.hotel.model;

import com.hotel.enums.ReservationStatus;
import com.hotel.enums.RoomStatus;
import com.hotel.manager.ReservationManager;
import com.hotel.manager.RoomManager;
import com.hotel.utils.PasswordHasher;
import com.hotel.utils.Validation;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "receptionists")
@PrimaryKeyJoinColumn(name = "user_id")
public class Receptionist extends User {

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "shift", length = 50)
    private String shift;

    // Required by Hibernate
    protected Receptionist() {
        super();
    }

    public Receptionist(String nationalID, int userID,
                        String firstName, String lastName,
                        String gender, int age,
                        String email, String passwordHash,
                        String phone, String shift) {

        super(nationalID, userID, firstName, lastName,
                gender, age, email, passwordHash,
                "RECEPTIONIST");

        if (!isValidPhone(phone))
            throw new IllegalArgumentException(
                    "Invalid phone number");

        if (shift == null || shift.isBlank())
            throw new IllegalArgumentException(
                    "Shift cannot be empty");

        this.phone = phone.trim();
        this.shift = shift.trim();
    }

    // Creates a Receptionist object for registration.
    // Does NOT save to the database.
    public static Receptionist createForRegistration(
            String nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword,
            String phone,
            String shift) {
        if (!isNationalId(nationalID))
            throw new IllegalArgumentException(
                    "Invalid National ID (must be 14 digits)\n"
            );


        if (!Validation.isValidEmail(email))
            throw new IllegalArgumentException(
                    "Invalid email format");

        List<String> problems =
                Validation.passwordProblems(rawPassword, email);

        if (!problems.isEmpty())
            throw new IllegalArgumentException(
                    "Weak password: "
                            + String.join("; ", problems));

        if (firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank())
            throw new IllegalArgumentException(
                    "Name cannot be empty");
        if (gender == null ||
                (!gender.equalsIgnoreCase("Male")
                        && !gender.equalsIgnoreCase("Female"))) {

            throw new IllegalArgumentException(
                    "Gender must be Male or Female"
            );
        }

        if (age <= 0 || age < 18)
            throw new IllegalArgumentException(
                    "Age must be positive and greater than or equal 18");


        if (!isValidPhone(phone))
            throw new IllegalArgumentException(
                    "Invalid phone number");

        if (shift == null || shift.isBlank())
            throw new IllegalArgumentException(
                    "Shift cannot be empty");

        String normalizedEmail =
                email.trim().toLowerCase();

        String hash = PasswordHasher.hash(rawPassword);

        return new Receptionist(
                nationalID,
                0,
                firstName,
                lastName,
                gender,
                age,
                normalizedEmail,
                hash,
                phone,
                shift
        );
    }

    public String getPhone() {
        return phone;
    }

    public String getShift() {
        return shift;
    }

    @Override
    public void displayProfile() {
        super.displayProfile();
        System.out.println("Phone: " + phone);
        System.out.println("Shift: " + shift);
    }

    private void requireLoggedIn() {
        if (!isLoggedIn())
            throw new IllegalStateException(
                    "Receptionist must be logged in");
    }

    public void viewGuestData(Guest guest) {
        requireLoggedIn();

        if (guest == null)
            throw new IllegalArgumentException(
                    "Guest is required");

        guest.displayProfile();
    }


    public RoomStatus viewRoomStatus(Room room) {
        requireLoggedIn();

        if (room == null)
            throw new IllegalArgumentException(
                    "Room is required");

        return room.getStatus();
    }


    public void checkInGuest(Reservation reservation) {
        requireLoggedIn();

        if (reservation == null || !reservation.isSaved())
            throw new IllegalArgumentException(
                    "A saved reservation is required");

        ReservationManager reservationManager =
                new ReservationManager();

        RoomManager roomManager = new RoomManager();

        Reservation savedReservation =
                reservationManager.findReservationById(
                        reservation.getReservationId());

        if (savedReservation == null)
            throw new IllegalArgumentException(
                    "Reservation not found");

        Room room = roomManager.findRoomById(
                savedReservation.getRoom().getRoomId());

        if (room == null || !room.isAvailable())
            throw new IllegalStateException(
                    "Room is not available for check-in");

        savedReservation.checkIn();

        reservationManager.updateReservation(savedReservation);

        roomManager.updateRoomStatus(
                room.getRoomId(),
                RoomStatus.OCCUPIED);
    }




    public void checkOutGuest(Reservation reservation) {
        requireLoggedIn();

        if (reservation == null || !reservation.isSaved())
            throw new IllegalArgumentException(
                    "A saved reservation is required");

        ReservationManager reservationManager =
                new ReservationManager();

        RoomManager roomManager = new RoomManager();

        // Load the latest reservation from the database
        Reservation savedReservation =
                reservationManager.findReservationById(
                        reservation.getReservationId());

        if (savedReservation == null)
            throw new IllegalArgumentException(
                    "Reservation not found");

        // Load the latest room status
        Room room = roomManager.findRoomById(
                savedReservation.getRoom().getRoomId());

        if (room == null || room.getStatus() != RoomStatus.OCCUPIED)
            throw new IllegalStateException(
                    "Room is not currently occupied");

        // Validate reservation status and check-out date
        savedReservation.checkOut();

        // Save the new reservation status
        reservationManager.updateReservation(savedReservation);

        // Mark the room as needing cleaning
        roomManager.updateRoomStatus(
                room.getRoomId(),
                RoomStatus.UNDER_CLEANING);

    }


    private static boolean isValidPhone(String p) {
        return p != null
                && p.trim().matches("^\\+?[0-9]{11}$");
    }
}
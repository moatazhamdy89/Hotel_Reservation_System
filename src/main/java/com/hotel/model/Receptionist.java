package com.hotel.model;

import com.hotel.enums.ReservationStatus;
import com.hotel.enums.RoomStatus;
import com.hotel.repository.HibernateRepository;
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

    public Receptionist(int nationalID, int userID,
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
            int nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword,
            String phone,
            String shift) {

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

        if (age <= 0)
            throw new IllegalArgumentException(
                    "Age must be positive");

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

    public void updateRoomStatus(Room room,
                                 RoomStatus newStatus) {
        requireLoggedIn();

        if (room == null)
            throw new IllegalArgumentException(
                    "Room is required");

        if (newStatus == null)
            throw new IllegalArgumentException(
                    "Status is required");

        HibernateRepository repository =
                new HibernateRepository();

        repository.withTransaction(session -> {

            Room savedRoom =
                    session.find(Room.class, room.getRoomId());

            if (savedRoom == null)
                throw new IllegalStateException(
                        "Room " + room.getRoomId()
                                + " not found in database");

            savedRoom.setStatus(newStatus);

            return null;
        });

        // Update local object only after successful commit
        room.setStatus(newStatus);
    }

    public RoomStatus viewRoomStatus(Room room) {
        requireLoggedIn();

        if (room == null)
            throw new IllegalArgumentException(
                    "Room is required");

        return room.status;
    }

    public void checkInGuest(Reservation reservation) {
        requireLoggedIn();

        if (reservation == null)
            throw new IllegalArgumentException(
                    "Reservation is required");

        if (reservation.getStatus()
                != ReservationStatus.CONFIRMED
                || !reservation.isDepositPaid())
            throw new IllegalStateException(
                    "Only a CONFIRMED reservation with "
                            + "a paid deposit can check in");

        Room room = reservation.getRoom();

        if (!room.isAvailable())
            throw new IllegalStateException(
                    "Room is not available for check-in");

        updateRoomStatus(room, RoomStatus.OCCUPIED);
    }

    /**
     * Room invoice creation and final payment belong to Billing.
     * This method only changes the room status to UNDER_CLEANING.
     */
    public void checkOutGuest(Reservation reservation) {
        requireLoggedIn();

        if (reservation == null)
            throw new IllegalArgumentException(
                    "Reservation is required");

        if (reservation.getStatus()
                != ReservationStatus.CONFIRMED)
            throw new IllegalStateException(
                    "Only a CONFIRMED reservation can check out");

        Room room = reservation.getRoom();

        if (room.status != RoomStatus.OCCUPIED)
            throw new IllegalStateException(
                    "Room is not currently occupied");

        updateRoomStatus(room, RoomStatus.UNDER_CLEANING);
    }

    private static boolean isValidPhone(String p) {
        return p != null
                && p.trim().matches("^\\+?[0-9]{8,15}$");
    }
}

package com.hotel.model;

import com.hotel.manager.UserManager;
import com.hotel.manager.RoomManager;
import com.hotel.manager.ReservationManager;
import com.hotel.manager.EventBookingManager;
import com.hotel.manager.InvoiceManager;
import com.hotel.manager.DashboardManager;

import com.hotel.utils.PasswordHasher;
import com.hotel.utils.Validation;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {

    // Required by Hibernate
    protected Admin() {
        super();
    }

    // Constructor
    public Admin(
            String nationalID,
            int userID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String passwordHash) {

        super(
                nationalID,
                userID,
                firstName,
                lastName,
                gender,
                age,
                email,
                passwordHash,
                "ADMIN"
        );
    }

    // Registration factory: creates object only
    public static Admin createForRegistration(
            String nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword) {
        if (!isNationalId(nationalID))
            throw new IllegalArgumentException(
                    "Invalid National ID (must be 14 digits)\n"
            );


        if (!Validation.isValidEmail(email)) {
            throw new IllegalArgumentException(
                    "Invalid email format");
        }

        List<String> problems =
                Validation.passwordProblems(
                        rawPassword, email);

        if (!problems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Weak password: "
                            + String.join("; ", problems));
        }

        if (firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty");
        }
        if (gender == null ||
                (!gender.equalsIgnoreCase("Male")
                        && !gender.equalsIgnoreCase("Female"))) {

            throw new IllegalArgumentException(
                    "Gender must be Male or Female"
            );
        }

        if (age <= 0 || age < 18) {
            throw new IllegalArgumentException(
                    "Age must be positive and greater than or equal 18");
        }

        String normalizedEmail =
                email.trim().toLowerCase(java.util.Locale.ROOT);

        String passwordHash =
                PasswordHasher.hash(rawPassword);

        return new Admin(
                nationalID,
                0,
                firstName,
                lastName,
                gender,
                age,
                normalizedEmail,
                passwordHash
        );
    }

    // ---------------------------------
    // Authorization
    // ---------------------------------

    private void requireLoggedIn() {
        if (!isLoggedIn()) {
            throw new SecurityException(
                    "Admin must be logged in");
        }
    }

    // ---------------------------------
    // User Management
    // ---------------------------------

    public UserManager manageUsers() {
        requireLoggedIn();
        return new UserManager();
    }

    // ---------------------------------
    // Room Management
    // ---------------------------------

    public RoomManager manageRooms() {
        requireLoggedIn();
        return new RoomManager();
    }

    // ---------------------------------
    // Reservation Management
    // ---------------------------------

    public ReservationManager manageReservations() {
        requireLoggedIn();
        return new ReservationManager();
    }

    // ---------------------------------
    // Event Booking Management
    // ---------------------------------

    public EventBookingManager manageEvent() {
        requireLoggedIn();
        return new EventBookingManager();
    }

    // ---------------------------------
    // Invoice Management
    // ---------------------------------

    public InvoiceManager manageInvoice() {
        requireLoggedIn();
        return new InvoiceManager();
    }

    // ---------------------------------
    // Dashboard Management
    // ---------------------------------

    public DashboardManager manageDashboard(
            DashboardManager dashboardManager) {

        requireLoggedIn();

        if (dashboardManager == null) {
            throw new IllegalArgumentException(
                    "DashboardManager is required");
        }

        return dashboardManager;
    }

    // ---------------------------------
    // View Guest Data
    // ---------------------------------

    public void viewGuestData(Guest guest) {
        requireLoggedIn();

        if (guest == null) {
            throw new IllegalArgumentException(
                    "Guest is required");
        }

        guest.displayProfile();
    }

    // ---------------------------------
    // View Reports
    // ---------------------------------

    public void viewReports(
            DashboardManager dashboardManager) {

        requireLoggedIn();

        if (dashboardManager == null) {
            throw new IllegalArgumentException(
                    "DashboardManager is required");
        }

        dashboardManager.displayDashboard();
    }

    // ---------------------------------
    // View Admin Profile
    // ---------------------------------

    public void viewProfile() {
        requireLoggedIn();
        displayProfile();
    }
}

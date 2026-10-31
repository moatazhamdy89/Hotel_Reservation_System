package com.hotel.model;

import com.hotel.utils.PasswordHasher;
import com.hotel.utils.Validation;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "guests")
@PrimaryKeyJoinColumn(name = "user_id")
public class Guest extends User {

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "phone", length = 20)
    private String phone;

    @OneToOne(
            mappedBy = "guest",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private LoyaltyProgram loyaltyProgram;

    // Required by Hibernate
    protected Guest() {
        super();
    }

    // Constructor for creating a Guest object
    public Guest(int nationalID, int userID,
                 String firstName, String lastName,
                 String gender, int age,
                 String email, String passwordHash,
                 String nationality, String phone,
                 int loyaltyPoints) {

        super(nationalID, userID, firstName, lastName,
                gender, age, email, passwordHash, "GUEST");

        if (isBlank(nationality)) {
            throw new IllegalArgumentException(
                    "Nationality cannot be empty");
        }

        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException(
                    "Invalid phone number");
        }

        this.nationality = nationality.trim();
        this.phone = phone.trim();

        this.loyaltyProgram = new LoyaltyProgram(loyaltyPoints);
        this.loyaltyProgram.setGuest(this);
    }

    // Validates input, hashes password and creates a Guest.
    // Does NOT save to the database.
    public static Guest createForRegistration(
            int nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword,
            String nationality,
            String phone) {

        if (!Validation.isValidEmail(email)) {
            throw new IllegalArgumentException(
                    "Invalid email format");
        }

        List<String> problems =
                Validation.passwordProblems(rawPassword, email);

        if (!problems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Weak password: "
                            + String.join("; ", problems));
        }

        if (isBlank(firstName) || isBlank(lastName)) {
            throw new IllegalArgumentException(
                    "Name cannot be empty");
        }

        if (age <= 0) {
            throw new IllegalArgumentException(
                    "Age must be positive");
        }

        if (isBlank(nationality)) {
            throw new IllegalArgumentException(
                    "Nationality cannot be empty");
        }

        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException(
                    "Invalid phone number");
        }

        String normalizedEmail = email.trim().toLowerCase();
        String hash = PasswordHasher.hash(rawPassword);

        return new Guest(
                nationalID,
                0,
                firstName,
                lastName,
                gender,
                age,
                normalizedEmail,
                hash,
                nationality,
                phone,
                0
        );
    }

    public String getNationality() {
        return nationality;
    }

    public String getPhone() {
        return phone;
    }

    public LoyaltyProgram getLoyaltyProgram() {
        return loyaltyProgram;
    }

    @Override
    public void displayProfile() {
        super.displayProfile();
        System.out.println("Nationality: " + nationality);
        System.out.println("Phone: " + phone);
    }

    public void viewProfile() {
        displayProfile();
    }

    public void viewPoints() {
        System.out.println(
                "Points: " + loyaltyProgram.getPoints());

        System.out.println(
                "Level: "
                        + loyaltyProgram.getMembershipLevel());

        System.out.println(
                "Discount: "
                        + (int) (
                        loyaltyProgram.calculateDiscount() * 100
                ) + "%");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean isValidPhone(String p) {
        return p != null
                && p.trim().matches("^\\+?[0-9]{8,15}$");
    }
}
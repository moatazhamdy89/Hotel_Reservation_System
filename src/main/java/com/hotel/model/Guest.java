package com.hotel.model;

import com.hotel.dao.UserDAO;
import com.hotel.utils.PasswordHasher;
import com.hotel.utils.Validation;

import java.sql.SQLException;
import java.util.List;

public class Guest extends User {
    private String nationality;
    private String phone;
    private final LoyaltyProgram loyaltyProgram;   // composition: created by and owned by the Guest

    public Guest(int nationalID, int userID, String firstName, String lastName, String gender,
                 int age, String email, String passwordHash, String nationality, String phone,
                 int loyaltyPoints) {
        super(nationalID, userID, firstName, lastName, gender, age, email, passwordHash, "GUEST");
        if (isBlank(nationality)) throw new IllegalArgumentException("Nationality cannot be empty");
        if (!isValidPhone(phone)) throw new IllegalArgumentException("Invalid phone number");
        this.nationality = nationality.trim();
        this.phone = phone.trim();
        this.loyaltyProgram = new LoyaltyProgram(loyaltyPoints);   // never passed in from outside
    }

    /** Self-registration. Static because no Guest object exists before registering. */
    public static Guest register(int nationalID, String firstName, String lastName, String gender,
                                 int age, String email, String rawPassword,
                                 String nationality, String phone) {
        if (!Validation.isValidEmail(email))
            throw new IllegalArgumentException("Invalid email format");
        List<String> problems = Validation.passwordProblems(rawPassword, email);
        if (!problems.isEmpty())
            throw new IllegalArgumentException("Weak password: " + String.join("; ", problems));
        if (isBlank(firstName) || isBlank(lastName))
            throw new IllegalArgumentException("Name cannot be empty");
        if (age <= 0) throw new IllegalArgumentException("Age must be positive");
        if (isBlank(nationality)) throw new IllegalArgumentException("Nationality cannot be empty");
        if (!isValidPhone(phone)) throw new IllegalArgumentException("Invalid phone number");

        String normalizedEmail = email.trim().toLowerCase();
        UserDAO dao = new UserDAO();
        try {
            if (dao.emailExists(normalizedEmail))
                throw new IllegalArgumentException("Email is already registered");

            String hash = PasswordHasher.hash(rawPassword);
            int newId = dao.insertGuest(nationalID, firstName.trim(), lastName.trim(), gender, age,
                    normalizedEmail, hash, nationality.trim(), phone.trim());
            return new Guest(nationalID, newId, firstName, lastName, gender, age,
                    normalizedEmail, hash, nationality, phone, 0);
        } catch (SQLException e) {
            throw new IllegalStateException("Registration failed: " + e.getMessage(), e);
        }
    }

    public String getNationality()            { return nationality; }
    public String getPhone()                  { return phone; }
    public LoyaltyProgram getLoyaltyProgram() { return loyaltyProgram; }

    @Override
    public void displayProfile() {
        super.displayProfile();               // reuse the User part, then add Guest details
        System.out.println("Nationality: " + nationality);
        System.out.println("Phone: " + phone);
    }

    public void viewProfile() {
        displayProfile();
    }

    public void viewPoints() {
        System.out.println("Points: " + loyaltyProgram.getPoints());
        System.out.println("Level: " + loyaltyProgram.getMembershipLevel());
        System.out.println("Discount: " + (int) (loyaltyProgram.calculateDiscount() * 100) + "%");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static boolean isValidPhone(String p) {
        return p != null && p.trim().matches("^\\+?[0-9]{8,15}$");
    }
}

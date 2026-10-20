package com.hotel.model;

import com.hotel.dao.UserDAO;
import com.hotel.utils.PasswordHasher;
import com.hotel.utils.Validation;

import java.sql.SQLException;

public abstract class User {
    protected int nationalID;
    protected int userID;
    protected String firstName;
    protected String lastName;
    protected String gender;
    protected int age;
    protected String email;
    protected String password;   // holds a bcrypt HASH, never plaintext
    protected String role;

    private boolean loggedIn = false;

    protected User(int nationalID, int userID, String firstName, String lastName,
                   String gender, int age, String email, String password, String role) {
        if (isBlank(firstName) || isBlank(lastName))
            throw new IllegalArgumentException("Name cannot be empty");
        if (!Validation.isValidEmail(email))
            throw new IllegalArgumentException("Invalid email format");
        if (age <= 0)
            throw new IllegalArgumentException("Age must be positive");
        if (isBlank(password))   // this is a hash, so only "not empty" is checked here
            throw new IllegalArgumentException("Password hash cannot be empty");
        if (isBlank(role))
            throw new IllegalArgumentException("Role cannot be empty");

        this.nationalID = nationalID;
        this.userID = userID;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.gender = gender;
        this.age = age;
        this.email = email.trim().toLowerCase();
        this.password = password;
        this.role = role;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ---------- Getters (no getPassword on purpose) ----------
    public int getNationalID()   { return nationalID; }
    public int getUserID()       { return userID; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getGender()    { return gender; }
    public int getAge()          { return age; }
    public String getEmail()     { return email; }
    public String getRole()      { return role; }
    public boolean isLoggedIn()  { return loggedIn; }

    // ---------- Authentication ----------
    public boolean login(String email, String rawPassword) {
        if (!Validation.isValidEmail(email) || rawPassword == null || rawPassword.isEmpty())
            return false;

        String normalized = email.trim().toLowerCase();
        if (!this.email.equals(normalized)) return false;   // this object must be that account

        try {
            String storedHash = new UserDAO().findPasswordHashByEmail(normalized);
            loggedIn = PasswordHasher.verify(rawPassword, storedHash);
        } catch (SQLException e) {
            loggedIn = false;                               // a DB failure never counts as success
        }
        return loggedIn;
    }
    public void logout() {
        loggedIn = false;
    }

    // ---------- Display ----------
    public void displayProfile() {
        System.out.println("ID: " + userID);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Gender: " + gender + " | Age: " + age);
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);
    }
}